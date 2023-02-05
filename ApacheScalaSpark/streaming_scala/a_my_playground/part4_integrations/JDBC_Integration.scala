package a_my_playground.part4_integrations

import common.{Car, carsSchema}
import org.apache.spark.sql.{Dataset, SparkSession}

object JDBC_Integration {


  val spark = SparkSession.builder().appName("Integrading JDBC").master("local[2]").getOrCreate()

  val driver = "org.postgresql.Driver"
  val url = "jdbc:postgresql://localhost:1234/rtjvm"
  val user = "docker"
  val passwd = "docker"

  def writeStreamToPostgres(): Unit ={
    val carsDF = spark.readStream
      .schema(carsSchema)
      .json("src/main/resources/data/cars")

    import spark.implicits._
    val carsDS = carsDF.as[Car]

    //postgres cant write a stream ds only static for this reason we do the next
    carsDS.writeStream
      .foreachBatch{(batch:Dataset[Car],batchId:Long) =>
        //each executor can control the batch
        //batch is a static dataset / dataframe



        batch.write
          .format("jdbc")
          .option("driver",driver)
          .option("url",url)
          .option("user",user)
          .option("password",passwd)
          .option("dbtable","public.cars")
          .save()
      }
      .start()
      .awaitTermination()
  }

  def main(args: Array[String]): Unit = {

    writeStreamToPostgres()
  }
}
