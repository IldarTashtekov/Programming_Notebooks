package Part3_ConcurrencyProgramming

import java.util.concurrent.Executors

object Intro_Concurrency_1 extends App{


  /*
  interface Runnable {
    public void run()
  }
  */

  //JVM Threads
  val aThread = new Thread(new Runnable{
    override def run(): Unit = println("running in paralel")
  })

  //creates a JVM thread on top of OS thread
  aThread.start()
  //join blocks until aThread finishes running
  aThread.join()

  //diffrent runs produce diffrent results
  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("adios")))
  threadHello.start()
  threadGoodbye.start()

  //executors
  val pool =  Executors.newFixedThreadPool(10)
  pool.execute(()=> println("something in the Thread Pool"))

  pool.execute(()=>{
    Thread.sleep(1000)
    println("done after 1 seg")
  })

  pool.execute(()=>{
    Thread.sleep(2000)
    println("done after 2 seg")
  })

  //shutdown the pool, we cant execute nothing after that
  //pool.shutdown()
  //pool.execute(() => println("should not appear")) //throws a exeptions in the Main thread

  //this method interrumps the sleeping threads that currently running in the pool
  //pool.shutdownNow()

  //this method says if the pool is shutdown
  println(pool.isShutdown)



}
