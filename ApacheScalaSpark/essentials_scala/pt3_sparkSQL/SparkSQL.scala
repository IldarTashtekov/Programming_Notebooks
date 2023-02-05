package pt3_sparkSQL

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.sql.functions._

object SparkSQL extends App{

  val spark = SparkSession.builder()
    .appName("Spark SQL")
    .config("spark.master","local")
    //change the spark sql warehouse dir location
    .config("spark.sql.warehouse.dir","src/main/resources/warehouse")
    //if you use spark 2.4 to overrride sapark-sql tables you need to put the next
    .config("sparl.sql.legacy.allowCreatingManagedTableUsingNonemptyLocation","true")
    .getOrCreate()

  val carsDF = spark.read.option("inferSchema","true").json("src/main/resources/data/cars.json")

  val driver = "org.postgresql.Driver"
  val url = "jdbc:postgresql://localhost/test_db"
  val user = "postgres"
  val password = "pgAdmin"

  def readTable(tableName: String) = spark.read
    .format("jdbc")
    .option("driver", driver)
    .option("url", url)
    .option("user", user)
    .option("password", password)
    .option("dbtable", s"public.$tableName")
    .load()

  def transferTables(tablesNames:List[String],shouldWriteToWarehouse: Boolean = false) =tablesNames.foreach{tableName =>
    val tableDF = readTable(tableName)
    tableDF.createOrReplaceTempView(tableName)
    if (shouldWriteToWarehouse) {
      tableDF.write
        .mode(SaveMode.Overwrite)
        .saveAsTable(tableName)
    }
  }

  def sparkSQL_basics(): Unit ={
    //regular DF API
    carsDF.select(col("Name")).where(col("Origin") === "USA").show(5)

    //use Spark SQL
    carsDF.createOrReplaceTempView("cars")
    spark.sql(
      """
        |select Name from cars where Origin = 'USA'

    """.stripMargin
    ).show(5)

    //create databases, run different sql statements
   // spark.sql("create database test_db")
   // spark.sql("use test_db")
   // spark.sql("show databases").show()

  }

  def write_sql_sparkSQL(): Unit ={
    /*
    //how to TRANSFER tables from sql to spark tables
    //first get sql db
    val empDF = readTable("emp")
    empDF.show()

    //write the sql table to sparkSql table
    empDF.write
      .mode(SaveMode.Overwrite)
      .saveAsTable("employees")
    //we automatize this process with the next functions: transferTables, readTable
  */

    transferTables(List("emp","client","dept","detall","comanda","producte"),true)
  }

  def readDF_from_warehouse(): Unit ={
    spark.read.table("emp").show()
  }

  def exercises(): Unit ={
    /**
     * Exercises
     * 1- Read the moviesDF and store it as a Spark table in test_db database
     * 2- Count how many employees where hired in 1980
     * 3- Show the avg salaries of 1980 employees and 1981 employees
     * 4- Show best payed departaments
     */

    //1
    //val moviesDF = spark.read.option("inferSchema","true").json("src/main/resources/data/movies.json")
    //moviesDF.write.mode(SaveMode.Overwrite).saveAsTable("movies")

    //2
    spark.sql(
      """
        |select (*) from emp
        |where data_alta < '1981-01-01'
        |
      """.stripMargin
    ).show()
  }

  //sparkSQL_basics()
  //write_sql_sparkSQL()
  //readDF_from_warehouse()
  exercises()


}
