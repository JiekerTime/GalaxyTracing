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

package org.example.galaxytracing.agent;

import com.google.common.base.Strings;
import com.huawei.shade.com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.example.galaxytracing.agent.core.storage.impl.SnowFlakeTraceStorage;
import org.example.galaxytracing.agent.initializer.TracingAgentBase;
import org.example.galaxytracing.infra.common.constant.GalaxyTracingAgentMessage;
import org.example.galaxytracing.infra.common.exception.GalaxyTracingException;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * Agent entry.
 *
 * @author JiekerTime
 */
@Slf4j(topic = "agent")
public final class TracingAgent {
    
    private final TracingAgentBase singleton;
    
    public TracingAgent() {
        this.singleton = TracingAgentBase.getInstance(null);
    }
    
    public TracingAgent(final String fileName) {
        this.singleton = TracingAgentBase.getInstance(fileName);
    }
    
    /**
     * Putting data into the storage.
     *
     * @param obj pojo
     * @throws GalaxyTracingException System exception
     */
    public void put(final Object obj) throws GalaxyTracingException {
        if (obj == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_OBJ_ERROR);
        }
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        final Map data = JSONObject.parseObject(JSONObject.toJSONString(obj), Map.class);
        for (Object key : data.entrySet()) {
            Object value = data.get(key);
            singleton.getStorage().put(String.valueOf(key), String.valueOf(value));
        }
    }
    
    /**
     * Putting data into the storage.
     *
     * @param key  key
     * @param data data
     * @return default traceStorage
     * @throws GalaxyTracingException System exception
     */
    public SnowFlakeTraceStorage put(final String key, final String data) throws GalaxyTracingException {
        if (Strings.isNullOrEmpty(key)) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_KEY_ERROR);
        }
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        return singleton.getStorage().put(key, data);
    }
    
    /**
     * Putting data into the storage.
     *
     * @param values keys and values
     * @throws GalaxyTracingException System exception
     */
    public void put(final String... values) throws GalaxyTracingException {
        if (values.length % 2 != 0) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.KEY_VALUE_NOT_MATCH_ERROR);
        }
        int failedCount = 0;
        StringBuilder errorMsg = new StringBuilder("Partial execution fails with the failure primary key: ");
        for (int i = 0; i < values.length; i += 2) {
            try {
                put(values[i], values[i + 1]);
            } catch (GalaxyTracingException ignored) {
                errorMsg.append(i);
                failedCount++;
            }
        }
        if (failedCount != 0) {
            throw new GalaxyTracingException(errorMsg.toString());
        }
    }
    
    /**
     * Get the data according to the key.
     *
     * @param key key
     * @return data
     * @throws GalaxyTracingException System exception
     */
    public String get(final String key) throws GalaxyTracingException {
        if (Strings.isNullOrEmpty(key)) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_KEY_ERROR);
        }
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        return singleton.getStorage().get(key);
    }
    
    /**
     * Remove the data matching the key.
     *
     * @param key key
     * @throws GalaxyTracingException System exception
     */
    public void remove(final String key) throws GalaxyTracingException {
        if (Strings.isNullOrEmpty(key)) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_KEY_ERROR);
        }
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        singleton.getStorage().remove(key);
    }
    
    /**
     * Get the Map storing the data.
     *
     * @return dataMap
     * @throws GalaxyTracingException System exception
     */
    public Map<String, String> getDataMap() throws GalaxyTracingException {
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        return singleton.getStorage().getDataMap();
    }
    
    /**
     * Set the Map that stores the data.
     *
     * @param dataMap dataMap
     * @throws GalaxyTracingException System exception
     */
    public void setDataMap(final Map<String, String> dataMap) throws GalaxyTracingException {
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        singleton.getStorage().setDataMap(dataMap);
    }
    
    /**
     * Get the keys of the storage.
     *
     * @return keys
     * @throws GalaxyTracingException System exception
     */
    public Set<String> getKeys() throws GalaxyTracingException {
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        return singleton.getStorage().getKeys();
    }
    
    /**
     * Get the traceId of the storage.
     *
     * @return traceId
     * @throws GalaxyTracingException System exception
     */
    public String getTraceId() throws GalaxyTracingException {
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        return singleton.getStorage().getTraceId();
        
    }
    
    /**
     * Reset the traceId in the storage.
     *
     * @param traceId traceId
     * @throws GalaxyTracingException System exception
     */
    public void resetTraceId(@Nullable final String traceId) throws GalaxyTracingException {
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        singleton.getStorage().resetTraceId(traceId);
    }
    
    /**
     * Clear the storage.
     *
     * @throws GalaxyTracingException System exception
     */
    public void clear() throws GalaxyTracingException {
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        singleton.getStorage().clear();
    }
    
    /**
     * Finish the storage even.
     *
     * @throws GalaxyTracingException System exception
     */
    public void finish() throws GalaxyTracingException {
        finish(log);
    }
    
    /**
     * Finish the storage even.
     *
     * @param logger log engine
     * @throws GalaxyTracingException System exception
     */
    public void finish(final Logger logger) throws GalaxyTracingException {
        if (singleton.getStorage() == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        if (!singleton.getReporter().isAlive()) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.REPORTER_SHUTDOWN_ERROR);
        }
        if (singleton.getConfiguration().getBasic().isLogging()) {
            final String msg = singleton.getStorage().clear();
            logger.info(msg);
            singleton.getMq().offer(msg);
        }
    }
    
    /**
     * Shutdown the Reporter server.
     */
    public void shutdown() {
        singleton.getReporter().shutdown();
    }
}
