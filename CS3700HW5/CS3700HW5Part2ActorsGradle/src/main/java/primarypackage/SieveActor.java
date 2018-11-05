/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primarypackage;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * start is inclusive, stop is exclusive
 *
 * @author Andrew
 */
public class SieveActor extends AbstractActor {

    private final ConcurrentSkipListSet<Integer> list;
    private final int start;
    private final int stop;
    private final ActorRef terminationActor;

    SieveActor(ConcurrentSkipListSet<Integer> list, int start, int stop, ActorRef terminationActor) {
        this.list = list;
        this.start = start;
        this.stop = stop;
        this.terminationActor = terminationActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SieveActor.Start.class, nill -> {
                    for (int i = start; i < stop; i++) {
                        int num = i;//it yells at me otherwise
                        list.removeIf(e -> e % num == 0 && e != num);
                    }
                    //getContext().stop(getSelf());
                    terminationActor.tell(new TerminationActor.Count(), getSelf());
                })
                .build();
    }

    public static class Start {
    }

}
