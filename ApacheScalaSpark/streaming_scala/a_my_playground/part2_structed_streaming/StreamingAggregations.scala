package a_my_playground.part2_structed_streaming

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame, SparkSession}

object StreamingAggregations {

  val spark: SparkSession = SparkSession.builder()
    .appName("Streaming Aggregations")
    .master("local[2]")
    .getOrCreate()


  def streamingCount()={

    val lines = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",12345)
      .load()

    val lineCount:DataFrame = lines.selectExpr("count(*) as lineCount")

    //aggregations with distinct are not supported
    //otherwise Spark will need to keep track on EVERYTHING


    lineCount.writeStream
      .format("console")
      .outputMode("complete") //append and update not supported on aggregations without watermark
      .start()
      .awaitTermination()

  }

  def numericalAggregations(aggFunction: Column => Column): Unit ={

    val lines = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",12345)
      .load()

    //aggregate here
    val numbers = lines.select(col("value").cast("integer").as("Number"))
    val sumDF = numbers.select(aggFunction(col("Number")).as("agg_so_far"))

    sumDF.writeStream
      .format("console")
      .outputMode("complete")
      .start()
      .awaitTermination()

  }


  def groupNames(): Unit ={

    val lines = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",12345)
      .load()

    val names = lines
      .select(col("value").as("name"))
      .groupBy(col("name")) //returns relational grouped dataset
      .count() //returns the number of rows

    names.writeStream.format("console").outputMode("complete").start().awaitTermination()


  }

  def main(args: Array[String]): Unit = {

    //streamingCount()

    //the input is numerical aggregation functions, sum, mean, stddev ...
    //numericalAggregations(mean)

    //groupNames()

    /**
     *To keep in mind
     * -aggregations work at micro-batch level
     * -the append output mode not supported without watermarks
     * -some aggregations are not supported , e.g sorting, distinct, chained aggregations
     *
     */

  }
}
