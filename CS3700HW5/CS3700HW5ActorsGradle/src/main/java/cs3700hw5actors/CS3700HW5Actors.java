/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs3700hw5actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import java.util.ArrayList;

/**
 *
 * @author Andrew
 */
public class CS3700HW5Actors {

    static Buffer buffer;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("system");

        ArrayList<ActorRef> producers = new ArrayList<>();
        ArrayList<ActorRef> consumers = new ArrayList<>();

        for (int i = 0, j = 5; i < j; i++) {
            producers.add(system.actorOf(Props.create(Producer.class, "Producer" + i)));
        }
        for (int i = 0, j = 2; i < j; i++) {
            consumers.add(system.actorOf(Props.create(Consumer.class)));
        }
//        for (int i = 0, j = 2; i < j; i++) {
//            producers.add(system.actorOf(Props.create(Producer.class, "Producer" + i)));
//        }
//        for (int i = 0, j = 5; i < j; i++) {
//            consumers.add(system.actorOf(Props.create(Consumer.class)));
//        }

        System.out.println("Begin?");
        system.actorOf(Props.create(Buffer.class, producers, consumers), "Buffer").tell("start", ActorRef.noSender());
    }

}
