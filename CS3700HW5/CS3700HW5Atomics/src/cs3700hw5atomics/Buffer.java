/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw5atomics;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Andrew
 */
public class Buffer {

    final int MAX;
    private final Object[] list;
    private AtomicInteger front, back, size;

    public Buffer() {
        this.MAX = 10;
        this.list = new Object[MAX];
        this.front = new AtomicInteger(0);
        this.back = new AtomicInteger(0);
        this.size = new AtomicInteger(0);
    }

    public boolean add(Object o) {
        if (size.getAndUpdate(v -> v == MAX ? v : v + 1) == MAX) {
            System.out.println(Thread.currentThread().getName() + ": Failed to add, buffer is full.");
            return false;
        }
        list[back.getAndUpdate(v -> (v + 1) % MAX)] = o;
        System.out.println(Thread.currentThread().getName() + ": Added \"" + o + "\" to buffer.");
        return true;
    }

    public Object remove() {
        if (size.getAndUpdate(v -> v == 0 ? v : v - 1) == 0) {
            System.out.println(Thread.currentThread().getName() + ": Failed to remove, buffer is empty.");
            return null;
        }
        Object temp = list[front.getAndUpdate(v -> (v + 1) % MAX)];
        System.out.println(Thread.currentThread().getName() + ": Removed \"" + temp + "\" from buffer.");
        return temp;
    }
}
