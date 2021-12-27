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

package org.example.galaxytracing.agent.core.storage.impl;

import com.huawei.shade.com.alibaba.fastjson.JSONObject;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.agent.core.storage.TraceStorage;
import org.example.galaxytracing.infra.common.constant.GalaxyTracingAgentMessage;
import org.example.galaxytracing.infra.common.exception.GalaxyTracingException;
import org.example.galaxytracing.infra.common.traceid.SnowflakeId;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default Trace Data Storage..
 *
 * @author JiekerTime
 */
@NoArgsConstructor
public final class SnowFlakeTraceStorage implements TraceStorage {
    
    private static final String TRACE_ID = "traceId";
    
    private static final int WRITE_OPERATION = 1;
    
    private static final int MAP_COPY_OPERATION = 2;
    
    private final ThreadLocal<Map<String, String>> dataMap = new ThreadLocal<>();
    
    private final ThreadLocal<Integer> lastOperation = new ThreadLocal<>();
    
    private final SnowflakeId snowflakeId = new SnowflakeId();
    
    @Override
    public SnowFlakeTraceStorage put(final String key, final String data) {
        Map<String, String> oldMap = dataMap.get();
        Integer lastOp = getAndSetLastOperation();
        if (wasLastOpReadOrNull(lastOp) || oldMap == null) {
            Map<String, String> newMap = duplicateAndInsertNewMap(oldMap);
            newMap.put(key, data);
        } else {
            oldMap.put(key, data);
        }
        return this;
    }
    
    @Override
    public String get(final String key) throws GalaxyTracingException {
        Map<String, String> map;
        if (((map = dataMap.get()) == null) || map.isEmpty()) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.STORAGE_NOT_INIT_ERROR);
        }
        return map.get(key);
    }
    
    @Override
    public void remove(final String key) throws GalaxyTracingException {
        Map<String, String> oldMap;
        if (((oldMap = dataMap.get()) == null) || oldMap.isEmpty()) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.STORAGE_NOT_INIT_ERROR);
        }
        Integer lastOp = getAndSetLastOperation();
        if (wasLastOpReadOrNull(lastOp)) {
            Map<String, String> newMap = duplicateAndInsertNewMap(oldMap);
            newMap.remove(key);
        } else {
            oldMap.remove(key);
        }
    }
    
    @Override
    public String clear() {
        final String result = JSONObject.toJSONString(getDataMap());
        lastOperation.set(WRITE_OPERATION);
        lastOperation.remove();
        dataMap.remove();
        return result;
    }
    
    @Override
    public Map<String, String> getDataMap() {
        lastOperation.set(MAP_COPY_OPERATION);
        return dataMap.get();
    }
    
    @Override
    public void setDataMap(final Map<String, String> dataMap) {
        lastOperation.set(WRITE_OPERATION);
        Map<String, String> newMap = new ConcurrentHashMap<>(dataMap);
        this.dataMap.set(newMap);
    }
    
    @Override
    public Set<String> getKeys() {
        Map<String, String> map = getDataMap();
        
        if (map != null && !map.isEmpty()) {
            return map.keySet();
        } else {
            return null;
        }
    }
    
    @Override
    public String getTraceId() {
        Map<String, String> map;
        if (((map = dataMap.get()) == null) || map.isEmpty()) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.STORAGE_NOT_INIT_ERROR);
        }
        return map.get(TRACE_ID);
    }
    
    @Override
    public void resetTraceId(final String traceId) {
        Map<String, String> dataMap = getDataMap();
        lastOperation.set(WRITE_OPERATION);
        if (dataMap == null || dataMap.isEmpty()) {
            throw new GalaxyTracingException(GalaxyTracingAgentMessage.STORAGE_NOT_INIT_ERROR);
        } else {
            dataMap.put(TRACE_ID, traceId);
        }
    }
    
    private Integer getAndSetLastOperation() {
        Integer lastOp = lastOperation.get();
        lastOperation.set(WRITE_OPERATION);
        return lastOp;
    }
    
    private boolean wasLastOpReadOrNull(final Integer lastOp) {
        return lastOp == null || lastOp == MAP_COPY_OPERATION;
    }
    
    private Map<String, String> duplicateAndInsertNewMap(final Map<String, String> oldMap) {
        Map<String, String> newMap = new ConcurrentHashMap<>();
        if (oldMap != null && !oldMap.isEmpty()) {
            newMap.putAll(oldMap);
        } else {
            newMap.put(TRACE_ID, String.valueOf(snowflakeId.generateId()));
        }
        dataMap.set(newMap);
        return newMap;
    }
    
}
