/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw4unstructured;

import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Andrew
 */
class Fork {

    final ReentrantLock LOCK = new ReentrantLock();
    final ReentrantLock PICKED_UP = new ReentrantLock();

    boolean pickUp(String name, String side) {
        System.out.println("Philosopher " + name + " attempts to grab " + side + " fork");
        if (LOCK.tryLock()) {
            try {
                if (PICKED_UP.tryLock()) {
                    System.out.println("Philosopher " + name + " acquires " + side + " fork");
                    return true;
                } else {
                    System.out.println("Philosopher " + name + " failed to acquire " + side + " fork");
                    return false;
                }
            } finally {
                LOCK.unlock();
            }
        }
        System.out.println("Philosopher " + name + " failed to acquire " + side + " fork");
        return false;
    }

    void putDown(String name, String side) {
        if (LOCK.tryLock()) {
            try {
                if (PICKED_UP.isHeldByCurrentThread()) {
                    System.out.println("Philosopher " + name + " releases " + side + " fork");
                    PICKED_UP.unlock();
                } else {
                    System.out.println("Philosopher " + name + " tried to put down " + side + " fork, but they don't hold it.");
                }
            } finally {
                LOCK.unlock();
            }
        }
    }

}
