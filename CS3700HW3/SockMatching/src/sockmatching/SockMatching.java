/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockmatching;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Andrew
 *
 * There are four threads that each make a random number of socks (1-100). Each
 * sock thread produces a sock that is one of four colors, Red, Green, Blue,
 * Orange. The socks are then passed to a single matching thread. The matching
 * thread finds two socks that are the same color. It then passes the pair of
 * socks to the washer thread. The washer thread destroys the socks. In the
 * console announce which thread is printing and what occurred: (Make sure your
 * program ends. When there is no more work to finish it should terminate)
 *
 * a)	EXAMPLE OUTPUT Red Sock: Produced 4 of 35 Red Socks Green Sock: Produced 7
 * of 19 Green Socks Matching Thread: Send Blue Socks to Washer. Total socks
 * 234. Total inside queue 3 Washer Thread: Destroyed Blue socks
 *
 */
public class SockMatching {

    static ArrayBlockingQueue<Sock> redPile;
    static ArrayBlockingQueue<Sock> greenPile;
    static ArrayBlockingQueue<Sock> bluePile;
    static ArrayBlockingQueue<Sock> orangePile;
    static MatcherSync matcherSync;
    static final Match match = new Match();
    static private final Object lock = new Object();
    static SockGenerator redGen;
    static SockGenerator greenGen;
    static SockGenerator blueGen;
    static SockGenerator orangeGen;
    static SockMatcher sockMatcher;
    static SockWasher sockWasher;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        redPile = new ArrayBlockingQueue<>(100);
        greenPile = new ArrayBlockingQueue<>(100);
        bluePile = new ArrayBlockingQueue<>(100);
        orangePile = new ArrayBlockingQueue<>(100);
        matcherSync = new MatcherSync();
        //match = new Match();
        redGen = new SockGenerator("Red", redPile);
        greenGen = new SockGenerator("Green", greenPile);
        blueGen = new SockGenerator("Blue", bluePile);
        orangeGen = new SockGenerator("Orange", orangePile);
        sockMatcher = new SockMatcher();
        sockWasher = new SockWasher();
        redGen.start();
        greenGen.start();
        blueGen.start();
        orangeGen.start();
        sockMatcher.start();
        sockWasher.start();
    }

    private final static class Sock {

        private final String color;

        public Sock(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    //Creates socks of a particular color
    //Sends socks to SockMatcher
    private static class SockGenerator extends Thread {

        private final String color;
        private ArrayBlockingQueue<Sock> pile;
        private final int socksToMake;

        public SockGenerator(String color, ArrayBlockingQueue<Sock> pile) {
            socksToMake = ThreadLocalRandom.current().nextInt(1, 100);
            this.color = color;
            this.pile = pile;
        }

        @Override
        public void run() {
            for (int i = 0; i < socksToMake; i++) {
                System.out.println(color + " Sock: Produced " + (i + 1) + " of " + socksToMake + " " + color + " Socks");
                pile.add(new Sock(color));
                matcherSync.more();
            }
            System.out.println(color + " Sock Generator: done");
        }

    }

    //Recieves socks from SockGenerators
    //matches pairs of same-colored socks together
    //Sends matched socks to SockWasher
    private static class SockMatcher extends Thread {

        private boolean done;

        public SockMatcher() {
            done = false;
        }

        @Override
        public void run() {
            while (!done) {
                if (redPile.size() < 2 && greenPile.size() < 2 && bluePile.size() < 2 && orangePile.size() < 2) {
                    if (redGen.isAlive() || greenGen.isAlive() || blueGen.isAlive() || orangeGen.isAlive()) {
                        System.out.println("Matching Thread: waiting for sock generators");
                        matcherSync.waitForOther();
                    } else if (redPile.size() < 2 && greenPile.size() < 2 && bluePile.size() < 2 && orangePile.size() < 2) {
                        done = true;
                    }
                }
                while (match.waitingToBeDestroyed()) {
                    System.out.println("Matching Thread: waiting for sock destruction");
                    match.waitForOther();
                }
                if (redPile.size() > 1) {
                    try {
                        System.out.println("Matching Thread: Send Red Socks to Washer. Total inside queue: "
                                + (redPile.size() + greenPile.size() + bluePile.size() + orangePile.size() - 2));
                        match.setPair(redPile.take(), redPile.take());
                    } catch (InterruptedException ex) {
                        //I don't know how the ArrayBlockingQueue would throw this exception,
                        // unless I am the one to throw it
                    }
                } else if (greenPile.size() > 1) {
                    try {
                        System.out.println("Matching Thread: Send Green Socks to Washer. Total inside queue: "
                                + (redPile.size() + greenPile.size() + bluePile.size() + orangePile.size() - 2));
                        match.setPair(greenPile.take(), greenPile.take());
                    } catch (InterruptedException ex) {
                    }
                } else if (bluePile.size() > 1) {
                    try {
                        System.out.println("Matching Thread: Send Blue Socks to Washer. Total inside queue: "
                                + (redPile.size() + greenPile.size() + bluePile.size() + orangePile.size() - 2));
                        match.setPair(bluePile.take(), bluePile.take());
                    } catch (InterruptedException ex) {
                    }
                } else if (orangePile.size() > 1) {
                    try {
                        System.out.println("Matching Thread: Send Orange Socks to Washer. Total inside queue: "
                                + (redPile.size() + greenPile.size() + bluePile.size() + orangePile.size() - 2));
                        match.setPair(orangePile.take(), orangePile.take());
                    } catch (InterruptedException ex) {
                    }
                }
                match.more();
            }System.out.println("Matching Thread: done");
        }
    }

    //Recieves matched socks
    //Destroys matched socks
    private static class SockWasher extends Thread {

        @Override
        public void run() {
            while (sockMatcher.isAlive() || match.waitingToBeDestroyed()) {
                if (match.waitingToBeDestroyed()) {
                    System.out.println("Washer Thread: Destroyed " + match.getColor() + " socks");
                    match.destroyPair();
                    match.more();
                } else {
                    match.waitForOther();
                }
            }
            System.out.println("Washer Thread: done");
        }
    }

    private static class MatcherSync {

        public synchronized void waitForOther() {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }

        public synchronized void more() {
            notifyAll();
        }
    }

    private static class Match {

        Sock sock1;
        Sock sock2;

        public Match() {
            sock1 = null;
            sock2 = null;
        }

        public synchronized void setPair(Sock a, Sock b) {
            sock1 = a;
            sock2 = b;
        }

        public synchronized void destroyPair() {
            sock1 = null;
            sock2 = null;
        }

        public synchronized boolean waitingToBeDestroyed() {
            return sock1 != null && sock2 != null;
        }

        public synchronized void waitForOther() {
            notifyAll();
            try {
                wait(1000);
            } catch (InterruptedException e) {

            }
        }

        public synchronized void more() {
            notifyAll();
        }

        private String getColor() {
            return sock1.getColor();
        }
    }

}
