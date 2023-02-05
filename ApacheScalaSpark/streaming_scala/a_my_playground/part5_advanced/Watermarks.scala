package a_my_playground.part5_advanced

import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.{StreamingQuery, Trigger}
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.io.PrintStream
import java.net.ServerSocket
import java.sql.Timestamp
import scala.concurrent.duration.DurationInt


object Watermarks {


  val spark = SparkSession.builder()
    .appName("Late Data with Watermarks")
    .master("local[2]")
    .getOrCreate()

  import spark.implicits._


  def debugQuery(query: StreamingQuery): Unit ={
    //useful skill for debuging streams

    new Thread(() => {
      (1 to 100).foreach{ i =>
        Thread.sleep(1000)
        val queryEventTime =
          if(query.lastProgress == null) "[]"
          else query.lastProgress.eventTime.toString

        println(s"$i: $queryEventTime")

      }
    }).start()
  }


  //(miliseconds, data)
  def testWatermark() ={
    val dataDF = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",12345)
      .load()
      .as[String]
      .map{ line =>
        val tokens = line.split(",")
        val timestamp = new Timestamp(tokens(0).toLong)
        val data = tokens(1)

        (timestamp,data)
      }
      .toDF("created_time","color")

    val watermaredDF = dataDF
      .withWatermark("created_time","2 seconds")
      .groupBy(window(col("created_time"),"2 seconds"),col("color"))
      .count()
      .selectExpr("window.*","color","count")

    /*
    A 2 secodns watermark means
    -a window will only be considered until the watermark surpasses  the window end
    -an element/a row/a record will be considered if AFTER the watermark
     */

    val query = watermaredDF.writeStream
      .format("console")
      .outputMode("append")
      .trigger(Trigger.ProcessingTime(2.seconds))
      .start()

    //debug query in other thread
    debugQuery(query)

    query.awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    testWatermark()
  }
}

object DataSender{
  val serverSocket = new ServerSocket(12345)
  val socket = serverSocket.accept()  //blocking call
  val printer = new PrintStream(socket.getOutputStream)

  println("socket accepted")

  def example1() = {
    Thread.sleep(7000)
    printer.println("7000,blue")
    Thread.sleep(1000)
    printer.println("8000,green")
    Thread.sleep(4000)
    printer.println("14000,blue")
    Thread.sleep(1000)
    printer.println("9000,red") // discarded: older than the watermark
    Thread.sleep(3000)
    printer.println("15000,red")
    printer.println("8000,blue") // discarded: older than the watermark
    Thread.sleep(1000)
    printer.println("13000,green")
    Thread.sleep(500)
    printer.println("21000,green")
    Thread.sleep(3000)
    printer.println("4000,purple") // expect to be dropped - it's older than the watermark
    Thread.sleep(2000)
    printer.println("17000,green")
  }

  def example2() = {
    printer.println("5000,red")
    printer.println("5000,green")
    printer.println("4000,blue")

    Thread.sleep(7000)
    printer.println("1000,yellow")
    printer.println("2000,cyan")
    printer.println("3000,magenta")
    printer.println("5000,black")

    Thread.sleep(3000)
    printer.println("10000,pink")
  }

  def example3() = {
    Thread.sleep(2000)
    printer.println("9000,blue")
    Thread.sleep(3000)
    printer.println("2000,green")
    printer.println("1000,blue")
    printer.println("8000,red")
    Thread.sleep(2000)
    printer.println("5000,red") // discarded
    printer.println("18000,blue")
    Thread.sleep(1000)
    printer.println("2000,green") // discarded
    Thread.sleep(2000)
    printer.println("30000,purple")
    printer.println("10000,green")
  }

  def main(args: Array[String]): Unit = {
    example1()
  }


}
