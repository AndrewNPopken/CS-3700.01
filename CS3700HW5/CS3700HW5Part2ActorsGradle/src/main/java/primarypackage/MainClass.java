/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package primarypackage;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * @author Andrew
 */
public class MainClass {

    public static void main(String args[]) {
        ActorSystem system = ActorSystem.create();
        long timestart = java.lang.System.currentTimeMillis();
        int N = 1000000;
        int rootN = 1000;
        ConcurrentSkipListSet<Integer> list = new ConcurrentSkipListSet();
        for (int i = 2; i < N; i++) {
            list.add(i);
        }
        int actorNum = 500;
        ActorRef terminationActor = system.actorOf(Props.create(
                TerminationActor.class, list, actorNum, timestart));
        for (int i = 0; i < actorNum; i++) {
            int start = i * rootN / actorNum;
            system.actorOf(Props.create(SieveActor.class, list,
                    start < 2 ? 2 : start, (i + 1) * rootN / actorNum,
                    terminationActor))
                    .tell(new SieveActor.Start(), ActorRef.noSender());
        }
    }
}
