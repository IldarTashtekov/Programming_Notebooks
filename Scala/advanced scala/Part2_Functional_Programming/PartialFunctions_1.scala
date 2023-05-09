package Part2_Functional_Programming

object PartialFunctions_1 extends App {

  val aFunction = (x:Int) => x + 1 //Function[Int,Int] === Int => Int

  //have a aprtial input or/and output scope
  val aPartialFunction:PartialFunction[Int,Int] = {
    case 1 => 42
    case 2 => 1
  }

  //Partial function utilities
  println(aPartialFunction.isDefinedAt(3)) //returns false

  val lifted = aPartialFunction.lift //Int => Option[Int]
  println(lifted(2))
  println(lifted(8))

  val pfChain = aPartialFunction.orElse[Int,Int]{
    case 45 => 67
  }

  println(pfChain(2)) //returns 1
  println(pfChain(45))//returns 67

  //HOF's accept partial functions as well
  val aMappedList = List(1,2,3).map{
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }

  /*
  Note: PF can only have ONE parameter type
  */

  /**
   * Exercises
   *
   * 1- construct a PF instance yourself (anonymous class)
   * 2- dumb chatbot as a PF
   */



  //2 exercise
  val partialChatbotAnswer:PartialFunction[String,String] = {
    case "hello" => "fuck you human"
    case "hi" => "fuck you human"
    case "goodbye" => "bye hoonie"
  }
  scala.io.Source.stdin.getLines().foreach(line => println("you said : " + line +"\n bot answer : "+partialChatbotAnswer(line))
  )


}

