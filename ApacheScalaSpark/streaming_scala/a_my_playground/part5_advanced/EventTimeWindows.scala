package a_my_playground.part5_advanced

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType, TimestampType}

object EventTimeWindows {

  val spark = SparkSession.builder()
    .appName("eventTimeWindows")
    .master("local[2]")
    .getOrCreate()


  val onlinePurchaseSchema: StructType = StructType(Array(
      StructField("id",StringType),
      StructField("time",TimestampType),
      StructField("item",StringType),
      StructField("quantity",IntegerType)
    ))


  def readPurchasesFromSocket() = spark.readStream
    .format("socket")
    .option("host","localhost")
    .option("port",12345)
    .load()
    .select(from_json(col("value"),onlinePurchaseSchema).as("purchase"))
    .selectExpr("purchase.*")


  def aggregatePurchasesBySlidingWindows(): Unit ={
    val purchasesDF = readPurchasesFromSocket()

    val windowsByDay = purchasesDF
      .groupBy(window(col("time"),"1 day","1 hour").as("time"))//struct column: has fields {start, end}
      .agg(sum("quantity").as("totalQuantity"))
      .select(
        col("time").getField("start").as("start"),
        col("time").getField("end").as("end"),
        col("totalQuantity")
      )

    windowsByDay.writeStream
      .format("console")
      .outputMode("complete")
      .start()
      .awaitTermination()

  }

  def aggregatePurchasesByTumblingWindows(): Unit ={
    val purchasesDF = readPurchasesFromSocket()

    val windowsByDay = purchasesDF
      .groupBy(window(col("time"),"1 day").as("time"))//tumbling window: sliding duration === window duration
      .agg(sum("quantity").as("totalQuantity"))
      .select(
        col("time").getField("start").as("start"),
        col("time").getField("end").as("end"),
        col("totalQuantity")
      )

    windowsByDay.writeStream
      .format("console")
      .outputMode("complete")
      .start()
      .awaitTermination()

  }

  /**
   * Exercises
   * 1) Show the best selling product of every day, +quantity sold.
   * 2) Show the best selling product of every 24 hours, updated every hour.
   */

    def exercise1(): Unit ={
      val purchasesDF = readPurchasesFromSocket()

      val windowsByDay = purchasesDF
        .groupBy(window(col("time"),"1 day").as("time"))//tumbling window: sliding duration === window duration
        .agg(max("quantity").as("max purchase"))
        .select(
          col("time").getField("start").as("start"),
          col("time").getField("end").as("end"),
          col("max purchase")
        )

      windowsByDay.writeStream
        .format("console")
        .outputMode("complete")
        .start()
        .awaitTermination()

    }

  def exercise2(): Unit ={
    val purchasesDF = readPurchasesFromSocket()

    val windowsByDay = purchasesDF
      .groupBy(window(col("time"),"1 day","1 hour").as("time"))//struct column: has fields {start, end}
      .agg(max("quantity").as("max purchase"))
      .select(
        col("time").getField("start").as("start"),
        col("time").getField("end").as("end"),
        col("max purchase")
      )

    windowsByDay.writeStream
      .format("console")
      .outputMode("complete")
      .start()
      .awaitTermination()
  }



  def main(args: Array[String]): Unit = {
    //aggregatePurchasesBySlidingWindows()
    //exercise1()
    exercise2()
  }

}
