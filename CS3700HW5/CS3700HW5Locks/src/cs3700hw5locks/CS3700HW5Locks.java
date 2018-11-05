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
public class CS3700HW5Locks {
    static Buffer buffer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        buffer = new Buffer();
        for (int i=0,j=5;i<j;i++){
            (new Thread(new Producer(buffer))).start();
        }
        for (int i=0,j=2;i<j;i++){
            (new Thread(new Consumer(buffer))).start();
        }
//        for (int i=0,j=2;i<j;i++){
//            (new Thread(new Producer(buffer))).start();
//        }
//        for (int i=0,j=5;i<j;i++){
//            (new Thread(new Consumer(buffer))).start();
//        }
    }
    
}
