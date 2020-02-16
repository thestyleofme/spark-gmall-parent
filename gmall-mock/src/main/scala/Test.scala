import java.time.{LocalDateTime, ZoneId, ZoneOffset}
import java.util.Date

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/02/16 15:13
 * @since 1.0
 */
object Test {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().appName("test").master("local[2]").getOrCreate()
//    val data: Seq[Date] = Array(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault).toInstant),
//      Date.from(LocalDateTime.now().minusDays(1L).atZone(ZoneId.systemDefault).toInstant)).toSeq
    val data: Seq[LocalDateTime] = Array(LocalDateTime.now(), LocalDateTime.now().minusDays(1L)).toSeq
    val res: RDD[Row] = spark.sparkContext.parallelize(data)
      .map(a => {
//        Row(new java.sql.Date(a.getTime))
        Row(new java.sql.Timestamp(a.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli))
      })
//    val structType: StructType = StructType(List(StructField("date", DataTypes.DateType, nullable = true)))
    val structType: StructType = StructType(List(StructField("date", DataTypes.TimestampType, nullable = true)))
    val df: DataFrame = spark.createDataFrame(res, structType)
    df.show(false)
  }

}
