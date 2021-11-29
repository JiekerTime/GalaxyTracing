package org.example.galaxytracing.agent.reporter;

import lombok.extern.slf4j.Slf4j;
import org.example.galaxytracing.agent.reporter.http.client.HttpReporterClient;
import org.example.galaxytracing.common.exception.GalaxyTracingException;

import java.util.concurrent.BlockingQueue;

/**
 * Timed send of data in the queue.
 *
 * @author JiekerTime
 */
@Slf4j
public final class Reporter extends Thread {
    
    private static final String DEFAULT_URL = "http://localhost:9000/collector";
    
    private final BlockingQueue<String> queue;
    
    private volatile boolean shutdown;
    
    public Reporter(final BlockingQueue<String> queue) {
        super();
        this.queue = queue;
    }
    
    @Override
    public void run() {
        while (!shutdown || !queue.isEmpty()) {
            if (!queue.isEmpty()) {
                try {
                    HttpReporterClient.doPost(DEFAULT_URL, queue.poll());
                } catch (GalaxyTracingException ex) {
                    log.error(ex.getMessage());
                }
            }
        }
        HttpReporterClient.shutdown();
        log.info("Reporter is closed.");
    }
    
    /**
     * Shutdown the reporter server.
     */
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
