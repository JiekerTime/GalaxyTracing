package org.example.galaxytracing.agent.reporter;

import org.example.galaxytracing.agent.reporter.http.client.HttpReporterClient;

import java.util.Queue;

/**
 * Timed send of data in the queue.
 *
 * @author JiekerTime
 */
public final class Reporter extends Thread {
    
    private static final String DEFAULT_URL = "https://localhost:9000/";
    
    private final Queue<String> queue;
    
    private volatile boolean shutdown;
    
    public Reporter(final Queue<String> queue) {
        super();
        this.queue = queue;
    }
    
    @Override
    public void run() {
        while (!shutdown) {
            if (!queue.isEmpty()) {
                HttpReporterClient.doPost(DEFAULT_URL, queue.poll());
            }
        }
    }
    
    /**
     * Shutdown the reporter server.
     */
    public void shutdown() {
        shutdown = true;
        HttpReporterClient.shutdown();
        synchronized (this) {
            notifyAll();
        }
    }
}
