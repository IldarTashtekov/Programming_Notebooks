package a_my_playground.part3_lowLevelAPI

import common.Person
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

import java.io.File
import java.sql.Date
import java.time.{LocalDate, Period}

object DStream_Transformations {

  val spark: SparkSession = SparkSession.builder().appName("DStream_Transformation").master("local[2]").getOrCreate()
  val ssc = new StreamingContext(spark.sparkContext,Seconds(1))

  def readPeople() = ssc.socketTextStream("localhost",9999)
    .map{ line =>
      val tokens = line.split(":")
      Person(
        tokens(0).toInt, //id
        tokens(1), //first name
        tokens(2), //middle name
        tokens(3), //last name
        tokens(4), //gender
        Date.valueOf(tokens(5)), //birth date
        tokens(6), //id
        tokens(7).toInt //salary
      )
    }

  //maps
  def peopleAges():DStream[(String,Int)]=readPeople().map({ person =>
    //person age
    val age = Period.between(person.birthDate.toLocalDate, LocalDate.now()).getYears

    //Tuple(String, Int)
    (s"${person.birthDate} ${person.lastName}",age)
  })

  def peopleSmallNames():DStream[String] = readPeople().flatMap{ person =>
    List(person.firstName,person.middleName)
  }

  //filters
  def hightIncomePeople(): DStream[Person] = readPeople().filter(_.salary > 8000)

  //count
  def countPeople():DStream[Long] = readPeople().count() //the number of every batch

  //count by value, PER BATCH
  def countNames():DStream[(String,Long)] = readPeople().map(_.firstName).countByValue()

  /*reduce by Key
    -works on DStream of touples
    -works PER BATCH
  * */
  def countNamesReduce():DStream[(String,Int)] = readPeople()
    .map(_.firstName)
    .map(name => (name,1))
    //when we get two touples with the same name, the value at the second is gonna
    // be reduced by a value we provide, in that case a sum function
    .reduceByKey((a,b) => a+b)

  //for each RDD
  import spark.implicits._
  def saveToJson()=readPeople().foreachRDD{rdd=>
    val ds = spark.createDataset(rdd)
    val f = new File("src/main/resources/people")
    val nFiles = f.listFiles().length
    val path = s"src/main/resources/people/people${nFiles}.json"

    ds.write.json(path)
  }

  //bash command
  // cat src/main/resources/data/people-1m/people-1m.txt | ncat.exe -lk 9999
  def main(args: Array[String]): Unit = {
    val stream = countNamesReduce()
    stream.print()

    ssc.start()
    ssc.awaitTermination()

  }
}
