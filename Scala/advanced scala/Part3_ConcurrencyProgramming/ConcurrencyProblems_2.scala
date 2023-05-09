package Part3_ConcurrencyProgramming

object ConcurrencyProblems_2 {

  def runInParallel():Unit = {

    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    println(x)


  }

  case class BankAccount(var amount: Int)

  def buy(bankAccount: BankAccount, thing:String, price:Int):Unit ={
    bankAccount.amount -= price
  }

  //syncronized
  def buySafe(bankAccount: BankAccount, thing:String, price:Int):Unit ={
    bankAccount.synchronized{ //does not allow multiple threads run this section AT THE SAME TIME
      bankAccount.amount -= price //critical section
    }
  }

  def demoBankingProblem():Unit ={
    (1 to 1000).foreach{ _ =>
      val account = BankAccount(50000)
      val thread1 = new Thread(() => buy(account, "shoes", 3000)) //unsafe
      val thread2 = new Thread(() => buySafe(account, "iPhone", 4000))//safe
      thread1.start()
      thread2.start()
      thread1.join()
      thread2.join()
      if(account.amount != 43000) println(s"AHA I just broken the bank: ${account.amount}")
     }
  }

  def main(args: Array[String]): Unit = {
    //first concurrent problem is unexpected results
    //the tecnical name of this is RACE CONDITION
    runInParallel() //somethimes prints 1 and sometimes prints 2

    //to resolve race condition a useful thing is syncronization
    demoBankingProblem()

  }
}
