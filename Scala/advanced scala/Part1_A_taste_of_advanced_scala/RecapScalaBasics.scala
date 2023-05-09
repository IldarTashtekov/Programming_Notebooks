package Part1_A_taste_of_advanced_scala

import scala.annotation.tailrec

object RecapScalaBasics extends App {

  //instructions vs expressions
  val aBoolean = false
  val aConditionedVal = if (aBoolean) 42 else 65

  //Code Block
  val aCodeBlock = {
    if (aBoolean) 54 else 10
  }

  //recursion: stack and tail, basiclly transforms recursions into a loop without stackoverflows, cool shit
  @tailrec def factorial(n: Int, accumulator: Int): Int =
    if (n <= 0) accumulator
    else factorial(n - 1, n * accumulator)

  //object oriented prgramming
  class Animal

  class Dog extends Animal

  val aDog: Animal = new Dog //subtyping polymorphism

  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("ñom ñom")
  }

  //method notations

  val aCroc = new Crocodile
  aCroc.eat(aDog) //classic method notation
  aCroc eat aDog //natural language

  //anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = print("roar!")
  }

  // generics
  abstract class MyList[+A]

  // singletons and companions
  object MyList

  // case classes
  case class Person(name: String, age: Int)

  //try catch exceptions
  val throwsExceptions = throw new RuntimeException //Nothing Type

  val aPotentialFailure = try {
    throw new Exception
  } catch {
    case e: Exception => "I caught an exception"
  } finally {
    println("some logs")
  }

  //functional programming
  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  incrementer(1)

  val anonymousIncrementer = (x: Int) => x + 1

  List(1, 2, 3).map(anonymousIncrementer) //HOF, Higher Order Function

  //map, flatMap, filter
  //for-comprehension
  val pairs = for {
    num <- List(1, 2, 3)
    char <- List('a', 'b', 'c')
  } yield num + "-" + char //cross paring between every number and char

  //Scala collections: Seq, Arrays, Lists, Vectors, Maps, Tuples
  val aMap = Map(
    "Daniel" -> 789,
    "Jess" -> 555
  )

  //"collections": Options, Try
  val anOption = Some(2)

  //pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => x + "th"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
  }


}
