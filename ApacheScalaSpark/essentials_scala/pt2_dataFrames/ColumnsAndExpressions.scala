package pt2_dataFrames

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, column, expr}

object ColumnsAndExpressions extends App{
  val spark = SparkSession.builder()
    .appName("DF Columns and Expressions")
    .config("spark.master","local")
    .getOrCreate()

  val carsDF = spark.read
    .option("inferSchema", "true")
    .json("src/main/resources/data/cars.json")

  carsDF.show(4)

  //COLUMNS
  def columns(): Unit ={
    //in this way we get a column from DF
    val firstColumn = carsDF.col("Name")

    // in this way we create a DF with a specific colums from other DF
    //the tecnical term is projection
    val carNamesDF = carsDF.select(firstColumn)
    carNamesDF.show()

    //various select methods
    //you can use many methods
    // DF.col
    // import org.apache.spark.sql.functions.{col,column, expr}
    // implicit "Column name"
    /*
    import spark.implicits._
    carsDF select(
      carsDF.col("Name"),
      col("Acceleration"),
      column("Weigth_in_lbs"),
      "Year", //scala symbol auto converted to column
       $"Horsepower", //fancier interpolated string returns a Column Object
      expr("Origin") //Expression
    )
  */
    //Another method
    carsDF.select("Name","Year","Acceleration").show(4)
  }

  //Selecting column names is the simpliest version of Expresions
  //EXPRESSIONS
  def expressions(): Unit ={
    val simpliestExpression = carsDF.col("Weight_in_lbs")
    val weigthInKgExpression = carsDF.col("Weight_in_lbs") / 2.2

    val carsWithWeightsDF = carsDF.select(
      col("Name"),
      col("Weight_in_lbs"),
      weigthInKgExpression.as("Weight_in_kg"),
      expr("Weight_in_lbs / 2.2").as("Weight_in_kg 2") //the same as Weight in kg but using expr()

    )
    carsWithWeightsDF.show(4)

    //selectExpr method
    val carsWithSelectExprDF = carsDF.selectExpr(
      "Name",
      "Weight_in_lbs",
      "Weight_in_lbs / 2.2"
    )

    //DF processing
    val carsWithKG3DF = carsDF.withColumn("Weight_in_kg_3", col("Weight_in_lbs")/2.2)
    //renaming a column
    val carsWithColumnRenamed = carsDF.withColumnRenamed("Weight_in_lbs","Weight in pounds")
    //careful with column names
    carsWithColumnRenamed.selectExpr("`Weight in pounds`")
    //remove columns
    carsWithColumnRenamed.drop("Cylinders","Displacement")

    //filtering
    //=!= is !=, and === is ==
    val notUSACars = carsDF.filter(col("Origin") =!= "USA")
    val europeanCarsDF2 = carsDF.where(col("Origin") =!= "USA")
    //filtering with expressions
    val americanCarsDF = carsDF.filter("Origin = 'USA'")
    //chain filters
    val americanPowerfulCarsDF = carsDF.filter(col("Origin")==="USA").filter(col("Horsepower")>150)
    val americanPowerfulCarsDF2 = carsDF.filter(col("Origin")==="USA" and col("Horsepower")>150)
    val americanPowerfulCarsDF3 = carsDF.filter("Origin = 'USA' and Horsepower > 150")

    //unioning, adding more rows
    val moreCarsDF = spark.read.option("inferSchema","true").json("src/main/resources/data/more_cars.json")
    val allCarsDF = carsDF.union(moreCarsDF) //works if the DF have the same schema

    //distinct values
    val allCountriesDF = carsDF.select("Origin").distinct()
    allCountriesDF.show()

  }

  def exercises(): Unit ={
    /**
     * Exercises
     *
     * 1.Read the movies DF and select 2 columns of you choice
     * 2.Create a new column summing a total profit of the movie = US_Gross+Word_Gross
     * 3.Select all the comedy movies with IMDB rating above 7
     *
     * Use as many methods are possible, try a be creative
     */
    //val firstExercise = carsDF.select("Name","Horsepower").show()

    val moviesDF = spark.read.option("inferSchema","true").json("src/main/resources/data/movies.json")

    val secondExercise = moviesDF.withColumn("Total Income",
                                             col("US_Gross")
                                             +col("Worldwide_Gross")
    )
    secondExercise.show(10)

    //val thirdExcercise = moviesDF.filter("Major_Genre = 'Comedy' and  IMDB_Rating > 7").show()


  }

  exercises()
}
