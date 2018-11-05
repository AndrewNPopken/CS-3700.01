/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw5part2linear;

import java.util.ArrayList;

/**
 * Input: an integer n > 1.
 *
 * Let A be an array of Boolean values, indexed by integers 2 to n, initially
 * all set to true.
 *
 * for i = 2, 3, 4, ..., not exceeding √n: if A[i] is true: for j = i2, i2+i,
 * i2+2i, i2+3i, ..., not exceeding n: A[j] := false.
 *
 * Output: all i such that A[i] is true.
 *
 * @author Andrew
 */
public class CS3700HW5Part2Linear {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long timestart = java.lang.System.currentTimeMillis();
        int N = 1000000;
        int rootN = 1000;
        ArrayList<Integer> list = new ArrayList<>(N);
        for (int i = 2; i < N; i++) {
            list.add(i);
        }
        for (int i = 2; i < rootN; i++) {
            int num = i;//it yells at me otherwise
            list.removeIf(e -> e % num == 0 && e != num);
        }
        int count = 0;
        for (Integer i : list) {
            System.out.print(i + " ");
            count++;
        }
        System.out.println("\n" + count + " / " + 78498);//check if all primes found
        long timestop = java.lang.System.currentTimeMillis();
        System.out.println("\nTotal time (after Gradle init): " + (timestop - timestart));
    }

}
