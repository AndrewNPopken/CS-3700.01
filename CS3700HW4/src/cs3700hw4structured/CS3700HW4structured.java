/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw4structured;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 *
 * @author Andrew
 */
public class CS3700HW4structured {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Fork forks[] = new Fork[5];
        Arrays.parallelSetAll(forks, (i) -> new Fork());
        Philosopher A = new Philosopher("A", forks[0], forks[1]);
        Philosopher B = new Philosopher("B", forks[1], forks[2]);
        Philosopher C = new Philosopher("C", forks[2], forks[3]);
        Philosopher D = new Philosopher("D", forks[3], forks[4]);
        Philosopher E = new Philosopher("E", forks[4], forks[0]);
        ExecutorService PhiloExecutor = Executors.newFixedThreadPool(5);
        PhiloExecutor.execute(A);
        PhiloExecutor.execute(B);
        PhiloExecutor.execute(C);
        PhiloExecutor.execute(D);
        PhiloExecutor.execute(E);
        try {
            if (!PhiloExecutor.awaitTermination(1200, SECONDS)) {
                PhiloExecutor.shutdownNow();
                Thread.sleep(100);
                System.out.println("Test lasted 20 minutes");
            }
        } catch (InterruptedException e) {
            PhiloExecutor.shutdownNow();
        }
    }

}
