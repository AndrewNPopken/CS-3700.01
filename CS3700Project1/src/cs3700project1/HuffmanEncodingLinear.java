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
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 *
 * @author Andrew
 */
public class HuffmanEncodingLinear {

    public static void decode(File input, File output) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);
        ObjectInputStream ois = new ObjectInputStream(fis);
        HuffNode huffTree = HuffNode.fromString(ois.readUTF());
//        String[] translator = new String[256];
//        makeTranslator(translator, huffTree, "");
//        HashMap<String, String> map = new HashMap<>();
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

    public static void encode(File input, File output) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);
        long timestart = System.currentTimeMillis();
        char c = (char) fis.read();
        String text = "";
        HashMap<Character, Integer> map = new HashMap<>();
        PriorityQueue<HuffNode> queue = new PriorityQueue<>();//((n1, n2) -> n1.count - n2.count);

        while (c != 65535) {
            text += c;
            map.compute(c, (k, v) -> (v == null) ? 1 : v + 1);
            c = (char) fis.read();
        }
        fis.close();
        for (Entry<Character, Integer> e : map.entrySet()) {
            queue.add(new HuffNode(e.getKey(), e.getValue(), null, null));
        }
        while (queue.size() > 1) {
            HuffNode l = queue.poll(), r = queue.poll();
            queue.add(new HuffNode('\0', l.count + r.count, l, r));
        }
        HuffNode huffTree = queue.poll();

        long timestop = System.currentTimeMillis();
        long timetotal = timestop - timestart;
        System.out.printf("Time for encoding with %d Threads: %d milliseconds\n", 1, timetotal);
        timestart = System.currentTimeMillis();
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeUTF(huffTree.toString());
        oos.flush();

        String[] translator = new String[256];
        makeTranslator(translator, huffTree, "");
        String bits = "";
        for (int i = 0, len = text.length(); i < len; i++) {
            bits += translator[text.charAt(i)];
        }
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
        System.out.printf("Time for writing with %d Threads: %d milliseconds\n", 1, timetotal);
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
}
