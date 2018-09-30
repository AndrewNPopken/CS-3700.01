//ExecutorService -->subinterface of Executor submit() -->Runnable or Callable Objects  
 
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors; 
import java.util.concurrent.TimeUnit; 
 
public class TestThread { 
 
   public static void main(final String[] arguments) throws InterruptedException { 
      ExecutorService executor = Executors.newSingleThreadExecutor(); 
 
      try { 
         executor.submit(new Task()); 
         System.out.println("Shutdown executor"); 
         executor.shutdown();   //shutdown after thread finishes executing 
         executor.awaitTermination(5, TimeUnit.SECONDS);    //wait up to 5 seconds or for thread to finish or interrupt occurs, whichever is first 
      } catch (InterruptedException e) {    //here --> if exception was thrown 
         System.err.println("tasks interrupted"); 
      } finally {   //here if 5 seconds is up 
 
         if (!executor.isTerminated()) {    //here if thread is still running 
            System.err.println("cancel non-finished tasks"); 
         } 
         executor.shutdownNow();    //force thread to shutdown 
         System.out.println("shutdown finished"); 
      } 
   } 
 
   static class Task implements Runnable { 
       
      public void run() { 
          
         try { 
            Long duration = (long) (Math.random() * 20); 
            System.out.println("Running Task!"); 
            TimeUnit.SECONDS.sleep(duration); 
         } catch (InterruptedException e) { 
            e.printStackTrace(); 
         } 
      } 
   }        
} 