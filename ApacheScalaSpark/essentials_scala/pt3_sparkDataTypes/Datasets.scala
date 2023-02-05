package pt3_sparkDataTypes

import org.apache.spark.sql.{Dataset, Encoder, Encoders, SparkSession}
import org.apache.spark.sql.functions._

object Datasets extends App{

  val spark = SparkSession
    .builder()
    .appName("Datasets")
    .config("spark.master","local")
    .getOrCreate()

  import spark.implicits._

  val numbersDF = spark.read.option("inferSchema","true").csv("src/main/resources/data/numbers.csv")
  val carsDF = spark.read.option("inferSchema","true").json("src/main/resources/data/cars.json")

  def readDFJson(filename: String) = spark.read
    .option("inferSchema", "true")
    .json(s"src/main/resources/data/$filename")


  case class Guitar(id:Long, make:String, model:String, guitarType:String)
  case class GuitarPlayer(id:Long, name:String, guitars: Seq[Long],band:Long)
  case class Band(id:Long,name:String,hometown:String,year:Long)

  case class Car(
                  Name: String,
                  Miles_per_Gallon: Option[Double],
                  Cylinders: Long,
                  Displacement: Double,
                  Horsepower: Option[Long],
                  Weight_in_lbs: Long,
                  Acceleration: Double,
                  Year: String,
                  Origin: String
                )


  def datasets(): Unit ={

     //CONVERT DF TO DS, works fine to DF with 1 column and simple types
     //encoder has the capability to tourn a row in DF into a Int
    // implicit val intEncoder: Encoder[Int] = Encoders.scalaInt
     val numberDS = numbersDF.as[String]
     numberDS.show()

     //DATASET of more complex types
     //step 1 create a case class, in that case Car case class
     //step 2 read the DF
     //step 3 define encoder (fast way: importing implicits)
     //implicit val carEncoder: Encoder[Car] = Encoders.product[Car]
     //step 4 convert DF to DS
     val carDS = carsDF.as[Car]
     carDS.show()

     //map,flatMap,fold,reduce
     val carNamesDS = carDS.map(car => car.Name.toUpperCase())
     carNamesDS.show

     carNamesDS.filter(_.contains("CHEVROLET")).show
   }

  def exercises(): Unit ={
    /**
    Exercises
     1.Count how many Powerful cars we have (Horsepower > 140)
     3.Average Horsepower in the entire dataset
     */

    val carDS = carsDF.as[Car]

    //1
    println(carDS.filter(_.Horsepower.getOrElse(0L) > 140).count())
    //2
    println(carDS.map(_.Horsepower.getOrElse(0L)).reduce(_ + _) / carDS.count)
    //otro metodo con DF funcions
    carDS.select(avg(col("Horsepower"))).show

  }


  def joins(): Unit ={
    val guitarDS = readDFJson("guitars.json").as[Guitar]
    val guitarPlayersDS = readDFJson("guitarPlayers.json").as[GuitarPlayer]
    val bandsDS = readDFJson("bands.json").as[Band]

    //joins on DS
    val guitarPlayerBandsDS = guitarPlayersDS.joinWith(bandsDS,
      guitarPlayersDS.col("band")===bandsDS.col("id"))

    guitarPlayerBandsDS.show()

    /**
     *Exercise
     * join guitarDS and guitarPlayerDS
     *hint: use array_contains
     */
    val guitarPlayerGuitarDS = guitarPlayersDS.joinWith(guitarDS,
      array_contains(guitarPlayersDS.col("guitars"),guitarDS.col("id")),
      "outer"
    )

    guitarPlayerGuitarDS.show()

  }

  def grouping(): Unit ={
    val carsDS = carsDF.as[Car]

    //how many cars have every origin
    carsDS.groupByKey(_.Origin).count().show()

    //joins and groups are WIDE transformations, will involve SHUFFLE operations
  }

  grouping()

}
