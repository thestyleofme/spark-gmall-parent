package org.abigballofmud.gmall.realtime.app

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.Properties

import com.google.gson.Gson
import com.typesafe.scalalogging.Logger
import org.abigballofmud.gmall.common.constants.GmallConstants
import org.abigballofmud.gmall.realtime.model.StartupLog
import org.abigballofmud.gmall.realtime.utils.{KafkaUtil, PropertiesUtil, RedisUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.slf4j.LoggerFactory
import redis.clients.jedis.{Jedis, Pipeline}

/**
 * <p>
 * dau -> daily active user
 * </p>
 *
 * @author isacc 2020/02/17 0:24
 * @since 1.0
 */
object DauApp {

  private val log = Logger(LoggerFactory.getLogger(DauApp.getClass))
  private val gson = new Gson()

  def main(args: Array[String]): Unit = {

    val properties: Properties = PropertiesUtil.load("config.properties")

    // redis
    val redisHost: String = properties.getProperty("redis.host")
    val redisPort: Int = Integer.valueOf(properties.getProperty("redis.port"))
    val redisPassword: String = properties.getProperty("redis.password")

    // 创建redis
    RedisUtil.makePool(redisHost, redisPort, redisPassword)

    val conf: SparkConf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("dau_app")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      // 加这个配置访问集群中的hive
      // https://stackoverflow.com/questions/39201409/how-to-query-data-stored-in-hive-table-using-sparksession-of-spark2
      .set("spark.sql.warehouse.dir", "/warehouse/tablespace/managed/hive")
      .set("metastore.catalog.default", "hive")
      .set("hive.metastore.uris", "thrift://hdsp001:9083")
      // spark调优 http://www.imooc.com/article/262032
      // 以下设置是为了减少hdfs小文件的产生
      //    https://www.cnblogs.com/dtmobile-ksw/p/11254294.html
      //    https://www.cnblogs.com/dtmobile-ksw/p/11293891.html
      .set("spark.sql.adaptive.enabled", "true")
      // 默认值64M
      .set("spark.sql.adaptive.shuffle.targetPostShuffleInputSize", "67108864")
      .set("spark.sql.adaptive.join.enabled", "true")
      // 20M
      .set("spark.sql.autoBroadcastJoinThreshold", "20971520")

    // 创建StreamingSession
    val spark: SparkSession = getOrCreateSparkSession(conf)
    val sc: SparkContext = spark.sparkContext
    // 创建StreamingContext
    val ssc = new StreamingContext(sc, Seconds(30))
    // 创建kafka流
    val kafkaStream: InputDStream[ConsumerRecord[String, String]] =
      KafkaUtil.createKafkaStream(ssc, GmallConstants.KAFKA_TOPIC_STARTUP, 1)
    // 开始消费，业务逻辑
    handler(kafkaStream, spark)
    // 启动并等待
    startAndWait(ssc)
  }

  /**
   * 消费处理业务逻辑
   *
   * @param stream InputDStream
   */
  def handler(stream: InputDStream[ConsumerRecord[String, String]], spark: SparkSession): Unit = {
    val startupLogDStream: DStream[StartupLog] = stream.map { record =>
      // 统计日活跃用户数量 daily active user
      // 转换类型 string -> case class 补充两个日期
      val startupLog: StartupLog = gson.fromJson(record.value(), classOf[StartupLog])
      startupLog.logType = GmallConstants.KAFKA_MESSAGE_TYPE_STARTUP
      val dateSpilt: Array[String] = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(startupLog.ts), ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"))
        .split(" ")
      startupLog.logDate = dateSpilt.apply(0)
      startupLog.logHour = dateSpilt.apply(1)
      startupLog
    }

    // 记录每天访问过的mid 形成一个清单
    val filteredDStream: DStream[StartupLog] = startupLogDStream.transform { rdd =>
      // 批次之间进行过滤
      log.info("过滤前：" + rdd.count())
      val jedis: Jedis = RedisUtil.getResource
      // driver 每个执行周期查询redis获得清单 通过广播变量发送到executor中
      val dauKey: String = "dau:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
      val dauSet: java.util.Set[String] = jedis.smembers(dauKey)
      val dauBC: Broadcast[java.util.Set[String]] = spark.sparkContext.broadcast(dauSet)
      RedisUtil.recycleResource(jedis)
      val filteredRDD: RDD[StartupLog] = rdd.filter { startupLog =>
        // executor
        !dauBC.value.contains(startupLog.mid)
      }
      log.info("过滤后： " + filteredRDD.count())
      filteredRDD
    }

    //  批次内进行去重: 按照key 进行分组 每组取一个
    val groupByMidDStream: DStream[(String, Iterable[StartupLog])] =
      filteredDStream.map(startupLog => (startupLog.mid, startupLog)).groupByKey()
    val realFilteredDStream: DStream[StartupLog] = groupByMidDStream.flatMap { case (_, startLogItr) =>
      startLogItr.take(1)
    }

    realFilteredDStream.cache()

    // 更新清单 存储到redis
    realFilteredDStream.foreachRDD { rdd =>
      // driver
      rdd.foreachPartition { startupLogItr =>
        val jedis: Jedis = RedisUtil.getResource
        // executor
        for (startupLog <- startupLogItr) {
          val dauKey: String = "dau:" + startupLog.logDate
          log.info("redis add {}:::{}", dauKey, startupLog.mid)
          jedis.sadd(dauKey, startupLog.mid)
        }
        jedis.close()
      }
    }

    // 更新消费的offset
    stream.foreachRDD { rdd =>
      val offsetRanges: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      recordOffset(offsetRanges, rdd.count())
    }

  }

  def recordOffset(offsetRanges: Array[OffsetRange], total: Long): Unit = {
    val jedis: Jedis = RedisUtil.getResource
    val pipeline: Pipeline = jedis.pipelined()
    // 会阻塞redis
    pipeline.multi()

    // 更新相关指标
    pipeline.incrBy(offsetRanges.apply(0).topic + ":total", total)

    // 更新offset
    offsetRanges.foreach { offsetRange =>
      log.info("save offsets, topic: {}, partition: {}, offset: {}",
        offsetRange.topic,
        offsetRange.partition,
        offsetRange.untilOffset)
      val topicPartitionKey: String = offsetRange.topic + ":" + offsetRange.partition
      pipeline.set(topicPartitionKey, offsetRange.untilOffset + "")
    }

    // 执行，释放
    pipeline.exec()
    pipeline.sync()
    pipeline.close()
    RedisUtil.recycleResource(jedis)
  }


  /**
   * 获取或创建SparkSession
   *
   * @return SparkSession
   */
  def getOrCreateSparkSession(conf: SparkConf): SparkSession = {
    val spark: SparkSession = SparkSession
      .builder()
      .config(conf)
      .enableHiveSupport()
      .getOrCreate()
    spark
  }

  /**
   * 启动并等待
   *
   * @param ssc StreamingContext
   */
  def startAndWait(ssc: StreamingContext): Unit = {
    // 启动
    ssc.start()
    // 等待
    ssc.awaitTermination()
  }

}
