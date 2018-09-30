//ExecutorService -->subinterface of ExecutorService use schedule()-->execute task after specified delay 
 
import java.util.concurrent.Executors; 
import java.util.concurrent.ScheduledExecutorService; 
import java.util.concurrent.ScheduledFuture; 
import java.util.concurrent.TimeUnit; 
 
public class TestThread { 
 
   public static void main(final String[] arguments) throws InterruptedException { 
      final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); //create 1 thread in ThreadPool 
 
      final ScheduledFuture<?> beepHandler =  
         scheduler.scheduleAtFixedRate(new BeepTask(), 2, 2, TimeUnit.SECONDS); //initially wait delay then repeat after specified delay 
                    //also try scheduler.scheduleWithFixedDelay 
      scheduler.schedule(new Runnable() { 
 
         @Override 
         public void run() { 
            beepHandler.cancel(true); 
            scheduler.shutdown();            
         } 
      }, 10, TimeUnit.SECONDS); 
   } 
 
   static class BeepTask implements Runnable { 
       
      public void run() { 
         System.out.println("beep");       
      } 
   } 
} 