/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw4structured;

/**
 *
 * @author Andrew
 */
class Fork {

    boolean pickedUp;

    Fork() {
        this.pickedUp = false;
    }

    synchronized boolean pickUp(String name, String side) {
        System.out.println("Philosopher " + name + " attempts to grab " + side + " fork");
        if (pickedUp) {
            System.out.println("Philosopher " + name + " failed to acquire " + side + " fork");
            return false;
        } else {
            pickedUp = true;
            System.out.println("Philosopher " + name + " acquires " + side + " fork");
            return true;
        }
    }

    synchronized void putDown(String name, String side) {
        if (pickedUp) {
            System.out.println("Philosopher " + name + " releases " + side + " fork");
            pickedUp = false;
        } else {
            System.out.println("Philosopher " + name + " tried to put down " + side + " fork, but it's already on the table.");
        }
    }

}
