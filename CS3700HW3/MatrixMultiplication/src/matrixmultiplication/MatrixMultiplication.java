/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matrixmultiplication;

import java.util.Arrays;

/**
 *
 * @author Andrew
 *
 * Write a method matmult(double A, double B, double C, int m, int n, int p)
 * that multiplies the m x n matrix A by the n x p matrix B to give the m x p
 * matrix C. To make the program execute faster in a multiprocessor environment,
 * use multiple threads to speed up the execution. Vary the number of threads
 * using 1, 2, 4, 8. Also vary the size of the matrices above so that you can
 * get a feel of how increasing threads will help to a limit.
 *
 * b)	EXAMPLE OUTPUT Time with 1 thread: 15sec Time with 2 thread: 8 sec Then
 * vary matrix size…indicate size and print out times.
 *
 */
public class MatrixMultiplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int m, n, p, numThreads;
        double[][] A;
        double[][] B;
        double[][] C;
        for (m = 100; m <= 500; m += 400) {
            for (n = 200; n <= 1000; n += 800) {
                for (p = 300; p <= 1500; p += 1200) {
                    A = new double[m][n];
                    B = new double[n][p];
                    for (numThreads = 1; numThreads < 9; numThreads *= 2) {
                        matrand(A);
                        matrand(B);
                        C = new double[m][p];
                        matmult(A, B, C, m, n, p, numThreads);
                    }
                    System.out.println();
                }
            }
        }
    }

    private static void matrand(double[][] A) {
        Arrays.stream(A).forEach((a) -> {
            Arrays.setAll(a, i -> java.util.concurrent.ThreadLocalRandom.current().nextDouble() * 10);
        });
    }

    public static void matmult(double[][] A, double[][] B, double[][] C, int m, int n, int p, int numThreads) {
        matmultThread[] threadArray = new matmultThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threadArray[i] = (new matmultThread(A, B, C, m, n, p, i * m * p / numThreads, (i + 1) * m * p / numThreads));
        }
        long timestart = System.currentTimeMillis();
        for (int i = 0; i < numThreads; i++) {
            threadArray[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            try {
                threadArray[i].join();
            } catch (InterruptedException ex) {
                //These threads should not be interrupted
            }
        }
        long timestop = System.currentTimeMillis();
        long timetotal = timestop - timestart;
        System.out.printf("Time for %dx%d X %dx%d matmult with %d Threads: %d milliseconds\n", m, n, n, p, numThreads, timetotal);

    }
//    //Used for debugging
//    private static void matprint(double[][] A) {
//        Arrays.stream(A).forEachOrdered((a) -> {
//            Arrays.stream(a).forEachOrdered((i) -> {
//                System.out.print("" + i + "\t");
//            });
//            System.out.println();
//        });
//    }

    private static class matmultThread extends Thread {

        double[][] A;
        double[][] B;
        double[][] C;
        int m;
        int n;
        int p;
        int arrstart;
        int arrstop;

        public matmultThread(double[][] A, double[][] B, double[][] C, int m, int n, int p, int arrstart, int arrstop) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.m = m;
            this.n = n;
            this.p = p;
            this.arrstart = arrstart;
            this.arrstop = arrstop;
        }

        @Override
        public void run() {
//            //Debugging
//            System.out.println("Thread " + Thread.currentThread().getId() + " start: " + arrstart);
//            System.out.println("Thread " + Thread.currentThread().getId() + " stop: " + arrstop);
            int i = arrstart / p, j = arrstart % p;
            for (; i < m && i * p + j < arrstop; i++) {
                for (; j < p && i * p + j < arrstop; j++) {
                    for (int k = 0; k < n; k++) {
                        C[i][j] += A[i][k] * B[k][j];
                    }
                }
                j = 0;
            }
        }
    }
}
