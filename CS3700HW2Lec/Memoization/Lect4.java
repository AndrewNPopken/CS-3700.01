//Memoization using Map 
import java.util.Map; 
import java.util.HashMap; 
 
public class Lect4{ 
    public static void main(String[] args) { 
        Fibber x = new Fibber(); 
 
        System.out.print(x.fib(10));    //print fibonnaci of 10 
    } 
} 
 
class Fibber { 
 
    private Map<Integer, Integer> memo = new HashMap<>(); 
 
    public int fib(int n) { 
 
        if (n < 0) { 
            throw new IllegalArgumentException( 
                    "Index was negative. No such thing as a negative index in a series."); 
 
            // base cases 
        } else if (n == 0 || n == 1) { 
            return n; 
        } 
 
        // see if we've already calculated this, grab calculated value 
        if (memo.containsKey(n)) { 
            System.out.printf("grabbing memo[%d]\n", n); 
            return memo.get(n); 
        } 
 
        System.out.printf("computing fib(%d)\n", n); 
        int result = fib(n - 1) + fib(n - 2); 
 
        // memoize 
        memo.put(n, result);//save results in map 
 
        return result; 
    } 
} 