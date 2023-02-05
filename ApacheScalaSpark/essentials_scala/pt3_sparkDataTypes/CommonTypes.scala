package pt3_sparkDataTypes

import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.functions._

object CommonTypes extends App {
  val spark = SparkSession.builder()
    .appName("Common Spark Types")
    .config("spark.master","local")
    .getOrCreate()

  val moviesDF = spark.read.option("inferSchema","true").json("src/main/resources/data/movies.json")
  val carsDF = spark.read.option("inferSchema","true").json("src/main/resources/data/cars.json")

  def booleanTypes(): Unit ={
    //adding a plain value to df
    moviesDF.select(col("Title"),lit(47).as("plain_value")).show()

    //BOOLEAN TYPES
    //Booleans to Filtring
    val dramaFilter = col("Major_Genre") equalTo "Drama"
    val goodRatingFilter = col("IMDB_Rating") > 7.0
    val goodDramasFilter = dramaFilter and goodRatingFilter
    moviesDF.select("Title").where(goodDramasFilter).show()

    //Boolean column, if the movie is good the value of column is True else False
    val moviesWithGoodnessFlagsDF = moviesDF.select(col("Title"), goodRatingFilter.as("good_movie"))

    //filter in boolean column
    moviesWithGoodnessFlagsDF.where("good_movie").show() //where(col("good_movie") === true)
    moviesWithGoodnessFlagsDF.where(not(col("good_movie"))).show()

  }

  def numberTypes(): Unit ={
    //NUMBER TYPES
    //math operators
    val moviesAvgRatings = moviesDF.select(col("Title"),
      col("Rotten_Tomatoes_Rating") / 10 + col("IMDB_Rating") / 2
    )

    //correlation = number -1 and 1, how more close to 1 or -1 are the result more corelated are the two values
    println(moviesDF.stat.corr("Rotten_Tomatoes_Rating", "IMDB_Rating"))

  }

  def stringTypes(): Unit ={

    //capitalziation: initcap(first letter Uppercase), lowe, upper
    carsDF.select(initcap(col("Name"))).show()

    //contains
    carsDF.select("*").where(col("Name").contains("volkswagen")).show()

    //regex, regular expretions

    //extract
    val regexString = "volkswagen|ww"
    val wwDF = carsDF.select(
      col("Name"),
      regexp_extract(col("Name"),regexString,0).as("regex_extract")
    ).where(col("regex_extract") =!= "")

    //replace
    wwDF.select(
      col("Name"),
      regexp_replace(col("Name"),regexString,"People's Car").as("regex_replace")
    ).show()



  }

  def exercises(): Unit ={

    /**
     * Exercise: Filter  the cars DF by a list of car names obtained by an API call
     *
     */
    def getCarNames: List[String] = List("Volkwagen", "Mercedes-Benz", "Ford")

    //regex version
    val complexRegex = getCarNames.map(_.toLowerCase()).mkString("|") //carName | carName | carName
    carsDF.select(
      col("Name"),
      regexp_extract(col("Name"),complexRegex,0).as("regex_extract")
    ).where(col("regex_extract") =!= "")
      .drop("regex_extract")
      .show()

    //contains version
    val carNameFilters = getCarNames.map(_.toLowerCase()).map(name => col("Name").contains(name))
    val bigFilter = carNameFilters.fold(lit(false))((combinedFilter,newCarNameFilter) => combinedFilter or newCarNameFilter)
    carsDF.filter(bigFilter).show()


  }



  exercises()



}
