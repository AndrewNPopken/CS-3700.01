/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw5locks;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrew
 */
public class Producer implements Runnable {

    private final Buffer buffer;

    Producer(Buffer b) {
        buffer = b;
    }

    @Override
    public void run() {
        for (int i = 0, j = 100; i < j; i++) {
            synchronized(buffer){
                while (!buffer.add(new Object())){
                    try {
                        buffer.wait(1100);
                    } catch (InterruptedException ex) {
                        
                    }
                }
            }
        }
    }

}
