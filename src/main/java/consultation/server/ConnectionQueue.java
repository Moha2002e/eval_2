package consultation.server;

import java.net.Socket;
import java.util.LinkedList;

public class ConnectionQueue {
    private final LinkedList<Socket> queue = new LinkedList<>();
    public synchronized void addConnection(Socket socket) {
        queue.addLast(socket);
        notify();
    }

    public synchronized Socket getConnection() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.removeFirst();
    }
}
