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

package org.example.galaxytracing.agent.initializer;

import org.example.galaxytracing.agent.TracingAgent;
import org.example.galaxytracing.agent.core.storage.TraceStorage;
import org.example.galaxytracing.agent.core.storage.TraceStorageBinder;
import org.example.galaxytracing.agent.reporter.Reporter;
import org.example.galaxytracing.infra.config.ConfigurationLoader;
import org.example.galaxytracing.infra.config.exception.ConfigurationLoadException;
import org.example.galaxytracing.infra.config.entity.impl.AgentConfiguration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * TracingAgent initializer.
 *
 * @author JiekerTime
 */
public final class TracingAgentBase {
    
    private static final String DEFAULT_FILE_NAME = "/conf/galaxytracing-config.%s";
    
    private static TracingAgentBase singleton;
    
    private final TraceStorage storage;
    
    private final BlockingQueue<String> mq;
    
    private final Reporter reporter;
    
    private AgentConfiguration configuration;
    
    private TracingAgentBase(final String fileName) {
        this.configuration = loadConfiguration(fileName);
        this.storage = TraceStorageBinder.INSTANCE.getInstance(configuration);
        this.mq = new LinkedBlockingQueue<>();
        this.reporter = new Reporter(mq, configuration);
        reporter.start();
    }
    
    /**
     * Get instance.
     *
     * @param fileName file name
     * @return TracingAgentBase
     */
    public static TracingAgentBase getInstance(final String fileName) {
        if (singleton == null) {
            if (fileName == null || "".equals(fileName)) {
                singleton = new TracingAgentBase(null);
            } else {
                singleton = new TracingAgentBase(fileName);
            }
        }
        return singleton;
    }
    
    private AgentConfiguration loadConfiguration(final String fileName) {
        if (configuration == null) {
            synchronized (this) {
                if (configuration == null) {
                    try {
                        this.configuration = ConfigurationLoader.loadAgentYaml(fileName == null
                                ? DEFAULT_FILE_NAME : fileName, TracingAgent.class);
                    } catch (ConfigurationLoadException ex) {
                        this.configuration = new AgentConfiguration(ConfigurationLoader.loadProperties(fileName == null
                                ? DEFAULT_FILE_NAME : fileName, TracingAgent.class));
                    }
                }
            }
        }
        return configuration;
    }
    
    /**
     * Get storage instance.
     *
     * @return TraceStorage
     */
    public TraceStorage getStorage() {
        return storage;
    }
    
    /**
     * Get mq.
     *
     * @return message mq
     */
    public BlockingQueue<String> getMq() {
        return mq;
    }
    
    /**
     * Get reporter instance.
     *
     * @return Reporter
     */
    public Reporter getReporter() {
        return reporter;
    }
}
