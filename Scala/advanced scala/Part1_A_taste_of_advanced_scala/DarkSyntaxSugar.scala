package Part1_A_taste_of_advanced_scala

import scala.math.random
import scala.util.Try

object DarkSyntaxSugar extends App{

 //syntax sugar #1 : methods with single param
  def singleArgMethod(arg:Int): String = s"number $arg"

  val description = singleArgMethod{
    if(random() < 0.5) 8 else 5
  }

  val aTryInstance = Try{
    throw new RuntimeException
  }

  List(1,2,3).map{ x =>
    x+1
  }

  //syntax sugar #2 : single abstract method
  trait Action{
    def act(x:Int):Int
  }

  val anInstance:Action = new Action { //without signle abstract method
    override def act(x: Int): Int = x+1
  }

  val aFunkyInstance:Action = (x:Int)=> x+1 //compile the anonymus function as override act mehtod

  val aSweeterThread = new Thread(() => println("sweet sweet scala syntax sugar"))

  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(a:Int):Unit
  }
  //now f is this anonymys function
  val anAbstractTypeInstance = (a:Int) => println( "too sweet , bad for my caries")


  //syntax sugar #3: the :: and #:: methods are special... and sweet
  val prependedList = 1 :: 2 :: List(3,4)

  class MyStream[T]{
    def -->:(value:T): MyStream[T] = this
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]

  // syntax sugar #3 multi-word method naming
  class TeenGirl (name:String){
    def `and then said`(gossip:String):Unit = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "I cant handle with scala sweetness because of my diabetes"


  // syntax sugar #5 infix types
  class Composite[A,B]
  //we can use this salty method
  //val composite:Composite[Int,String] = ???
  //or this sweet method
  val composite: Int Composite String = ???

  class -->[A,B]
  val towards:Int --> String = ???

  //syntax sugar #6 update() is very special, much like apply()
  var anArray = Array(1,2,3)
  anArray(2)=7 //rewritten to anArray.update(2,7)
  //used in mutable collections
  //remember apply() and update()!

  //syntax sugar #7: setters for mutable containers
  class Mutable{
    private var internalMember:Int = 0 //private fir oo encapsulation
    def member:Int = internalMember //"getter"
    def member_=(value:Int):Unit =
      internalMember=value // "setter"
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 32 //reweitten as aMutableContainer.member_=42
}
