/**
 * Main uses ComServer and ComClient to communicate with other two processes
 */
package cs3700finalp1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Andrew
 */
public class CS3700FinalP1 {

    static ArrayList<ComServer> senders;
    static ArrayList<ComClient> receivers;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        senders = new ArrayList<>();
        receivers = new ArrayList<>();
        int startPort = 6500;
        int portS = startPort;
        int portC = startPort;
        int prevPort = 0;
        boolean foundPort = false;
        System.out.println("Attempting to establish connections");
        while (!foundPort) {
            try {
                ServerSocket serverSocket = new ServerSocket(portS);
                foundPort = true;
                while (senders.size() < 2) {
                    ComServer cs = new ComServer(serverSocket);
                    cs.start();
                    senders.add(cs);
                }
            } catch (IOException ex) {
                portS++;
            }
        }
        if (senders.size() == 2) {
            while (receivers.size() < 2) {
                if (portC == portS || portC == prevPort) {
                    portC++;
                    continue;
                }
                try {
                    //System.out.println("Attempting client on port " + portC);
                    Socket socketComC = new Socket("localhost", portC);
                    ComClient cc = new ComClient(socketComC);
                    cc.start();
                    receivers.add(cc);
                    prevPort = portC;
                } catch (IOException ex) {
                    //System.out.println("Client failure on port " + portC);
                    portC = portC < startPort + 3 ? portC + 1 : startPort;
                    if (portC == startPort) {//if statement is to make this output appear less often
                        System.out.println("Waiting for other players");
                    }
                }
            }
        } else {
            System.out.println("Failure to establish connections");
        }
        System.out.println("Connections established successfully");

        //Play game
        String name = "Player " + (portS - startPort + 1);
        System.out.println("I am " + name);
        int numGames;
        switch (name) {
            case "Player 1":
                System.out.print("Enter number of rounds to play: ");
                numGames = (new Scanner(System.in)).nextInt();
                senders.forEach(s -> s.sendToClient(String.valueOf(numGames)));
                break;
            default:
                System.out.println("Waiting on Player 1 to decide the number of rounds to play...");
                if (receivers.get(0).getPort() == startPort) {
                    numGames = Integer.valueOf(receivers.get(0).nextBlocking());
                } else {
                    numGames = Integer.valueOf(receivers.get(1).nextBlocking());
                }
        }
        int[] points = new int[3];
        for (int i = 0; i < numGames; i++) {
            String shoot = play();
            senders.forEach(s -> s.sendToClient(shoot));
            String[] results = new String[3];
            results[portS - startPort] = shoot;
            results[receivers.get(0).getPort() - startPort] = receivers.get(0).nextBlocking();
            results[receivers.get(1).getPort() - startPort] = receivers.get(1).nextBlocking();

            System.out.println("\nRound " + (i + 1) + ":");
            System.out.println("Player 1 Chose: " + results[0]);
            System.out.println("Player 2 Chose: " + results[1]);
            System.out.println("Player 3 Chose: " + results[2]);
            if (!(results[0].equals(results[1]) && results[0].equals(results[2]))
                    && !(!results[0].equals(results[1])
                    && !results[0].equals(results[2])
                    && !results[1].equals(results[2]))) {
                for (int j = 0; j < 3; j++) {
                    switch (results[j % 3]) {
                        case "Rock":
                            if (results[(j + 1) % 3].equals("Scissors")) {
                                points[j] += 1;
                            }
                            if (results[(j + 2) % 3].equals("Scissors")) {
                                points[j] += 1;
                            }
                            break;
                        case "Paper":
                            if (results[(j + 1) % 3].equals("Rock")) {
                                points[j] += 1;
                            }
                            if (results[(j + 2) % 3].equals("Rock")) {
                                points[j] += 1;
                            }
                            break;
                        case "Scissors":
                            if (results[(j + 1) % 3].equals("Paper")) {
                                points[j] += 1;
                            }
                            if (results[(j + 2) % 3].equals("Paper")) {
                                points[j] += 1;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            System.out.println("Player 1 Current Points: " + points[0]);
            System.out.println("Player 2 Current Points: " + points[1]);
            System.out.println("Player 3 Current Points: " + points[2]);

        }
        System.out.println("\nFinal Scores: ");
        System.out.println("Player 1: " + points[0]);
        System.out.println("Player 2: " + points[1]);
        System.out.println("Player 3: " + points[2]);

        //End Program
        senders.forEach(sender -> {
            sender.die();
        });
        receivers.forEach(receiver -> {
            receiver.die();
        });
    }

    private static String play() {
        int r = ThreadLocalRandom.current().nextInt(0, 3);
        switch (r) {
            case 0:
                return "Rock";
            case 1:
                return "Paper";
            case 2:
            default:
                return "Scissors";
        }
    }

}
