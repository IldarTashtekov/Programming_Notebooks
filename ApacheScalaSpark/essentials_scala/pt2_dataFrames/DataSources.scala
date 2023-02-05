package pt2_dataFrames

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.types.{DateType, DoubleType, LongType, StringType, StructField, StructType}
import pt2_dataFrames.DataFrames_Basics.spark

object DataSources extends App {

  val spark = SparkSession.builder()
    .appName("Data Sources")
    .config("spark.master","local")
    .getOrCreate()


  def general(): Unit ={
    val carsSchema = StructType(Array(
      StructField("Name", StringType), //example of Spark type in Schema type
      StructField("Miles_per_Gallon", DoubleType),
      StructField("Cylinders", LongType),
      StructField("Displacement", DoubleType),
      StructField("Horsepower", LongType),
      StructField("Weight_in_lbs", LongType),
      StructField("Acceleration", DoubleType),
      StructField("Year", DateType),
      StructField("Origin", StringType)
    ))

    /*
    Reading a DF require:
     - format
     - schema or inferSchema=true
     -path
     - zero or more options
     */
    val carsDF = spark.read
      .format("json")
      .schema(carsSchema) //put a schema to DF
      //if json is dont conform the schema or is malformed you can use mode
      .option("mode","failFast") // dropMalformed, permissive(default)
      //.option("path","src/main/resources/data/cars.json").load() another form of load file
      .load("src/main/resources/data/cars.json") //path of file or url

    carsDF.show(5)

    val carDFWithOptionMap = spark.read
      .format("json")
      .options(
        Map(
          "mode" -> "failFast",
          "path" -> "src/main/resources/data/cars.json",
          "inferSchema" -> "true"
        ))
      .load()


    /*
    Writing DF's
      -format
      -save mode = overwrite, append, ignore, errorIfExist
      -path
     */
    carsDF.write
      .format("json")
      .mode(SaveMode.Overwrite)
      //.option("path","src/main/resources/data/cars_clone.json")
      .save("src/main/resources/data/cars_clone.json")

  }

  def jsonFlags(): Unit ={

    val carsSchema = StructType(Array(
      StructField("Name", StringType), //example of Spark type in Schema type
      StructField("Miles_per_Gallon", DoubleType),
      StructField("Cylinders", LongType),
      StructField("Displacement", DoubleType),
      StructField("Horsepower", LongType),
      StructField("Weight_in_lbs", LongType),
      StructField("Acceleration", DoubleType),
      StructField("Year", DateType),
      StructField("Origin", StringType)
    ))
    //json flags
    spark.read
      //.format("json")
      .schema(carsSchema)//date format only works with specified schema
      .option("dateFormat", "YYYY-MM-dd") //couple with schema; if Spark fails parsing it will put null
      .option("allowSingleQuotes","true") //treat the "" like '' in json
      .option("compression","uncompressed") //bzip2, gzip, lz4, snappy, deflate
      .json("src/main/resources/data/cars_clone.json") //format and load at the same time
    //.load("blabalpath")
  }

  def csvFlags(): Unit ={

    val stocksSchema = StructType(Array(
      StructField("symbol",StringType),
      StructField("date",DateType),
      StructField("price",DoubleType)
    ))

    spark.read
      .schema(stocksSchema)
      .option("dateFormat","MMM dd YYYY")
      .option("header","true") //ignores the first row becouse is column name
      .option("sep", ",") //separator in that csv
      .option("nullValue","") //csv dont have nulls, in that csv null is ""
      .csv("src/main/resources/data/stocks.csv")
      .show(5)
  }

  def parquetFormat(): Unit ={
    val carsDF = spark.read
      .option("inferSchema", "true")
      .option("mode","failFast") // dropMalformed, permissive(default)
      .json("src/main/resources/data/cars.json") //path of file or url

    carsDF.write
      .mode(SaveMode.Overwrite)
      .parquet("src/main/resources/data/cars.parquet")

  }

  def textFiles(): Unit ={
    spark.read.text("src/main/resources/data/sampleTextFile.txt").show( )
  }

  def postgresDB(): Unit ={

    val empDF = spark.read
      .format("jdbc")
      .option("driver","org.postgresql.Driver")
      .option("url","jdbc:postgresql://localhost/test_db")
      .option("user","postgres")
      .option("password","pgAdmin")
      .option("dbtable","public.client")
      .load()

    empDF.show()
  }

  def write_exercises(): Unit ={
    /**
     * Exercise:
     *  read the movies DF , then write it as
     *    -tab-separated value file
     *    -snappy Parquet
     *    -as sql table
     */

    val moviesDF = spark.read
      .option("inferSchema", "true")
      .json("src/main/resources/data/movies.json")


    moviesDF.write
      .mode(SaveMode.Overwrite)
      .parquet("src/main/resources/data/movies.parquet")

    moviesDF.write
      .option("header","true")
      .csv("src/main/resources/data/movies_csv.csv")

    moviesDF.write
      .format("jdbc")
      .option("driver","org.postgresql.Driver")
      .option("url","jdbc:postgresql://localhost/test_db")
      .option("user","postgres")
      .option("password","pgAdmin")
      .option("dbtable","public.movies")
      .save()
  }

}

