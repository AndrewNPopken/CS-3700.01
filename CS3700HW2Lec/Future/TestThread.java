//Future with ExecutorService passing Callable NOTE: will execute call() vs run() for Runnable 
import java.util.concurrent.Callable; 
import java.util.concurrent.ExecutionException; 
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 
import java.util.concurrent.Future; 
 
public class TestThread { 
 
   public static void main(final String[] arguments) throws InterruptedException, 
      ExecutionException { 
 
      ExecutorService executor = Executors.newSingleThreadExecutor(); 
 
      System.out.println("Factorial Service called for 10!"); 
      Future<Long> result10 = executor.submit(new FactorialService(10)); //start executing factorial of 10 NONBLOCKING 
 
      System.out.println("Factorial Service called for 20!"); 
      Future<Long> result20 = executor.submit(new FactorialService(20)); //start executing factorial of 20 NONBLOCKING 
 
      Long factorial10 = result10.get();    //main thread will wait, block, until value is returned 
      System.out.println("10! = " + factorial10); 
 
      Long factorial20 = result20.get(); //main thread will wait, block, until value is returned 
      System.out.println("20! = " + factorial20); 
 
      executor.shutdown();  //stop all threads 
   }   
 
   static class FactorialService implements Callable<Long> { 
      private int number; 
 
      public FactorialService(int number) { 
         this.number = number; 
      } 
 
      @Override 
      public Long call() throws Exception { //threads will start executing here 
         return factorial(); 
      } 
 
      private Long factorial() throws InterruptedException { 
         long result = 1;  
          
         while (number != 0) {  
            result = number * result;  
            number--;  
            Thread.sleep(100);  
         } 
         return result;  
      } 
   } 
} 