//AtomicInteger -->NOTE: creating over a 1000 threads 
import java.util.concurrent.atomic.AtomicInteger; 
 
public class TestThread { 
 
   static class Counter { 
      private AtomicInteger c = new AtomicInteger(0); 
 
      public void increment() { 
         c.getAndIncrement(); // c will be read and incremented as one step. No context switch  
      } 
 
      public int value() { 
         return c.get();  //atomic operation to read value of c 
      } 
   } 
    
   public static void main(final String[] arguments) throws InterruptedException { 
      final Counter counter = new Counter(); 
       
      //1000 threads are created 
      for(int i = 0; i < 1000 ; i++) { 
 
         new Thread(new Runnable() { 
            public void run() {  //threads will starts running here 
               counter.increment();   
            } 
         }).start();  
      }   
      Thread.sleep(6000);  //wait 6 seconds 
      System.out.println("Final number (should be 1000): " + counter.value()); 
   } 
} 