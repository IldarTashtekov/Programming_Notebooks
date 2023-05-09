package Part2_Functional_Programming

object LazyEvaluation_4 extends App {

  //lazy DELAYS the evaluation of values
  lazy val x:Int ={
    println("hello")
    42
  }
  println(x) //returns hello 42
  println(x) //returns only 42


  //examples and implications
  def sideEffectCondition:Boolean ={
    println("Boo")
    true
  }
  def simpleCondition:Boolean = false
  lazy val lazyCondition = sideEffectCondition
  //the first condition is false, the second condition is not evaluated, for this reason the side effect "Boo" is not gonna appear in the screan
  println(if(simpleCondition && lazyCondition) "yes" else "not")

  // in conjuction with call by name
  //with this method the number is evaluated 3 times and we have the side effect 3 times
  def byNameMethod(n: => Int):Int = n + n + n + 1
  //with this not, only once
  def byNameMethodBetter(n: => Int):Int = {
    //this technique is called CALL BY NEED
    lazy val t = n
    t + t + t + 1
  }
  def retrieveMagicValue={
    //side effect or long computation
    println("waiting")
    Thread.sleep(1000)
    42
  }

  println(byNameMethodBetter(retrieveMagicValue))


  //use lazy vals
  def lessThan30(i:Int):Boolean ={
    println(s"$i is less than 30")
    i > 30
  }

  def greaterThan20(i:Int):Boolean ={
    println(s"$i is bigger than 20")
    i < 20
  }


  //filtering with lazy vals
  val numbers = List(1, 25, 30, 5, 23)
  val lt30 = numbers.filter(lessThan30) //List(1, 25, 5. 23)
  val gt20 = lt30.filter(greaterThan20)//List(23, 25)

  val lt30Lazy = numbers.withFilter(lessThan30) //lazy vals under the hood
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  println
  println(gt20Lazy) //methods are not evaluated
  gt20Lazy.foreach(println) //they are evaluated

  //for-comprehensions use withFilter with guards
  for{
    a <- List(1,2,3) if a % 2 == 0
  }yield a + 1
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1) //this line evaluate all lazy values and transform them again to types


  /*
  Exercise: implement a lazily evaluated, singly linked STREAM of elements.

  naturals = MyStream.from(1)(x => x + 1) = stream of natural numbers (potentially infinite!)
  naturals.take(100).foreach(println) //laizly evaluated stream of the first 100 naturals, finite stream
  naturals.foreach(println) will crash becouse infinite
  naturals.map(_ * 2) //stream of all even numbers (potentially infinite)
  */

  abstract class MyStream[+A]{
    def isEmpty: Boolean
    def head: A
    def tail: MyStream[A]
    def #::[B >: A](element: B): MyStream[B] //prepend operator
    def ++ [B >: A] (anotherStream: MyStream[B]): MyStream[B] //concatenate two streams

    def foreach(f: A => Unit):Unit
    def map[B](f:A => MyStream[B]): MyStream[A]

    def take(n:Int): MyStream[A] //takes the dirst n elements out of this stream
    def takeAsList(n:Int): List[A]
  }


  object MyStream {
    def from[A](start: A)(generator: A => A): MyStream[A] = ???
  }

}
