package Part1_A_taste_of_advanced_scala

object AdvancedPatternMatching extends App{

  val numbers = List(1)

  val description = numbers match {
    //equivalent infix pattern
    //case ::(head, Nil) => println(blabla)
    case head :: Nil => println(s"the only element is $head")
    case _ => println( "I love match patterns" )
  }

  /*
  The only things available for pattern matching are
  -constants (val name)
  -wildcards ( _ )
  -case classes
  -tuples
  -some special magic like above
   */


  //you want make this class compatible with pattern matching, but only case classes can
  class Person(val name:String, val age:Int)
  //first you create this singelton object with an unapply method
  object PersonPattern{
    def unapply(person:Person):Option[(String,Int)] =
      Some((person.name,person.age))
    def unapply(age:Int):Option[String] =
      Some(if(age<21) "minor" else "major")
  }
  //here comes the magic
  val bob = new Person("Bob", 25)
  val greeting = bob match{
    case PersonPattern(n,a) =>s"Hi my name is $n and my age is $a"
  }

  val legalStatus = bob.age match {
    case PersonPattern(status) => s" I am $status"
  }

  println(greeting+legalStatus)

  /*
  Exercise.
  */
  val n:Int = 45

  object singleDigit{
    def unapply(arg:Int):Boolean = arg > -10 && arg < 10
  }

  object even{
    def unapply(arg:Int):Boolean = arg % 2 == 0
  }

  val mathProperty = n match{
    case singleDigit() => "single digit"
    case even() => "an even number"
    case _ => "no property"
  }


  //Infix patterns
  case class Or[A,B](a:A, b:B)
  val either = Or(2,"two")
  val humanDescription = either match{
    //case Or(number, string) => s"$number is written as $string"
     case number Or string => s"$number is written as $string"
  }

  //decomposing sequences
  val vararg = numbers match{
    case List(1,_*)  => "starting with 1"
  }

  abstract class MyList[+A] {
    def head:A = ???
    def tail: MyList[A] = ???
  }

  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head:A, override val tail:MyList[A]) extends MyList[A]

  object MyList{
    //turns the MyList[A] into a Option[Seq[A]] when the sequence holds the same elements in the same order
    def unapplySeq[A](list: MyList[A]):Option[Seq[A]]=
      if(list == Empty) Some(Seq.empty) //if list empty reurns empty sequence
      else unapplySeq(list.tail).map(list.head +: _) //else recursevly call the funct
  }

  //Custom return types for an unapply
  //isEmpty: Boolean, get: something

  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper{
    def unapply(person:Person):Wrapper[String] = new Wrapper[String]{
      def isEmpty = false
      def get:String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This person's name is $n"
    case _ => "An alien"
  })

}

