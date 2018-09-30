//ConcurrentNavigableMap - subinterface of ConcurrentMap implements ConcurrentSkipListMap which is a concurrent TreeMap 
import java.util.concurrent.ConcurrentNavigableMap; 
import java.util.concurrent.ConcurrentSkipListMap; 
 
public class TestThread { 
 
   public static void main(final String[] arguments) { 
      ConcurrentNavigableMap<String,String> map = 
         new ConcurrentSkipListMap<String, String>(); 
 
      map.put("1", "One");   //will guarantee NOT to interfere with other threads when inserting put() 
      map.put("2", "Two"); 
      map.put("3", "Three"); 
      map.put("5", "Five"); 
      map.put("6", "Six"); 
 
      System.out.println("Initial ConcurrentHashMap: "+map); 
      System.out.println("HeadMap(\"2\") of ConcurrentHashMap: "+map.headMap("2")); 
      System.out.println("TailMap(\"2\") of ConcurrentHashMap: "+map.tailMap("2")); 
      System.out.println( 
         "SubMap(\"2\", \"4\") of ConcurrentHashMap: "+map.subMap("2","4")); 
   }   
} 