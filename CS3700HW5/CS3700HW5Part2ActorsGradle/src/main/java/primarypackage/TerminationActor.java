/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primarypackage;

import akka.actor.AbstractActor;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author Andrew
 */
public class TerminationActor extends AbstractActor {

    private int count;
    private final int goal;
    private final ConcurrentSkipListSet<Integer> list;
    private final long timestart;

    TerminationActor(ConcurrentSkipListSet<Integer> list, int goal, long timestart) {
        count = 0;
        this.goal = goal;
        this.list = list;
        this.timestart = timestart;
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(Count.class, nill -> {
                    count++;
                    if (count == goal) {
                        int pcount = 0;
                        pcount = list.stream().map((i) -> {
                            System.out.print(i + " ");
                            return i;
                        }).map((_item) -> 1).reduce(pcount, Integer::sum);
                        //check if all primes found
                        System.out.println("\n" + pcount + " / " + 78498);
                        long timestop = java.lang.System.currentTimeMillis();
                        System.out.println("\nTotal time (after Gradle init): " + (timestop - timestart));
                        getContext().getSystem().terminate();
                    }
                })
                .build();
    }

    public static class Count {
    }

}
