package pt2_dataFrames

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{DoubleType, IntegerType, LongType, StringType, StructField, StructType}
object DataFrames_Basics extends App{

  //creating spark session
  val spark = SparkSession.builder()
    .appName("DataFrames Basics")
    .config("spark.master","local") //when is not local we pass url
    .getOrCreate()

  def theory(): Unit ={
    //reading DF
    val firstDF = spark.read
      .format("json")
      //in producttion dont use inferSchema=true,  rather define you own schema,
      //becouse some type deduction can be mistakes, for example infere a date type like string
      .option("inferSchema", "true")
      .load("src/main/resources/data/cars.json")
    //show df
    firstDF.show()

    //print column descriptions of table
    firstDF.printSchema()

    //take the first 10
    firstDF.take(10).foreach(println)

    //spark types are singelton objects of normal types
    val longtype =LongType

    //exist more complex types like
    //Schema
    val carsSchema = StructType(Array(
      StructField("Name", StringType), //example of Spark type in Schema type
      StructField("Miles_per_Gallon", DoubleType),
      StructField("Cylinders", LongType),
      StructField("Displacement", DoubleType),
      StructField("Horsepower", LongType),
      StructField("Weight_in_lbs", LongType),
      StructField("Acceleration", DoubleType),
      StructField("Year", StringType),
      StructField("Origin", StringType)
    ))

    //you can obtain the schema of a dataset
    val carsDFSchema = firstDF.schema

    //reading DF
    val carsDF = spark.read
      .format("json")
      .schema(carsDFSchema) //put a schema to DF
      .load("src/main/resources/data/cars.json")


    //create rows by hand
    val myRow = Row("chevrolet chevelle malibu",18.0,8L,307.0,130L,3504L,12.0,"1970-01-01","USA")

    //create a DF for touples
    val cars = Seq(
      ("chevrolet chevelle malibu",18.0,8L,307.0,130L,3504L,12.0,"1970-01-01","USA"),
      ("buick skylark 320",15.0,8L,350.0,165L,3693L,11.5,"1970-01-01","USA"),
      ("plymouth satellite",18.0,8L,318.0,150L,3436L,11.0,"1970-01-01","USA"),
      ("amc rebel sst",16.0,8L,304.0,150L,3433L,12.0,"1970-01-01","USA"),
      ("ford torino",17.0,8L,302.0,140L,3449L,10.5,"1970-01-01","USA"),
      ("ford galaxie 500",15.0,8L,429.0,198L,4341L,10.0,"1970-01-01","USA"),
      ("chevrolet impala",14.0,8L,454.0,220L,4354L,9.0,"1970-01-01","USA"),
      ("plymouth fury iii",14.0,8L,440.0,215L,4312L,8.5,"1970-01-01","USA"),
      ("pontiac catalina",14.0,8L,455.0,225L,4425L,10.0,"1970-01-01","USA"),
      ("amc ambassador dpl",15.0,8L,390.0,190L,3850L,8.5,"1970-01-01","USA")
    )

    val manualCarsDF = spark.createDataFrame(cars) //schema auto-inferred, not column names
    //note : DFs have schemas Rows not

    //create DFs with implicits
    import spark.implicits._
    val manuallyCarDFWithImplicits = cars.toDF("Name", "MPG", "Cylinders", "Displacement", "HP",
      "Weight","Acceleration","Year","Country")
  }

  def exercises(): Unit ={
    /**
     * Exercise 1:
     *  Create a manual DF describing smartphones
     *  -model
     *  -brand
     *  -price
     *
     *  Exercise 2
     *    Read another file from the data folder, e.g. movies.json
     *    -print the schema
     *    -count the number of rows, call count()
     */


    //EX1
    /**
    val phoneSchema = StructType(Array(
      StructField("Model", StringType), //example of Spark type in Schema type
      StructField("Brand", StringType),
      StructField("Price",IntegerType)
    ))
  **/

    val phones = Seq(
      ("Nokia69","Nokia",122),
      ("SamsungGalaxy1","Samsung",222),
      ("IPhone3","Apple",821),
      ("Telefunken3","Telefunken",34),
      ("IPhone2","Apple",665),
      ("SamsungGalaxy33","Samsung",424),
    )

    import spark.implicits._
    val manualPhonesDF = phones.toDF("Model","Brand","Prince")
    manualPhonesDF.show()

    //Exercise 2
    val moviesDF = spark.read
      .format("json")
      .option("inferSchema", "true")
      .load("src/main/resources/data/movies.json")

    moviesDF.show()
    moviesDF.printSchema()
    println(s"The Movie DF has ${moviesDF.count()}rows")

  }

  exercises()

}

