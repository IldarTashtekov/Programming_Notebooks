package Part2_Functional_Programming

object CurryingAndPartiallyApliedFunctions_3 extends App{

  //curried functions
  //is a function that takes an int and return another function that returns an int
  val superAdder:Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // Int => Int => y => 3+y
  println(add3(4))
  println(superAdder(3)(4))

  def curriedAdder(x:Int)(y:Int): Int = x + y //curried method

  val add4:Int => Int = curriedAdder(4)
  // functions != methods(def) in jvm internal work
  //transforming a method to a function is called LIFTING or ETA-EXPANSION
  def inc(x:Int)=x+1
  List(1,2,3).map(inc) //here the compiler transforms the method inc to a function

  //this syntax lift the def to a function in scala 2 and early 3, now is an automatical process
  val add5 = curriedAdder(5) _

  /**EXERCISE**/
  val simpleAddFunction = (x:Int, y:Int) => x + y
  def simpleAddMethod(x:Int, y:Int) = x + y
  def curriedAddMethod(x:Int)(y:Int) = x + y

  /**
    define an add7: Int => Int = y => 7 + y
    as many different implementations of add7 using the above be creative!
  **/
  var add7 = (x:Int) => simpleAddFunction(7,x)
  val add7_2 = simpleAddFunction.curried(7)

  val add7_3 = curriedAddMethod(7) _ //PAF
  val add7_4 = curriedAddMethod(7)(_) //PAF = alternative syntax

  val add7_5 = simpleAddMethod(7 , _:Int)//alternative syntax for turning methods into function values
  val add7_6 = simpleAddFunction(7, _:Int) //works in functions as well

  //underscores are powerful, curried example
  def concatenator(a:String, b:String, c:String):String = a + b + c
  val insertName = concatenator("Hello , I'm " , _:String, " how are you")
  println(insertName("Daniel"))

  /**
   * EXERCISES
   *
   * 1 Process a list of numbers and return their string representations with different formats
   *    - Use the %4.2f,%8.6g and %14.12f with curried formatter function
   */

  def curriedFormater(formatString:String)(number:Double) = formatString format number
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
  val simpleFormat = curriedFormater("%14.12f")
  println(numbers.map(simpleFormat))


  /** 2 difference between
   *    - functions vs methods
   *    - parameters: by-name vs 0-lambda
   */

  def byName(n: => Int):Int = n + 1
  def byFunction(f: ()=> Int):Int = f() + 1
  def method: Int = 42
  def parenMethod(): Int = 42

  /*
  Calling by name and by Function
  -int
  -method
  -partenMethod
  -lambda
  -PAF
  * */

  byName(23) //ok
  byName(method) //ok
  byName(parenMethod()) //ok
  //byName(parenMethod) scala 3 dont compile methods without parentesis
  //byName(()=>2) not ok, byName only accepts Ints
  byName((()=>42)()) //its okay becouse the input is the output of the lambda
  //byFunction(45) not ok, expects lambda
  //byFunction(method) not ok, parameterles method. Compiler dont do ETA-expansion
  byFunction(parenMethod) //its ok, ETA expansion done
  byFunction(()=>45) //its ok, becuse you are passing a function yeah sape
}
