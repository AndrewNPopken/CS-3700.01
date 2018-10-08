/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neighboraveraging;

import java.util.ArrayList;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrew
 */
public class NeighborAveraging {

    static ArrayList<Double> array;
    static ArrayList<Phaser> phaserArray;
    static ArrayList<PhaserNeighborAvg> threadArray;
    static final int SIZE = 100;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        array = new ArrayList<>(SIZE);
        phaserArray = new ArrayList<>(SIZE);
        threadArray = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            array.add(ThreadLocalRandom.current().nextDouble(0, 100));
        }
        for (int i = 0; i < SIZE; i++) {
            phaserArray.add(new Phaser());
        }
        for (int i = 0; i < SIZE; i++) {
            threadArray.add(new PhaserNeighborAvg(i));
        }
        System.out.println("Array before averaging:");
        for (int i = 0; i < SIZE; i++) {
            System.out.printf("%.2f\t",array.get(i));
        }
        threadArray.forEach(i -> i.start());
        System.out.println("\nArray after averaging:");
        for (int i = 0; i < SIZE; i++) {
            try {
                threadArray.get(i).join();
            } catch (InterruptedException ex) {
                Logger.getLogger(NeighborAveraging.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int i = 0; i < SIZE; i++) {
            System.out.printf("%.2f\t",array.get(i));
        }
    }

//        /**
//         *   [0       1       2       3       4       5]
//         * 
//         *  [0,1]   [0,2]   [1,3]   [2,4]   [3,5]   [4,5]
//         * get neighbor data
//         *  \if i==0, then get array[i] and array[i+1]
//         *  \if i==SIZE-1, then get array[i] and array[i-1]
//         *  \else, get array[i-1] and array[i+1]
//         *  \for gotten values, perform Arrive method
//         * calculate average
//         *  \avg = (val1 + val2) / 2
//         * write average back to array
//         *  \for own index, perform awaitAdvance method
//         *  \array[i] = avg
//         */
    private static class PhaserNeighborAvg extends Thread {

        int index, ilt, igt;//index, index less than, index greator than

        PhaserNeighborAvg(int i) {
            index = i;
            switch (index) {
                case 0:
                    ilt = index;
                    igt = index + 1;
                    break;
                case SIZE - 1:
                    ilt = index - 1;
                    igt = index;
                    break;
                default:
                    ilt = index - 1;
                    igt = index + 1;
                    break;
            }
            phaserArray.get(ilt).register();
            phaserArray.get(igt).register();
        }

        @Override
        public void run() {
            double lt, gt, avg;
            lt = array.get(ilt);
            phaserArray.get(ilt).arrive();
            gt = array.get(igt);
            phaserArray.get(igt).arrive();
            avg = (lt + gt) / 2;
            phaserArray.get(index).awaitAdvance(0);
            array.set(index, avg);
        }
    ;
}
}
