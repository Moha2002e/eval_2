package consultation.server;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import consultation.server.protocol.CAPProtocol;

public class ClientThreadPool {
    private final int poolSize;
    private final ConnectionQueue queue;
    private final Supplier<CAPProtocol> protocolFactory;
    private final List<ConnectionWorker> workers;
    private volatile boolean running;
    public ClientThreadPool(int poolSize, ConnectionQueue queue, Supplier<CAPProtocol> protocolFactory) {
        this.poolSize = poolSize;
        this.queue = queue;
        this.protocolFactory = protocolFactory;
        this.workers = new ArrayList<>(poolSize);
    }
    public void start() {
        running = true;
        for (int i = 0; i < poolSize; i++) {
            ConnectionWorker worker = new ConnectionWorker(queue, protocolFactory.get());
            workers.add(worker);
            Thread t = new Thread(worker, "ClientWorker-" + i);
            t.start();
        }
    }
    public void stop() {
        running = false;
        for (ConnectionWorker worker : workers) {
            worker.stop();
        }
    }
}
