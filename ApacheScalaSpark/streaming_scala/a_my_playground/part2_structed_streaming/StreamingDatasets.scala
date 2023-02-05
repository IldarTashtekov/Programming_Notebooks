package a_my_playground.part2_structed_streaming

import common._
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders, SparkSession}
import org.apache.spark.sql.functions.{avg, col, count, from_json}

object StreamingDatasets {

  //include encoders for DF to DS transformation
  //implicit method
  //import spark.implicits._

  //explicit method
  var carEncoder: Encoder[Car] = Encoders.product[Car]

  val spark: SparkSession = SparkSession.builder
    .appName("Streaming Datasets")
    .master("local[2]")
    .getOrCreate()

  def readCars() ={

    spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",1234)
      .load()//with single columns "value"
      .select(from_json(col("value"),carsSchema).as("car")) //composite column (struct)
      .selectExpr("car.*") //DF with multiple column
      .as[Car](carEncoder) //transform DF to Car

  }

  def showCarsNames(): Unit ={
    val carsDS: Dataset[Car] = readCars()

    //transformations
    // DS to DF
    val carNamesDF:DataFrame = carsDS.select(col("Name"))

    //DS to DS
    val carNamesAlt:Dataset[String] = carsDS.map(_.Name)(Encoders.STRING)

    carNamesAlt.writeStream
      .format("console")
      .outputMode("append")
      .start()
      .awaitTermination()
  }

  /*
  EXERCISES
    1) Count how many POWERFUL cars exist in the DS (HorsePower > 140)
    2) Average HorsePower of all DS (use complete output mode)
    3) Count the cars by they origin field
   */
  def exercises(): Unit ={
    val carsDS: Dataset[Car] = readCars()

    //1
    /*
    carsDS.filter(_.Horsepower.getOrElse(0L) > 140)
      .writeStream
      .format("console")
      .outputMode("append")
      .start()
      .awaitTermination()
*/
    //2
    /*
    carsDS.select(avg(col("Horsepower")))
      .writeStream
      .format("console")
      .outputMode("complete")
      .start()
      .awaitTermination()

     */

    //3
    //DF API solution
   // carsDS.groupBy("Origin").count()

    //DS API solution
     carsDS.groupByKey(car => car.Origin)(Encoders.STRING).count()
      .writeStream
      .format("console")
      .outputMode("complete")
      .start()
      .awaitTermination()
  }

  def main(args: Array[String]): Unit = {
    //showCarsNames()
    //exercises()

    /*
    Same DS convertion as non-streaming DS
    pros: type safety , expressivness
    cons: potential perf implications as lambdas cannot be optimized
     */
  }
}
