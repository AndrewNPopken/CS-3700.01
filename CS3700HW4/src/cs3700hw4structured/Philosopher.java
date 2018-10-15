/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw4structured;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Andrew
 */
class Philosopher implements Runnable {

    String name;
    Fork left, right;
    boolean hasLeft, hasRight;
    int eatCount;

    Philosopher(String name, Fork leftFork, Fork rightFork) {
        super();
        this.name = name;
        left = leftFork;
        right = rightFork;
        hasLeft = false;
        hasRight = false;
        eatCount = 0;
    }

    @Override
    public void run() {
        while (true) {
            try {
                think();
                grabForks();
                if (hasLeft && hasRight) {
                    eat();
                    releaseForks();
                }
            } catch (InterruptedException ex) {
                break;
            }
        }
        System.out.println("Philosopher " + name + "'s final eat count: " + eatCount);
    }

    void think() throws InterruptedException {
        int r = ThreadLocalRandom.current().nextInt(1, 6);
        System.out.println("Philosopher " + name + " thinks for " + r + " seconds");
        Thread.sleep(r * 1000);
    }

    void eat() throws InterruptedException {
        //if (hasLeft && hasRight) { //Checked by run logic
        int r = ThreadLocalRandom.current().nextInt(1, 11);
        System.out.println("Philosopher " + name + " eats for " + r + " seconds. Eat count: " + ++eatCount);
        Thread.sleep(r * 1000);
        //}
    }

    void grabForks() {
        hasLeft = left.pickUp(name, "left");
        if (hasLeft) {
            hasRight = right.pickUp(name, "right");
            if (!hasRight) {
                left.putDown(name, "left");
                hasLeft = false;
            }
        }
    }

    void releaseForks() {
        left.putDown(name, "left");
        hasLeft = false;
        right.putDown(name, "right");
        hasRight = false;
    }

}
