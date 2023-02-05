package a_my_playground.part5_advanced

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, current_timestamp, sum, window,length}

object ProcessingWindow {

  val spark = SparkSession.builder()
    .appName("processingTimeWindows")
    .master("local[2]")
    .getOrCreate()

  def aggregateByProcessingTime(): Unit ={
    val linesDF = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",1234)
      .load()
      .select(col("value"),current_timestamp().as("processingTime"))
      .groupBy(window(col("processingTime"),"10 seconds").as("window"))
      .agg(sum(length(col("value")))) //counting characters every 10 seconds by processing time

    linesDF.writeStream
      .format("console")
      .outputMode("complete")
      .start()
      .awaitTermination()
  }

  def main(args: Array[String]): Unit = {

    aggregateByProcessingTime()

  }
}
