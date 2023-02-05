package pt2_dataFrames

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.expr

object Joins extends App {
  val spark = SparkSession.builder()
    .appName("Aggregations")
    .config("spark.master", "local")
    .getOrCreate()
  val guitarDF = spark.read.option("inferSchema","true").json("src/main/resources/data/guitars.json")

  val guitaristsDF = spark.read.option("inferSchema","true").json("src/main/resources/data/guitarPlayers.json")

  val bandsDF = spark.read.option("inferSchema","true").json("src/main/resources/data/bands.json")

  def joins(): Unit ={

    //inner joins
    val joinCondition = guitaristsDF.col("band")===bandsDF.col("id")
    val guitaristsBandsDF = guitaristsDF.join(bandsDF,joinCondition,"inner")

    //outer joins
    //left outer = everything in the inner join + all the rows in the LEFT table, with nulls when data is missing
    guitaristsDF.join(bandsDF,joinCondition,"left_outer").show
    //rigth outer join
    guitaristsDF.join(bandsDF,joinCondition,"right_outer").show

    //outer join = all with all
    guitaristsDF.join(bandsDF,joinCondition,"outer").show

    //semi join, everything in a left DF for which there is a row in a right DF satisfaying the condition
    guitaristsDF.join(bandsDF,joinCondition,"left_semi").show

    //anti-joins, everything in a left DF for which there is NO row in a right DF satisfaying the condition
    guitaristsDF.join(bandsDF,joinCondition,"left_anti").show

    //things to bear in mind
    // guitaristsBandsDF.select("id","band").show //this crashes
    //option 1 - rename the column which we are joining
    guitaristsDF.join(bandsDF.withColumnRenamed("id","band"),"band")
    //option 2 drop the dupe column
    guitaristsBandsDF.drop(bandsDF.col("id"))
    //option 3 rename the offending column and keep the data
    val bandsModDF = bandsDF.withColumnRenamed("id","bandId")
    guitaristsDF.join(bandsModDF,guitaristsDF.col("band")===bandsModDF.col("bandId"))
    //option 4 using complex types
    guitaristsDF.join(guitarDF.withColumnRenamed("id","guitarId"),expr("array_contains(guitars,guitarId)"))

  }

  def exercises(): Unit ={
    val band_guitarristJoinCondition = guitaristsDF.col("band")===bandsDF.col("id")
    val guitarrist_guitarJoinCondition = guitaristsDF.col("guitars")(0)===guitarDF.col("id")

    //Show the model of guitar used by guitarists of band
    guitaristsDF.join(guitarDF,guitarrist_guitarJoinCondition,"left_outer")
      .join(bandsDF,band_guitarristJoinCondition,"left_outer")
      .select(bandsDF.col("Name"),guitaristsDF.col("name"),guitarDF.col("model"))
      .show
  }

  exercises()
}
