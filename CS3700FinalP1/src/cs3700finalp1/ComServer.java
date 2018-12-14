/**
 * ComServer sends messages to a ComClient
 */
package cs3700finalp1;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Andrew
 */
public class ComServer extends Thread {

    ServerSocket serverSocket;
    boolean running;
    final Object waitlock; //not necessary, but probably more efficient than checking for death every second?
    DataOutputStream oos;

    ComServer(ServerSocket serverSocket) {
        super("ComServerThread");
        this.serverSocket = serverSocket;
        this.running = true;
        this.waitlock = new Object();
    }

    public void sendToClient(String msg) {
        try {
            oos.writeUTF(msg);
            oos.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void die() {
        running = false;
        this.interrupt();
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public void run() {
        try {
            Socket socket = serverSocket.accept();
            oos = new DataOutputStream(socket.getOutputStream());
            synchronized (waitlock) {
                while (running) {
                    try {
                        waitlock.wait();
                    } catch (InterruptedException ex) {
                        //ex.printStackTrace();
                    }
                }
            }
            oos.close();
            socket.close();
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
    }
}
