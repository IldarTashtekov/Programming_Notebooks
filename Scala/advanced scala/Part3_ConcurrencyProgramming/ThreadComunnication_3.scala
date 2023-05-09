package Part3_ConcurrencyProgramming

import scala.collection.mutable
import java.util.Random

object ThreadComunnication_3 extends App{

  /*
  the producer-consumer problem

  producer -> [ ? ] -> consumer
  */

  class SimpleContainer {
    private var value:Int = 0
    def isEmpty:Boolean = value == 0
    def set(newValue:Int):Unit = value = newValue
    def get:Int={
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons():Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(()=> {
      println("[consumer] waiting ...")
      while (container.isEmpty){
        println("[consumer] actively waiting")
      }

      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(()=>{
      println("[producer] computing...")
      Thread.sleep(500)
      val value = 42
      println("I have produce after a long work this value "+value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  //dumb way
  //naiveProdCons()

  //to make the same but smarter and safer we gonna use WAIT and NOTIFY
  def smartProdCons():Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(()=> {
      println("[consumer] waiting ...")

      container.synchronized{
        //container is waiting a producer response
        container.wait()
      }
      //container must have some value
      println("[consumer] I have consumed " + container.get)
    })

    val producer = new Thread(()=>{
      println("[producer] computing...")
      Thread.sleep(2000)
      val value = 42
      container.synchronized{
        println("[producer] I have produce after a long work this value "+value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  //smart way
 // smartProdCons()


  //producer -> [buffer] -> consumer
 def prodConsLargeBuffer():Unit ={
   val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
   val capacity = 3

   val consumer = new Thread(()=>{
     val random = new Random()
     while(true){
       buffer.synchronized{
         if(buffer.isEmpty){
           println("[consumer] buffer empty, waiting")
           buffer.wait()
         }
         //there must be at least one value in the buffer
         val x = buffer.dequeue()
         println("[consumer] consumed "+x)

         // hey producer , therese empty space aviable, are you lazy?!
         buffer.notify()
       }
       Thread.sleep(random.nextInt(500))
     }
   })

   val producer = new Thread(() => {
     val random = new Random()
     var i = 0

     while (true) {
       buffer.synchronized {
         if (buffer.size == capacity) {
           println("[producer] buffer is full, waiting...")
           buffer.wait()
         }
         //there must be at least ONE EMPTY SPACE in the buffer
         println("[producer] producing " + i)
         buffer.enqueue(i)

         //hey consumer, new food for you glutton code instance
         buffer.notify()

         i += 1
       }

       Thread.sleep(random.nextInt(500))
     }
   })

   producer.start()
   consumer.start()
 }

  //with queues
  //prodConsLargeBuffer()


  //multiple producers -> [buffer] -> multiple consumers

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread{
    override def run(): Unit = {
      val random = new Random()
      while(true){
        buffer.synchronized{
          while(buffer.isEmpty){ //we change the if to while
            println(s"[consumer $id] buffer empty, waiting")
            buffer.wait()
          }
          //there must be at least one value in the buffer
          val x = buffer.dequeue()
          println(s"[consumer $id] consumed "+x)

          // hey producer , therese empty space aviable, are you lazy?!
          buffer.notify()
        }
        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id:Int, buffer: mutable.Queue[Int], capacity:Int) extends Thread{
    override def run(): Unit = {
        val random = new Random()
        var i = 0

        while (true) {
          buffer.synchronized {
            while (buffer.size == capacity) { //we changed if to while
              println(s"[producer $id] buffer is full, waiting...")
              buffer.wait()
            }
            //there must be at least ONE EMPTY SPACE in the buffer
            println(s"[producer $id] producing " + i)
            buffer.enqueue(i)

            //hey consumer, new food for you glutton code instance
            buffer.notify()

            i += 1
          }

          Thread.sleep(random.nextInt(500))
      }
    }
  }

  def multipleProdConsLargeBuffer():Unit ={
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3
    val numConsumers = 3
    val numProducers = 2

    (1 to numConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to numProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }

    multipleProdConsLargeBuffer()
}

