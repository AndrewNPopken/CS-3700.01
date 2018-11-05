/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw5actors;

import akka.actor.AbstractActor;
import akka.actor.AbstractActor.Receive;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 *
 * @author Andrew
 */
public class Consumer extends AbstractActor {

    private final Object waitLock;

    Consumer() {
        this.waitLock = new Object();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Producer.Message.class, s -> {
                    System.out.println("Consumed: " + s);
                    synchronized (waitLock) {
                        waitLock.wait(1000);
                    }
                    getSender().tell(new Request(), getSelf());
                })
                .build();
    }

    public static final class Request {

    }

    /* @Override
    public void run() {
        synchronized (waitLock) {
            while (true) {
                if (buffer.remove() == null) {
                    failCount++;
                    if (failCount == failTolerance) {
                        System.out.println(Thread.currentThread().getName() + ": Ending.");
                        return;
                    }
                } else if (failCount != 0) {
                    failCount = 0;
                }
                try {
                    waitLock.wait(1000);
                } catch (InterruptedException ex) {

                }
            }
        }
    } */
}
