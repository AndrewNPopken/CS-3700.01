/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700project1;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrew
 */
public class CS3700Project1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File input;
        File output;
//        ///Linear
//        System.out.println("Linear:\n");
//        input = new File("target.txt");
//        output = new File("output.txt");
//        try {
//            HuffmanEncodingLinear.encode(input, output);
//        } catch (IOException ex) {
//            System.out.println(ex);
//        }
//        ///Parallel 1
//        System.out.println("Parallel 1:\n");
//        input = new File("target.txt");
//        output = new File("parallel1_output.txt");
//        try {
//            HuffmanEncodingParallel1.encode(input, output,1);
//            HuffmanEncodingParallel1.encode(input, output,2);
//            HuffmanEncodingParallel1.encode(input, output,4);
//            HuffmanEncodingParallel1.encode(input, output,8);
//        } catch (IOException ex) {
//            System.out.println(ex);
//        }
        ///Parallel 2
        System.out.println("Parallel 2:\n");
        input = new File("target.txt");
        output = new File("parallel2_output.txt");
        try {
            HuffmanEncodingParallel2.encode(input, output,1);
            HuffmanEncodingParallel2.encode(input, output,2);
            HuffmanEncodingParallel2.encode(input, output,4);
            HuffmanEncodingParallel2.encode(input, output,8);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        ///Parallel 2, target 2
        System.out.println("Parallel 2:\n");
        input = new File("target2.txt");
        output = new File("parallel2_output.txt");
        try {
            HuffmanEncodingParallel2.encode(input, output,1);
            HuffmanEncodingParallel2.encode(input, output,2);
            HuffmanEncodingParallel2.encode(input, output,4);
            HuffmanEncodingParallel2.encode(input, output,8);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        ///Parallel 3, target 2
        System.out.println("Parallel 3:\n");
        input = new File("target2.txt");
        output = new File("parallel3_output.txt");
        try {
            HuffmanEncodingParallel3.encode(input, output,1);
            HuffmanEncodingParallel3.encode(input, output,2);
            HuffmanEncodingParallel3.encode(input, output,4);
            HuffmanEncodingParallel3.encode(input, output,8);
        } catch (IOException ex) {
            System.out.println(ex);
        }
//        ///Decoding
//        input = new File("output.txt");
//        output = new File("decoded.txt");
//        try {
//            HuffmanEncodingLinear.decode(input, output);
//        } catch (IOException ex) {
//            System.out.println(ex);
//        }
    }

}
