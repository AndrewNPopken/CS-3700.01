/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700project1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrew
 */
public class HuffmanEncodingParallel2 {

    public static void decode(File input, File output) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);
        ObjectInputStream ois = new ObjectInputStream(fis);
        HuffNode huffTree = HuffNode.fromString(ois.readUTF());
        char nextByte = 0;
        short bitPlace = 8;
        HuffNode cur = huffTree;
        while (fis.available() > 0 || cur != huffTree) {
            if (bitPlace == 8) {
                bitPlace = 0;
                nextByte = (char) fis.read();
            }
            if (nextByte % 2 == 1) {
                cur = cur.right;
            } else {
                cur = cur.left;
            }
            nextByte /= 2;
            bitPlace++;
            if (cur.isLeaf()) {
                fos.write(cur.ch);
                cur = huffTree;
            }
        }
        fis.close();
        ois.close();
        fos.close();
    }

    public static void encode(File input, File output, int numThreads) throws FileNotFoundException, IOException {
        //FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);
        long timestart = System.currentTimeMillis();
        //char c = (char) fis.read();
        String text;// = "";
        HashMap<Character, Integer> map = new HashMap<>();
        PriorityBlockingQueue<HuffNode> queue = new PriorityBlockingQueue<>();

//        long timestart1 = System.currentTimeMillis();
        ///Amost all time spent encoding is here, when counting frequency
        StringBuilder sb = new StringBuilder();
        for (String s : Files.readAllLines(input.toPath())) {
            sb.append(s);
        }
        text = sb.toString();
//        while (c != 65535) {
//            //text += c;
//            map.compute(c, (k, v) -> (v == null) ? 1 : v + 1);
//            c = (char) fis.read();
//        }

        //HashMap<Character, Integer> mapT = new HashMap();
        HashMap<Character, Integer>[] mapArray = new HashMap[numThreads];
        CyclicBarrier barrier = new CyclicBarrier(numThreads + 1);
        for (int i = 0; i < numThreads; i++) {
            String subText = text.substring(i * text.length() / numThreads, (i + 1) * text.length() / numThreads);
            (new Thread(new freqMapping(subText, mapArray, i, barrier))).start();
        }
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(HuffmanEncodingParallel2.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < numThreads; i++) {
            mapArray[i].entrySet().stream().forEach((e) -> {
                map.compute(e.getKey(), (k, v) -> (v == null) ? e.getValue() : v + e.getValue());
            });
        }

        //fis.close();
//        long timestop1 = System.currentTimeMillis();
//        long timetotal1 = timestop1 - timestart1;
//        System.out.printf("Time for 1: %d milliseconds\n", timetotal1);
//        timestart1 = System.currentTimeMillis();
        map.entrySet().stream().forEach((e) -> {
            queue.add(new HuffNode(e.getKey(), e.getValue(), null, null));
        });
//        timestop1 = System.currentTimeMillis();
//        timetotal1 = timestop1 - timestart1;
//        System.out.printf("Time for 2: %d milliseconds\n", timetotal1);
//        timestart1 = System.currentTimeMillis();
        while (queue.size() > 1) {
            HuffNode l = queue.poll(), r = queue.poll();
            queue.add(new HuffNode('\0', l.count + r.count, l, r));
        }
        HuffNode huffTree = queue.poll();
//        timestop1 = System.currentTimeMillis();
//        timetotal1 = timestop1 - timestart1;
//        System.out.printf("Time for 3: %d milliseconds\n", timetotal1);

        long timestop = System.currentTimeMillis();
        long timetotal = timestop - timestart;
        System.out.printf("Time for encoding with %d Threads: %d milliseconds\n", numThreads, timetotal);
        timestart = System.currentTimeMillis();
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeUTF(huffTree.toString());
        oos.flush();

        String[] translator = new String[256];
        makeTranslator(translator, huffTree, "");
        String bits;
        String[] textArray = new String[numThreads];
        //barrier = new CyclicBarrier(numThreads + 1);
        for (int i = 0; i < numThreads; i++) {
            String subText = text.substring(i * text.length() / numThreads, (i + 1) * text.length() / numThreads);
            (new Thread(new writeEncoded(subText, translator, textArray, i, barrier))).start();
        }
        sb = new StringBuilder();
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(HuffmanEncodingParallel2.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (String s : textArray) {
            sb.append(s);
        }
        bits = sb.toString();

        BitSet bitSet = new BitSet(bits.length());
        int bitCount = 0;
        for (Character bit : bits.toCharArray()) {
            if (bit.equals('1')) {
                bitSet.set(bitCount);
            }
            bitCount++;
        }
        try {
            fos.write(bitSet.toByteArray());
            //System.out.write(bitSet.toByteArray());
            //System.out.println();
        } catch (IOException ex) {
            System.out.println(ex);
        }
        timestop = System.currentTimeMillis();
        timetotal = timestop - timestart;
        System.out.printf("Time for writing with %d Threads: %d milliseconds\n", numThreads, timetotal);
        oos.close();
        fos.close();
    }

    private static void makeTranslator(String[] translator, HuffNode root, String bits) {
        if (!root.isLeaf()) {
            makeTranslator(translator, root.left, bits + '0');
            makeTranslator(translator, root.right, bits + '1');
        } else {
            translator[root.ch] = bits;
        }
    }

    private static class HuffNode implements Comparable<HuffNode>, Serializable {

        private final char ch;
        private final int count;
        private final HuffNode left, right;

        public HuffNode(char ch, int count, HuffNode left, HuffNode right) {
            this.ch = ch;
            this.count = count;
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(HuffNode t) {
            return count - t.count;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public String toString() {
            return isLeaf() ? ("A" + ch + count + 'C') : ("B" + count + 'C' + left + right);
        }

        public static HuffNode fromString(String huffTree) throws IOException {
            //System.out.println(huffTree);
            HuffNode root;
            StringReader sr = new StringReader(huffTree);
            //first char is always B unless there is only one node, which would be an empty file
            sr.skip(1);
            String ccount = "";
            char ch = (char) sr.read();
            while (ch != 'C') {
                ccount += ch;
                ch = (char) sr.read();
            }
            //System.out.println(ccount);
            root = new HuffNode('\0', Integer.parseInt(ccount), fromString(sr), fromString(sr));

            return root;
        }

        private static HuffNode fromString(StringReader sr) throws IOException {
            HuffNode root;
            char ch = (char) sr.read();
            //System.out.println(ch);
            if (ch == 'A') {
                char c = (char) sr.read();
                String ccount = "";
                ch = (char) sr.read();
                while (ch != 'C') {
                    ccount += ch;
                    ch = (char) sr.read();
                }
                //System.out.println(ccount);
                root = new HuffNode(c, Integer.parseInt(ccount), null, null);
            } else {
                String ccount = "";
                ch = (char) sr.read();
                while (ch != 'C') {
                    ccount += ch;
                    ch = (char) sr.read();
                }
                //System.out.println(ccount);
                root = new HuffNode('\0', Integer.parseInt(ccount), fromString(sr), fromString(sr));
            }
            return root;
        }

    }

    private static class writeEncoded implements Runnable {

        String subText;
        String[] textArray;
        int index;
        CyclicBarrier barrier;
        String[] translator;

        public writeEncoded(String subText, String[] translator, String[] textArray, int i, CyclicBarrier barrier) {
            this.barrier = barrier;
            this.translator = translator;
            this.index = i;
            this.subText = subText;
            this.textArray = textArray;
        }

        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, len = subText.length(); i < len; i++) {
                sb.append(translator[subText.charAt(i)]);
            }
            textArray[index] = sb.toString();
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                Logger.getLogger(HuffmanEncodingParallel2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static class freqMapping implements Runnable {

        String subText;
        HashMap<Character, Integer>[] mapArray;
        int index;
        CyclicBarrier barrier;

        public freqMapping(String subText, HashMap<Character, Integer>[] mapArray, int i, CyclicBarrier barrier) {
            this.barrier = barrier;
            this.index = i;
            this.subText = subText;
            this.mapArray = mapArray;
        }

        @Override
        public void run() {
            mapArray[index] = new HashMap<>();
            //HashMap<Character, Integer> map = mapArray[index];
            for (int i = 0, len = subText.length(); i < len; i++) {
                mapArray[index].compute(subText.charAt(i), (k, v) -> (v == null) ? 1 : v + 1);
            }
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                Logger.getLogger(HuffmanEncodingParallel2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
