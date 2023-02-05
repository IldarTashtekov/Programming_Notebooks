package pt2_dataFrames

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{approx_count_distinct, avg, col, count, countDistinct, desc, max, mean, min, stddev, sum}

object Aggregations extends App {
  val spark = SparkSession.builder()
    .appName("Aggregations")
    .config("spark.master", "local")
    .getOrCreate()

  val moviesDF = spark.read.option("inferSchema","true").json("src/main/resources/data/movies.json")


  //counting
  def counting(): Unit ={
    val genresCountDF = moviesDF.select(count(col("Major_Genre"))) //all column rows except null
    //another way is =  moviesDF.selectExpr("count(Major_Genre)")
    genresCountDF.show()
    //count all the rows and include nulls
    //moviesDF.select(count("*"))

    //counting distinct
    moviesDF.select(countDistinct(col("Major_Genre"))).show()

    //aproximate count
    moviesDF.select(approx_count_distinct(col("Major_Genre")))
  }

  //statistics
  def statistics(): Unit ={
    //max min
    val minRating = moviesDF.select(min(col("IMDB_Rating")))
    val maxRating = moviesDF.select(max(col("IMDB_Rating")))

    //sum
    moviesDF.select(sum(col("US_Gross"))).show()

    //avg
    moviesDF.select(avg(col("Rotten_Tomatoes_Rating")))

    //mean,standard deviation
    moviesDF.select(
      mean(col("Rotten_Tomatoes_Rating")),
      stddev(col("Rotten_Tomatoes_Rating"))
    ).show()
  }

  def grouping(): Unit ={
    //the next operation is equivalent to sql: select count(*) from moviesDF group by Major_Genre
    val countByGenre = moviesDF
      .groupBy(col("Major_Genre")) //includes null
      .count()
    countByGenre.show()

    val avgRatingByGenreDF = moviesDF
      .groupBy(col("Major_Genre"))
      .avg("IMDB_Rating")
    avgRatingByGenreDF.show()


    //many aggregations with agg method
    val aggregationsByGenreDF = moviesDF
      .groupBy(col("Major_Genre"))
      .agg(
        count("*").as("N_Movies"),
        avg("IMDB_Rating").as("Avg_Rating")
      )
      .orderBy(col("Avg_Rating"))

    aggregationsByGenreDF.show()


  }

  def exercises(): Unit ={
    /**
     * 1. Sum up ALL the profits of ALL the movies in the DF
     * 2. Count how many distinct directors we have
     * 3. Show the mean and standart deviation of US gross revenue for the movies
     * 4. Compute the average IMDB rating and the average US gross revenue PER DIRECTOR
     */

    //1
    moviesDF.select(
       sum(col("US_Gross"))
      +sum(col("Worldwide_Gross"))
      +sum(col("US_DVD_Sales"))
    ).show()

    //2
    moviesDF.select(approx_count_distinct(col("Director"))).show()

    //3
    moviesDF.select(
      mean(col("US_Gross")),
      stddev(col("US_Gross"))
    ).show()

    //4
    moviesDF.groupBy(col("Director"))
      .agg(
        mean(col("US_Gross")),
        mean(col("IMDB_Rating")).as("Rating_avg")
      ).orderBy(col("Rating_avg").desc).show()


  }

  exercises()
}