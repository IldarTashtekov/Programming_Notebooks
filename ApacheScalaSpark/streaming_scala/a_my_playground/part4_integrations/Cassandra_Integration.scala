package a_my_playground.part4_integrations

import com.datastax.spark.connector.cql.CassandraConnector
import common.{Car, carsSchema}
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{Dataset, ForeachWriter, SaveMode, SparkSession}
import org.apache.spark.sql.cassandra._
import part4integrations.IntegratingCassandra.CarCassandraForeachWriter

object Cassandra_Integration {
  val spark = SparkSession.builder()
    .appName("Integrading Cassandra")
    .master("local[2]")
    .config("spark.cassandra.connection.host", "127.0.0.1")
    .config("spark.cassandra.connection.port", "9042")
    .getOrCreate()
  import spark.implicits._



  def writeStreamToCassandraInBatches(): Unit ={


    val carsDS = spark.readStream
      .schema(carsSchema)
      .json("src/main/resources/data/cars")
      .as[Car]

    carsDS
      .writeStream
      .foreachBatch{(batch:Dataset[Car], _: Long) => //(batch, id)
        //save this batch to cassandra in a single table write
          batch
            .select(col("Name"),col("Horsepower"))
            .write
            .cassandraFormat("cars","public") //type enrichment
            .mode(SaveMode.Append)
            .save()

      }
      .start()
      .awaitTermination()
  }

  //For each writer class
  class CarCassandraForeachWriter extends ForeachWriter[Car]{
    /*
      -on every batch, on every partition 'partitionId'
        -on every 'epoch' = chunk of data
          -call the open method:Boolean ; if false skip the chunk
          -for each entry in this chunk, call the process method
          -call the close methood either at the end of the chunk or with errir if it was throw
     */

    val keyspace = "public"
    val table = "cars"
    val connector = CassandraConnector(spark.sparkContext.getConf)

    override def open(partitionId: Long, epochId: Long): Boolean = {
      println("Open connection")
      true
    }

    override def process(car: Car): Unit = {
      connector.withSessionDo{ session =>
        session.execute(
          s"""
             |insert into $keyspace.$table("Name","Horsepower")
             |values('${car.Name}',${car.Horsepower.orNull}) //car is string for this reason rounded by '', hoorsepower is an optional for this reason we use .orNull
             |""".stripMargin)

      }
    }

    override def close(errorOrNull: Throwable): Unit = println("Closing connection")
  }

  def writeStreamToCassandra(): Unit ={
    val carsDS = spark.readStream
      .schema(carsSchema)
      .json("src/main/resources/data/cars")
      .as[Car]

    carsDS
      .writeStream
      .foreach(new CarCassandraForeachWriter) // needs a foreach writer
      .start()
      .awaitTermination()

  }

  def main(args: Array[String]): Unit = {
    writeStreamToCassandra()
  }
}
