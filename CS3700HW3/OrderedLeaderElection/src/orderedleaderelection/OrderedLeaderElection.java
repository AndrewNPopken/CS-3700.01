/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package orderedleaderelection;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Andrew
 *
 * Assume a system with N elected official threads and one rank thread. Each
 * elected official thread has an identifying name and an integer rank value,
 * where -∞ is the lowest rank and +∞ is the highest rank, use Random. Threads
 * do not previously know the rank value of other threads. As threads are being
 * created they should print out there name, rank and who they think is the
 * leader, initially they think they are the leader, and notify the rank thread
 * that a new elected official has been created, using an interrupt. When the
 * rank thread is interrupted it will check the ranking of all the threads at
 * the time and will only notify all threads if there is a new leader using an
 * interrupt. The thread with the largest rank value is to be selected as the
 * leader. You can use any algorithm that selects one and only one thread as the
 * leader.
 */
public class OrderedLeaderElection {

    private static final Semaphore voteBox = new Semaphore(1);
    private static final AtomicInteger voteCount = new AtomicInteger(0);
    private static String noticeName;
    private static int noticeRank;
    private static boolean electionFlag;
    private static final int numElectedOfficials = 15;
    private static final ThreadGroup EOTG = new ThreadGroup("Elected Officials");
    private static final ThreadGroup RTG = new ThreadGroup("Rank Thread");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        electionFlag = true;
        (new Thread(RTG, new RankThread())).start();
        for (int i = 0; i < numElectedOfficials; i++) {
            (new Thread(EOTG, new ElectedOfficial())).start();
        }
    }

    public static class ElectedOfficial implements Runnable {

        String name;
        int rank;
        String winnerName;

        private ElectedOfficial() {
            rank = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        @Override
        public void run() {
            name = Thread.currentThread().getName();
            winnerName = name;
            System.out.println("Elected Official " + name + ", rank " + rank + ", initially thinks the winner will be " + winnerName);
            synchronized (voteBox) {
                boolean update = false;
                while (true) {
                    if (voteBox.tryAcquire()) {
                        noticeName = name;
                        noticeRank = rank;
                        break;
                    }
                    try {
                        voteBox.wait(100);
                    } catch (InterruptedException ex) {
                        if (!update) {
                            update = true;
                            voteCount.decrementAndGet();
                        }
                    }
                }
                RTG.interrupt();
                while (electionFlag) {
                    if (!update) {
                        try {
                            voteBox.wait();
                        } catch (InterruptedException ex) {
                            if (!update) {
                                update = true;
                                voteCount.decrementAndGet();
                            }
                        }
                    } else {
                        if (voteBox.tryAcquire()) {
                            winnerName = RankThread.winnerName;
                            update = false;
                            System.out.println("Elected Official " + name + " now thinks the winner will be " + winnerName);
                            voteCount.incrementAndGet();
                            voteBox.release();
                        } else {
                            try {
                                voteBox.wait(100);
                            } catch (InterruptedException ex) {
//                            //Already trying to update
                            }
                        }
                    }
                }
            }
        }
    }

    public static class RankThread implements Runnable {

        public static String winnerName;
        int winnerRank;

        private RankThread() {
            winnerName = "No Elected Officials";
            winnerRank = Integer.MIN_VALUE;
        }

        @Override
        public void run() {
            synchronized (voteBox) {
                while (electionFlag) {
                    try {
                        voteBox.wait(100);
                    } catch (InterruptedException ex) {
                        voteCount.incrementAndGet();
                        if (noticeRank >= winnerRank) {
                            winnerRank = noticeRank;
                            winnerName = noticeName;
                            EOTG.interrupt();
                        }
                        voteBox.release();
                    }
                    if (voteCount.get() == numElectedOfficials) {
                        electionFlag = false;
                        voteBox.notifyAll();
                        System.out.println("The winner is: " + winnerName);
                    }
                }
            }
        }
    }
}
