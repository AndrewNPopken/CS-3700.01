/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw5atomics;

/**
 *
 * @author Andrew
 */
public class Producer implements Runnable {

    private final Buffer buffer;
    private final Object waitLock;

    Producer(Buffer b) {
        this.waitLock = new Object();
        buffer = b;
    }

    @Override
    public void run() {
        for (int i = 0, j = 100; i < j; i++) {
            synchronized (waitLock) {
                while (!buffer.add(Thread.currentThread().getName() + "-" + i)) {
                    try {
                        waitLock.wait(1100);
                    } catch (InterruptedException ex) {

                    }
                }
            }
        }
    }

}
