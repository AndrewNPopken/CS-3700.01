/**
 * ComClient receives messages from a ComServer
 */
package cs3700finalp1;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

/**
 *
 * @author Andrew
 */
public class ComClient extends Thread {

    Socket socket;
    DataInputStream ois;
    LinkedList<String> messages;
    boolean running;

    ComClient(Socket socket) {
        super("ComClientThread");
        this.socket = socket;
        this.messages = new LinkedList<>();
        this.running = true;
    }

    public String next() {
        if (hasNext()) {
            return messages.pop();
        }
        return null;
    }

    public String nextBlocking() {
        while (!hasNext()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                break;
            }
        }
        if (running) {
            return messages.pop();
        }
        return null;
    }

    public boolean hasNext() {
        return !messages.isEmpty();
    }

    public void die() {
        running = false;
    }
    
    public int getPort(){
        return socket.getPort();
    }

    @Override
    public void run() {
        try {
            ois = new DataInputStream(socket.getInputStream());
            while (running) {
                while (ois.available() == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        //ex.printStackTrace();
                    }
                    if (!running) {
                        break;
                    }
                }
                if (!running) {
                    break;
                }
                messages.add(ois.readUTF());
            }
            ois.close();
            socket.close();
        } catch (IOException ex) {
            //ex.printStackTrace();
        }
    }
}
