package pt3_sparkDataTypes

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._


object ManagingNulls extends App{

  val spark = SparkSession
    .builder()
    .appName("Managing Nulls")
    .config("spark.master","local")
    .getOrCreate()

  val moviesDF = spark.read.option("inferSchema","true").json("src/main/resources/data/movies.json")
  // val carsDF = spark.read.option("inferSchema","true").json("src/main/resources/data/cars.json")

  def examples(): Unit ={

    //coalesce method
    moviesDF.select(
      col("Title"),
      //coalesce :every null RTomatoesRating is filled with IMDBRating in the same row
      col("Rotten_Tomatoes_Rating"),
      col("IMDB_Rating")*10,
      coalesce(col("Rotten_Tomatoes_Rating"),col("IMDB_Rating")*10)
    ).show()

    //cheking for the nulls
    moviesDF.select("*").where(col("Rotten_Tomatoes_Rating").isNull).show()

    //manage nulls when ordering
    moviesDF.orderBy(col("IMDB_Rating").desc_nulls_last) //or desc_nulls_first

    //remove all the rows containing nulls
    moviesDF.select(
      col("Title"),
      col("IMDB_Rating")
    ).na.drop()

    //replace nulls
    //here we replace null with 0
    moviesDF.na.fill(0,List("Rotten_Tomatoes_Rating","IMDB_Rating"))
    //replace nulls of many columns with different values
    moviesDF.na.fill(Map(
      "IMDB_Rating" -> 0,
      "Rotten_Tomatoes_Rating"->10,
      "Director"->"Unknown"
    ))

    //complex operations
    moviesDF.selectExpr(
      "Title",
      "Rotten_Tomatoes_Rating",
      "IMDB_Rating",
      "ifnull(Rotten_Tomatoes_Rating, IMDB_Rating * 10)", //same as coalesce
      "nvl(Rotten_Tomatoes_Rating, IMDB_Rating * 10)",
      "nullif(Rotten_Tomatoes_Rating, IMDB_Rating * 10)",//returns null if the two vales are equal, pretty strange function, else return the first value
      "nvl2(Rotten_Tomatoes_Rating, IMDB_Rating * 10,0.0)"//if (first != null) second else third
    ).show()

  }

  examples()

}
