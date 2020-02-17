package org.abigballofmud.gmall.realtime.utils

import java.util.Properties

import com.typesafe.scalalogging.Logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis

import scala.collection.mutable

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/16 23:27
 * @since 1.0
 */
object KafkaUtil {

  private val log = Logger(LoggerFactory.getLogger(KafkaUtil.getClass))

  private val properties: Properties = PropertiesUtil.load("config.properties")

  val kafka_broker_list: String = properties.getProperty("kafka.broker.list")

  // kafka消费者配置
  val kafkaParams_auto = Map(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> kafka_broker_list,
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer],
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> classOf[StringSerializer],
    // 消费组
    "group.id" -> "gmall_consumer_group",
    // 如果没有初始化offset或者当前的偏移量不存在任何服务器上，可使用这个配置
    "auto.offset.reset" -> "latest",
    // true: 这个消费者的offset会在后台自动提交，但是kafka宕机容易丢失数据
    // false: 需要手动维护kafka的offset
    "enable.auto.commit" -> (true: java.lang.Boolean)
  )

  val kafkaParams: Map[String, Object] = Map[String, Object](
    "bootstrap.servers" -> kafka_broker_list,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "gmall_consumer_group",
    "auto.offset.reset" -> "none",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  // 创建DStream，返回接收到的输入数据
  // LocationStrategies：根据给定的主题和集群地址创建consumer
  // LocationStrategies.PreferConsistent：持续的在所有Executor之间分配分区
  // ConsumerStrategies：选择如何在Driver和Executor上创建和配置Kafka Consumer
  // ConsumerStrategies.Subscribe：订阅一系列主题

  def getKafkaStream(topic: String, ssc: StreamingContext): InputDStream[ConsumerRecord[String, String]] = {
    val dStream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Array(topic), kafkaParams_auto)
    )
    dStream
  }

  def getTopicPartitions(topic: String, partitionNum: Int): Map[String, Int] = {
    val topicPartitions: Map[String, Int] = Map[String, Int](topic -> partitionNum)
    topicPartitions
  }

  /**
   * 创建kafka流
   *
   * @param ssc StreamingContext
   * @return InputDStream
   */
  def createKafkaStream(ssc: StreamingContext,
                        topic: String,
                        partitionNum: Int): InputDStream[ConsumerRecord[String, String]] = {
    val offsets: Map[TopicPartition, Long] = getOffsets(getTopicPartitions(topic, partitionNum))

    // 创建kafka stream
    val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Assign[String, String](offsets.keys.toList, kafkaParams, offsets)
    )
    stream
  }

  /**
   * 从redis中获取offset信息
   *
   * @return Map[TopicPartition, Long]
   */
  def getOffsets(topicPartitions: Map[String, Int]): Map[TopicPartition, Long] = {
    val jedis: Jedis = RedisUtil.getResource

    // 设置每个分区起始的offset
    val offsets: mutable.Map[TopicPartition, Long] = mutable.Map[TopicPartition, Long]()

    topicPartitions.foreach { it =>
      val topic: String = it._1
      val partitions: Int = it._2
      // 遍历分区，设置每个topic下对应partition的offset
      for (partition <- 0 until partitions) {
        val topicPartitionKey: String = topic + ":" + partition
        var lastOffset = 0L
        val lastSavedOffset: String = jedis.get(topicPartitionKey)

        if (null != lastSavedOffset) {
          try {
            lastOffset = lastSavedOffset.toLong
          } catch {
            case e: Exception =>
              log.error("get last saved offset error", e)
              System.exit(1)
          }
        }
        log.info("from redis topic: {}, partition: {}, last offset: {}", topic, partition, lastOffset)

        // 添加
        offsets += (new TopicPartition(topic, partition) -> lastOffset)
      }
    }

    RedisUtil.recycleResource(jedis)

    offsets.toMap
  }


}
