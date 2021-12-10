/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.galaxytracing.agent.reporter;

import lombok.extern.slf4j.Slf4j;
import org.example.galaxytracing.agent.reporter.http.client.HttpReporterClient;
import org.example.galaxytracing.infra.common.exception.GalaxyTracingException;

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
