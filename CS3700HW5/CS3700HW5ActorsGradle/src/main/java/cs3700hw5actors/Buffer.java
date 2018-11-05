/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw5actors;

import java.util.ArrayList;
import akka.actor.AbstractActor;
import akka.actor.AbstractActor.Receive;
import akka.actor.ActorRef;

/**
 *
 * @author Andrew
 */
public class Buffer extends AbstractActor {

    final int MAX;
    private final Object[] list;
    private int front, back, next;
    private boolean isFull, isReady[];
    private final ArrayList<ActorRef> producers;
    private final ArrayList<ActorRef> consumers;

    public Buffer(ArrayList<ActorRef> producers, ArrayList<ActorRef> consumers) {
        this.MAX = 10;
        this.list = new Object[MAX];
        this.front = 0;
        this.back = 0;
        this.next = 0;
        this.isFull = false;
        this.producers = new ArrayList<>(producers);
        this.consumers = new ArrayList<>(consumers);
        this.isReady = new boolean[consumers.size()];
        for (int i = 0, j = consumers.size(); i < j; i++) {
            isReady[i] = true;
        }
    }

    public boolean add(Object o) {
        if (isFull) {
            System.out.println(Thread.currentThread().getName() + ": Failed to add, buffer is full.");
            return false;
        }
        list[back] = o;
        back = (back + 1) % MAX;
        if (front == back) {
            isFull = true;
        }
        //System.out.println(Thread.currentThread().getName() + ": Add to buffer successful.");
        return true;
    }

    public Object remove() {
        if (isFull) {
            front = (front + 1) % MAX;
            isFull = false;
            //System.out.println(Thread.currentThread().getName() + ": Remove from buffer successful.");
            return list[back];
        }
        if (front == back) {
            System.out.println(Thread.currentThread().getName() + ": Failed to remove, buffer is empty.");
            return null;
        }
        int temp = front;
        front = (front + 1) % MAX;
        //System.out.println(Thread.currentThread().getName() + ": Remove from buffer successful.");
        return list[temp];
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("start", p -> {
                    //System.out.println("start");
                    producers.get(next).tell("send", getSelf());
                    next = producers.isEmpty() ? 0 : (next + 1) % producers.size();
                })
                .matchEquals("done", p -> {
                    System.out.println("done");
                    producers.remove(getSender());
                    next = producers.isEmpty() ? 0 : (next + 1) % producers.size();
                })
                .match(Consumer.Request.class, c -> {
                    if (front == back && !isFull) {
                        isReady[consumers.indexOf(getSender())] = true;
                    } else {
                        getSender().tell(remove(), getSelf());
                    }
                    if (!isFull && !producers.isEmpty()) {
                        producers.get(next).tell("send", getSelf());
                        next = producers.isEmpty() ? 0 : (next + 1) % producers.size();
                    }
                    if (producers.isEmpty() && front == back && !isFull) {
                        getContext().getSystem().terminate();
                    }
                })
                .match(Producer.Message.class, r -> {
                    //System.out.println(r);
                    int i = 0;
                    while (i < isReady.length && !isReady[i]) {
                        i++;
                    }
                    boolean success = true;
                    if (i == isReady.length) {
                        success = add(r);
                    } else {
                        consumers.get(i).tell(r, getSelf());
                        isReady[i] = false;
                    }
                    if (!success) {
                        getSender().tell("resend", getSelf());
                    } else if (!isFull && !producers.isEmpty()) {
                        producers.get(next).tell("send", getSelf());
                        next = producers.isEmpty() ? 0 : (next + 1) % producers.size();
                    }
                })
                .build();
    }
}
