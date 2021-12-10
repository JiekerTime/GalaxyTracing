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
import org.example.galaxytracing.agent.core.storage.TraceStorage;
import org.example.galaxytracing.agent.core.storage.TraceStorageBinder;
import org.example.galaxytracing.agent.reporter.Reporter;
import org.example.galaxytracing.infra.common.constant.GalaxyTracingAgentMessage;
import org.example.galaxytracing.infra.common.exception.GalaxyTracingException;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Agent entry.
 *
 * @author JiekerTime
 */
@Slf4j
public final class TracingAgent implements Serializable {
    
    private static final long serialVersionUID = -7914467893018071362L;
    
    private final TraceStorage traceStorage;
    
    private final BlockingQueue<String> mq;
    
    private final Reporter reporter;
    
    public TracingAgent() {
        traceStorage = TraceStorageBinder.INSTANCE.getTraceStorage();
        mq = new LinkedBlockingQueue<>();
        reporter = new Reporter(mq);
        reporter.start();
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
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        final Map data = JSONObject.parseObject(JSONObject.toJSONString(obj), Map.class);
        for (Object key : data.entrySet()) {
            Object value = data.get(key);
            traceStorage.put(String.valueOf(key), String.valueOf(value));
        }
    }
    
    /**
     * Putting data into the storage.
     *
     * @param key  key
     * @param data data
     * @throws GalaxyTracingException System exception
     */
    public void put(final String key, final String data) throws GalaxyTracingException {
        if (Strings.isNullOrEmpty(key)) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_KEY_ERROR);
        }
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        traceStorage.put(key, data);
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
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        return traceStorage.get(key);
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
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        traceStorage.remove(key);
    }
    
    /**
     * Get the Map storing the data.
     *
     * @return dataMap
     * @throws GalaxyTracingException System exception
     */
    public Map<String, String> getDataMap() throws GalaxyTracingException {
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        return traceStorage.getDataMap();
    }
    
    /**
     * Set the Map that stores the data.
     *
     * @param dataMap dataMap
     * @throws GalaxyTracingException System exception
     */
    public void setDataMap(final Map<String, String> dataMap) throws GalaxyTracingException {
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        traceStorage.setDataMap(dataMap);
    }
    
    /**
     * Get the keys of the storage.
     *
     * @return keys
     * @throws GalaxyTracingException System exception
     */
    public Set<String> getKeys() throws GalaxyTracingException {
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        return traceStorage.getKeys();
    }
    
    /**
     * Get the traceId of the storage.
     *
     * @return traceId
     * @throws GalaxyTracingException 系统异常
     */
    public String getTraceId() throws GalaxyTracingException {
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        return traceStorage.getTraceId();
        
    }
    
    /**
     * Reset the traceId in the storage.
     *
     * @param traceId traceId
     * @throws GalaxyTracingException 系统异常
     */
    public void resetTraceId(@Nullable final String traceId) throws GalaxyTracingException {
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        traceStorage.resetTraceId(traceId);
    }
    
    /**
     * Clear the storage.
     *
     * @throws GalaxyTracingException System exception
     */
    public void clear() throws GalaxyTracingException {
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        traceStorage.clear();
    }
    
    /**
     * Finish the storage even.
     *
     * @throws GalaxyTracingException System exception
     */
    public void finish() throws GalaxyTracingException {
        if (traceStorage == null) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.NULL_TRACE_STORAGE_ERROR);
        }
        if (!reporter.isAlive()) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.REPORTER_SHUTDOWN_ERROR);
        }
        mq.offer(traceStorage.clear());
    }
    
    /**
     * Shutdown the Reporter server.
     */
    public void shutdown() {
        reporter.shutdown();
    }
}
