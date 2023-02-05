package a_my_playground.part2_structed_streaming

import org.apache.spark.sql.functions.{col, from_json}
import org.apache.spark.sql.{DataFrame, SparkSession}

object StreamingJoins {

  val spark: SparkSession = SparkSession.builder
    .appName("Streaming Joins")
    .master("local[2]")
    .getOrCreate()

  val guitarPlayers: DataFrame = spark.read.option("inferSchema",true).json("src/main/resources/data/guitarPlayers")
  val guitars: DataFrame = spark.read.option("inferSchema",true).json("src/main/resources/data/guitars")
  val bands: DataFrame = spark.read.option("inferSchema",true).json("src/main/resources/data/bands")

  //joining static DF
  val joinCondition = guitarPlayers.col("band")===bands.col("id")
  val guitaristsBands = guitarPlayers.join(bands,joinCondition,"inner")

  def joinStreamWithStatic() = {

    val bandsSchema= bands.schema

    val streamBandsDF = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",12345)
      .load() // a DF with a single column "value" of type String
      .select(from_json(col("value"),bandsSchema).as("band")) //we need the same schema to join the static DF
      .selectExpr("band.id as id","band.name as name","band.hometown as hometown","band.year as year")

    //join happend per batch
    val streamedBandGuitaristsDF = streamBandsDF
      .join(guitarPlayers,
        guitarPlayers.col("band") === streamBandsDF.col("id"),
        "inner")

    /*
      restricted joins
      -stream joining with static :   RIGHT outer join/full outer join/ rigth_semi not permitted
      -static joining with streaming: LEFT outer join/full/left-semi not permitted
     */

    streamedBandGuitaristsDF.writeStream
      .format("console")
      .outputMode("append")
      .start()
      .awaitTermination()


  }

  //since spark 2.3 we have stream vs stream joins
  def joinStreamWithStream(): Unit ={
    val bandsSchema= bands.schema
    val guitarPlayersSchema = guitarPlayers.schema

    val streamBandsDF = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",12345)
      .load() // a DF with a single column "value" of type String
      .select(from_json(col("value"),bandsSchema).as("band")) //we need the same schema to join the static DF
      .selectExpr("band.id as id","band.name as name","band.hometown as hometown","band.year as year")

    val streamGuitaristsDF = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port",12346)
      .load() // a DF with a single column "value" of type String
      .select(from_json(col("value"),guitarPlayersSchema).as("guitarPlayer")) //we need the same schema to join the static DF
      .selectExpr("guitarPlayer.id as id","guitarPlayer.name as name","guitarPlayer.guitars as guitars","guitarPlayer.band as band")

    //join stream with stream
    val streamedJoin = streamBandsDF.join(streamGuitaristsDF,
      streamGuitaristsDF.col("band") === streamBandsDF.col("id"))

    /*
     -inner joins are supported
     -left/right outer joins ARE supported , but MUST have watermarks
     -full outer joins are NOT supported

     */

    streamedJoin.writeStream
      .format("console")
      .outputMode("append") //only append mode supported to stream vs stream joining
      .start()
      .awaitTermination()

  }

  def main(args: Array[String]): Unit = {
    //joinStreamWithStatic()
    joinStreamWithStream()
  }




}
