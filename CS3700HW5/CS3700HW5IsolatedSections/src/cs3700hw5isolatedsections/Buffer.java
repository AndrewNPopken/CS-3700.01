/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw5isolatedsections;

/**
 *
 * @author Andrew
 */
public class Buffer {

    final int MAX;
    private final Object[] list;
    private int front, back;
    private boolean isFull;

    public Buffer() {
        this.MAX = 10;
        this.isFull = false;
        this.list = new Object[MAX];
        this.front = 0;
        this.back = 0;
    }

    public synchronized boolean add(Object o) {
        if (isFull) {
            System.out.println(Thread.currentThread().getName() + ": Failed to add, buffer is full.");
            return false;
        }
        list[back] = o;
        back = (back + 1) % MAX;
        if (front == back) {
            isFull = true;
        }
        System.out.println(Thread.currentThread().getName() + ": Add to buffer successful.");
        return true;
    }

    public synchronized Object remove() {
        if (isFull) {
            front = (front + 1) % MAX;
            isFull = false;
            System.out.println(Thread.currentThread().getName() + ": Remove from buffer successful.");
            return list[back];
        }
        if (front == back) {
            System.out.println(Thread.currentThread().getName() + ": Failed to remove, buffer is empty.");
            return null;
        }
        int temp = front;
        front = (front + 1) % MAX;
        System.out.println(Thread.currentThread().getName() + ": Remove from buffer successful.");
        return list[temp];
    }
}
