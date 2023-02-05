package pt4_RDDs

import org.apache.spark.SparkContext
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.io.Source

object RDDs extends App{
  case class StockValue(company:String,date:String,price:Double)

  val spark = SparkSession.builder()
    .appName("RDD's")
    .config("spark.master","local")
    .getOrCreate()

  val sc = SparkContext.getOrCreate()

  def rddBasics(): Unit ={
    //ways to create RDD's
    //1-parallelize an existing collection
    val numbers = 1 to 1000
    val numbersRDD = sc.parallelize(numbers)

    //2-reading from files

    def readStocks(fileName:String):List[StockValue]=
      Source.fromFile(fileName).getLines()
        .drop(1) //drop header
        .map(line => line.split(',')) //get Array[String] of every value separated by ","
        .map(tokens => StockValue(tokens(0),tokens(1),tokens(2).toDouble)) //create StockValue class with that values
        .toList //return a list of StockValue

    val stocksRDD = sc.parallelize(readStocks("src/main/resources/data/stocks.csv"))

    //2.1- 2d way to reading from files
    val stocksRDD2 = sc.textFile("src/main/resources/data/stocks.csv") //plain text
      .map(line => line.split(','))
      .filter(tokens => tokens(0).toUpperCase() == tokens(0)) //filter the header
      .map(tokens => StockValue(tokens(0),tokens(1),tokens(2).toDouble))

    //3- read from the dataframe
    val stocksDF = spark.read.option("inferSchema","true").csv("src/main/resources/data/stocks.csv")
    import spark.implicits._
    val stocksDS = stocksDF.as[StockValue]
    val stockRDD3 = stocksDS.rdd

    //RDD -> DF
    val numbersDF = numbersRDD.toDF("numbers") // you loose the type information

    //RDD -> DS
    val numbersDS = spark.createDataset(numbersRDD) //keeps the type information
  }


  def rddTransformations(): Unit ={

    val numbers = 1 to 1000
    val numbersRDD = sc.parallelize(numbers)

    def readStocks(fileName:String):List[StockValue]=
      Source.fromFile(fileName).getLines()
        .drop(1) //drop header
        .map(line => line.split(',')) //get Array[String] of every value separated by ","
        .map(tokens => StockValue(tokens(0),tokens(1),tokens(2).toDouble)) //create StockValue class with that values
        .toList //return a list of StockValue

    val stocksRDD = sc.parallelize(readStocks("src/main/resources/data/stocks.csv"))
    //stocksRDD.foreach(print)
    val msftRDD = stocksRDD.filter(_.company == "MSFT" ) //lazy transformation

    //counting
    val msftCount = msftRDD.count() //eager action
    println(msftCount)
    val companyNamesRDD = stocksRDD.map(_.company).distinct() //distinct is also lazy transformation
    companyNamesRDD.foreach(println)

    //min and max
    implicit val stockOrdering: Ordering[StockValue] = Ordering.fromLessThan((stock_a, stock_b)=>stock_a.price >stock_b.price)
    val minMicrosoft = msftRDD.min() //this is an action

    //reduce
    numbersRDD.reduce(_ + _)

    //grouping
    val groupedStocksRDD = stocksRDD.groupBy(_.company)//we group by company name, grouping is a wide operation and is very expensive

    groupedStocksRDD.foreach(println)

    //partitioning
    /*
    repartitioning is expensive involves shuffeling
    Best practice, partition EARLY then process that rdd
     Recomended ize of a partition 10-100MB
     */
    val repartitionedStocksRDD = stocksRDD.repartition(30)
    import spark.implicits._
    repartitionedStocksRDD.toDF.write
      .mode(SaveMode.Overwrite)
      .parquet("src/main/resources/data/stocks30")

    //coalesce
    val coalescedRDD = repartitionedStocksRDD.coalesce(15)//does NOT involve shuffling
    coalescedRDD.toDF.write
      .mode(SaveMode.Overwrite)
      .parquet("src/main/resources/data/stocks15")


  }



  case class Movie(title:String, genre:String, rating:Double)
  def exercises(): Unit ={
    /**
     * Exercises
     * 1. Read the movies.json as RDD
     * 2. Show the distinct genres as an RDD
     * 3. Select all the movies in the Drama genre with IMDB rating > 6.
     * 4. Show the average rating of movies by genre
     */

    //1
    val moviesDF_raw = spark.read.option("inferSchema","true").json("src/main/resources/data/movies.json")
    val moviesDF = moviesDF_raw.select(col("Title").as("title")
      ,col("Major_Genre").as("genre")
      ,col("IMDB_Rating").as("rating")
    ).na.fill(Map( //RDD can't have nulls
      "genre" -> "None",
      "rating"->0,
      "Title"->"None"
    ))

    moviesDF.show(10)

    import spark.implicits._
    val moviesDS = moviesDF.as[Movie]
    val moviesRDD = moviesDS.rdd
    //moviesRDD.foreach(println)

    //2
    moviesRDD.map(_.genre).distinct().foreach(println)


    //3
    moviesRDD.filter(m => m.genre=="Drama" && m.rating > 6).foreach(println)


    //4
    println("THE AVERAGE RATING BY GENRE")
    moviesRDD.groupBy(_.genre).foreach(genre =>
      println(genre._1+" | "+genre._2.map(m=> m.rating ).sum / genre._2.count(movie=>true))
    )

  }

  exercises()

}
