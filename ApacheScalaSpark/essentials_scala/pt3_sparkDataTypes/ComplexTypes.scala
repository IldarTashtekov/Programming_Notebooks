package pt3_sparkDataTypes

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import pt3_sparkDataTypes.CommonTypes.spark

object ComplexTypes extends App{

 val spark = SparkSession.builder().appName("complex data types").config("spark.master","local").getOrCreate()
 spark.conf.set("spark.sql.legacy.timeParserPolicy","LEGACY")

  val moviesDF = spark.read.option("inferSchema","true").json("src/main/resources/data/movies.json")

  def dates (): Unit ={
    //transform string with date to Date
    moviesDF.select(col("Title"),to_date(col("Release_Date"),"dd-MMM-yy").as("Actual_Release"))
      .withColumn("Today",current_date())
      .withColumn("Right_Now",current_timestamp())
      .withColumn("Movie age",datediff(col("Today"),col("Actual_Release")) / 365)
      .show()
    //date_add & date_sub ,add and subtract date

    /*
    Dates exercises
    1.How we deal with multiple formats? solution: parse DF multiples times , then union the small DF
    2. Read the stocksDF
     */

    val stockDF = spark.read.option("inferSchema","true").option("header","true").csv("src/main/resources/data/stocks.csv")

    stockDF.select(
      col("symbol"),
      col("price"),
      to_date(col("date"),"MMM dd yyyy")
    ).show()


  }

  def structures (): Unit ={
    //with cols
    moviesDF.select(col("Title"),
      struct(col("US_Gross"),col("Worldwide_Gross")).as("Profit")).show()

    //with expr
    moviesDF.selectExpr("Title","(US_Gross,Worldwide_Gross) as Profit")
      .selectExpr("Title","Profit.US_Gross")

    //Arrays
    val moviesWithWords = moviesDF.select(col("Title"),split(col("Title")," | ").as("Title_Words")) //return array of strings
    moviesWithWords.select(
      col("Title"),
      expr("Title_Words[0]"), //col with first word of title
      size(col("Title_Words")), //col with number of title words
      array_contains(col("Title_Words"),"Love") //col with boolean if contains Love true else false
    ).show()
  }


  structures()
}
