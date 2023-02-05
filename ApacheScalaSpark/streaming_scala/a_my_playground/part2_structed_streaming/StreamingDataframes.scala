package a_my_playground.part2_structed_streaming

import common.stocksSchema
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.concurrent.duration._

object StreamingDataframes {

  val spark: SparkSession = SparkSession.builder()
    .appName("Our first Streams")
    .master("local[2]")
    .getOrCreate()

  def readFromSocket(): Unit = {

    //we created data frame conecting spark to a socket
    //read a DF
    val lines: DataFrame = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",12345)
      .load()


    //between read and consume we can do transformations
    val upperCaseLines = lines.select(upper(col("value")))


    //tell between static and streaming DF
    println(upperCaseLines.isStreaming)

    //we started an action in the dataframe outputing each line in the console
    // consuming a DF
    val query = upperCaseLines.writeStream
      .format("console")
      .outputMode("append")
      .start()

    //wait for the stream to finish
    query.awaitTermination()
  }

  def readFromFiles(): Unit ={
    val stocksDF = spark.readStream
      .format("csv")
      .option("header","false")
      .option("dateFormat","MMM d yyyy")
      .schema(stocksSchema)
      .load("src/main/resources/data/stocks")

    stocksDF.writeStream
      .format("console")
      .outputMode("append")
      .start()
      .awaitTermination()

  }

  def demoTriggers(): Unit ={
    val lines: DataFrame = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",12345)
      .load()

    //write the lines DF at a certain trigger
    lines.writeStream
      .format("console")
      .outputMode("append")
      .trigger(
        //Trigger.ProcessingTime(2.seconds) //every two seconds runs the query
        //Trigger.Once() //single batch then terminate
      Trigger.Continuous(2.seconds) //experimental, every 2 seg creates a batch with whatever you have, include nothing
      )

      .start()
      .awaitTermination()
  }

  def main(args: Array[String]): Unit = {

    //readFromSocket()
    //readFromFiles()
    //demoTriggers()

    /**
     * --Takeaways--
     *  Streaming DF can be read via spark session, but you need a schema:  spark.readSteam
     *  Streaming DF have identical API's to non-streaming DF:  randomDF.select(col("col"))
     *  Streaming DF can be written  via a call to start : val writer = query.writeStream
     */

  }
}
