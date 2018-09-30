//BlockingQueue 
import java.util.Random; 
import java.util.concurrent.ArrayBlockingQueue; 
import java.util.concurrent.BlockingQueue; 
 
public class TestThread { 
 
   public static void main(final String[] arguments) throws InterruptedException { 
      BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(10);   //create queue size 10 
 
      Producer producer = new Producer(queue);  //pass queue to producer 
      Consumer consumer = new Consumer(queue);  //pass queue to consumer 
 
      new Thread(producer).start(); //exectute a new thread start at Producer.run() 
      new Thread(consumer).start(); //exectute a new thread start at Consumer.run() 
 
      Thread.sleep(4000);   //put main thread to sleep for 4 seconds. Note program will end when it wakes up 
   }   
 
 
   static class Producer implements Runnable { 
      private BlockingQueue<Integer> queue; 
 
      public Producer(BlockingQueue queue) { 
         this.queue = queue; 
      } 
 
      @Override 
      public void run() {       //producer thread starts here  
         Random random = new Random(); 
 
         try { 
            int result = random.nextInt(100); 
            Thread.sleep(1000); 
            queue.put(result);  //this is atomic. Do not have to worry about thread interference 
            System.out.println("Added: " + result); 
             
            result = random.nextInt(100); 
            Thread.sleep(1000); 
            queue.put(result);  //this is atomic. 
            System.out.println("Added: " + result); 
             
            result = random.nextInt(100); 
            Thread.sleep(1000); 
            queue.put(result);  //this is atomic. 
            System.out.println("Added: " + result); 
         } catch (InterruptedException e) { 
            e.printStackTrace(); 
         } 
      }     
   } 
 
   static class Consumer implements Runnable { 
      private BlockingQueue<Integer> queue; 
 
      public Consumer(BlockingQueue queue) { 
         this.queue = queue; 
      } 
       
      @Override 
      public void run() {   //consumer thread starts here 
          
         try { 
            System.out.println("Removed: " + queue.take());//this is atomic. 
            System.out.println("Removed: " + queue.take());//this is atomic. 
            System.out.println("Removed: " + queue.take());//this is atomic. 
         } catch (InterruptedException e) { 
            e.printStackTrace(); 
         } 
      } 
   } 
} 