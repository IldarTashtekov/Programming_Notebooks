package a_my_playground.part4_integrations

import common.carsSchema
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object Kafka_StrucutredStreaming {

  val spark = SparkSession.builder().appName("Integrading Kafka").master("local[2]").getOrCreate()


  def readFromKafka(): Unit ={

    val kafkaDF:DataFrame = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers","localhost;9092")
      .option("subscribe","rockthejvm")
      .load()



    kafkaDF
      .select(col("topic"),expr("cast(value as string) as actualValue")) //cast bianry to string
      .writeStream
      .format("console")
      .outputMode("append")
      .start()
      .awaitTermination()
  }

  def writeKafka(): Unit ={
    val carsDF = spark.readStream
      .schema(carsSchema)
      .json("src/main/resources/data/cars")

    val carsKafkaDF = carsDF.selectExpr("upper(Name) as key","Name as value")

    carsKafkaDF.writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers","localhost;9092")
      .option("topic","rockthejvm")
      .option("checkpointLocation","checkpoints") //without checkpoints the writing to Kafka will fail
      .start()
      .awaitTermination()

  }

  def main(args: Array[String]): Unit = {

    readFromKafka()
  }
}
