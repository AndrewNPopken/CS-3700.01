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
public class Producer extends AbstractActor {

    private final String name;
    private int count;
    private final int MAX;

    Producer(String name) {
        count = 0;
        MAX = 100;
        this.name = name;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("send", p -> {
                    if (count < 100) {
                        getSender().tell(new Message(name + " [" + count + "]"), getSelf());
                        count++;
                    } else if (count == 100) {
                        getSender().tell("done", getSelf());
                        count++;
                        //getContext().stop(getSelf());
                    }
                })
                .matchEquals("resend", p -> {
                    count--;
                })
                .build();
    }

    public static final class Message {

        private final String text;

        public Message(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
