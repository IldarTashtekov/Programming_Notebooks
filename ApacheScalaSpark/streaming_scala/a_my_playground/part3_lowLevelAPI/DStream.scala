package a_my_playground.part3_lowLevelAPI

import common.Stock
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.io.{File, FileWriter}
import java.sql.Date
import java.text.SimpleDateFormat

object DStream {

  val spark = SparkSession.builder().appName("DStreams").master("local[2]").getOrCreate()

  /*
  spark streaming context = entry point to DStreams API
  - needs the spark context
  - a duration = batch interval
  */

  val ssc = new StreamingContext(spark.sparkContext,Seconds(1))

  /*
    -define input sources by creating DStreams
    -define transformations on DStreams
    -start computation with ssc.start()
      -after this point no more computations will be added
    -await termination , or stop the computation
      -after termination you cannot restart a computation

   */

  def readForSocket(): Unit ={

    val socketStream:DStream[String] = ssc.socketTextStream("localhost",12345)

    //transformation *transformation are lazy, they are not performed before we do an action
    val wordsStream: DStream[String] = socketStream.flatMap(line => line.split(" "))

    //actions
    wordsStream.print() //print
    wordsStream.saveAsTextFiles("src/main/resources/data/words/") //save as text file

    ssc.start()
    ssc.awaitTermination()
  }

  //that function creates a file to trigger DStream read from file
  def createNewFile(): Unit ={
    new Thread(()=>{
      Thread.sleep(5000)

      val path = "src/main/resources/data/stocks"

      val dir = new File(path)
      val nFiles = dir.listFiles().length
      val newFile = new File(s"$path/newStocks$nFiles.csv")
      newFile.createNewFile()

      val writer = new FileWriter(newFile)
      writer.write(
        """
          |AAPL,May 1 2000,21
          |AAPL,Jun 1 2000,26.19
          |AAPL,Jul 1 2000,25.41
          |AAPL,Aug 1 2000,30.47
          |AAPL,Sep 1 2000,12.88
          |AAPL,Oct 1 2000,9.78
          |AAPL,Nov 1 2000,8.25
          |AAPL,Dec 1 2000,7.44
          |AAPL,Jan 1 2001,10.81
          |AAPL,Feb 1 2001,9.12
          |AAPL,Mar 1 2001,11.03
          |""".stripMargin.trim)

      writer.close()
    }).start()
  }

  def readFromFile(): Unit ={

    createNewFile() //operates in another thread

    /*
    ssc.textFileStream monitors a directory for NEW FILES
     */
    val stocksFilePath = "src/main/resources/data/stocks"
    val textStream: DStream[String] = ssc.textFileStream(stocksFilePath)

    //transformations
    val dateFormat = new SimpleDateFormat("MMM d yyyy")
    val stocksStream: DStream[Stock] = textStream.map{line=>
      val tokens = line.split(",")
      val company = tokens(0)
      val date = new Date(dateFormat.parse(tokens(1)).getTime)
      val price = tokens(2).toDouble

      Stock(company,date,price)
    }

    //actions
    stocksStream.print()

    //start the computations
    ssc.start()
    ssc.awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    //readForSocket()
   // readFromFile()

    /**
    /**
     * Takeaways
     * -DStreams are never-ending sequence of RDD'S
     * -Available under Streaming Context
     * -Supports various transformations like map, flatmap
     * -Computation started via an action + start streaming context
     */
  }
}
