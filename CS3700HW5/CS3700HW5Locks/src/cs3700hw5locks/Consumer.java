/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw5locks;

/**
 *
 * @author Andrew
 */
public class Consumer implements Runnable {

    private final Buffer buffer;
    private int failCount;
    private final int failTolerance;

    Consumer(Buffer b) {
        buffer = b;
        failCount = 0;
        failTolerance = 3;
    }

    @Override
    public void run() {
        synchronized (buffer) {
            while (true) {
                if (buffer.remove() == null) {
                    failCount++;
                    if (failCount == failTolerance) {
                        System.out.println(Thread.currentThread().getName() + ": Ending.");
                        return;
                    }
                } else if (failCount != 0) {
                    failCount = 0;
                }
                try {
                    buffer.wait(1000);
                } catch (InterruptedException ex) {

                }
            }
        }
    }
}
