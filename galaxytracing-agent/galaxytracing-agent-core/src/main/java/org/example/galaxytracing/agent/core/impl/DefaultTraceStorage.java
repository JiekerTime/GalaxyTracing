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

package org.example.galaxytracing.agent.core.impl;

import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.agent.core.TraceStorage;
import org.example.galaxytracing.common.constant.SnowflakeId;
import org.example.galaxytracing.common.excetion.GalaxyTracingException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default Trace Data Storage..
 *
 * @author JiekerTime
 */
@NoArgsConstructor
public final class DefaultTraceStorage implements TraceStorage {
    
    private static final String TRACE_ID = "traceId";
    
    private static final int WRITE_OPERATION = 1;
    
    private static final int MAP_COPY_OPERATION = 2;
    
    private final ThreadLocal<Map<String, String>> dataMap = new ThreadLocal<>();
    
    private final ThreadLocal<Integer> lastOperation = new ThreadLocal<>();
    
    private final SnowflakeId snowflakeId = new SnowflakeId();
    
    @Override
    public void put(final String key, final String data) {
        Map<String, String> oldMap = dataMap.get();
        Integer lastOp = getAndSetLastOperation();
        if (wasLastOpReadOrNull(lastOp) || oldMap == null) {
            Map<String, String> newMap = duplicateAndInsertNewMap(oldMap, null);
            newMap.put(key, data);
        } else {
            oldMap.put(key, data);
        }
    }
    
    @Override
    public String get(final String key) throws GalaxyTracingException {
        Map<String, String> map;
        if ((map = dataMap.get()) == null) {
            throw new GalaxyTracingException("Storage container not initialized, get property failed!");
        }
        if (map.isEmpty() || !map.containsKey(key)) {
            throw new GalaxyTracingException("Storage container is empty or does not contain field %s, get property failed!", key);
        }
        return map.get(key);
    }
    
    @Override
    public void remove(final String key) throws GalaxyTracingException {
        Map<String, String> oldMap;
        if ((oldMap = dataMap.get()) == null) {
            throw new GalaxyTracingException("Storage container not initialized, remove property failed!");
        }
        if (oldMap.isEmpty() || !oldMap.containsKey(key)) {
            throw new GalaxyTracingException("Storage container is empty or does not contain field %s, remove property failed!", key);
        }
        Integer lastOp = getAndSetLastOperation();
        if (wasLastOpReadOrNull(lastOp)) {
            Map<String, String> newMap = duplicateAndInsertNewMap(oldMap, null);
            newMap.remove(key);
        } else {
            oldMap.remove(key);
        }
    }
    
    @Override
    public void clear() {
        lastOperation.set(WRITE_OPERATION);
        lastOperation.remove();
        dataMap.remove();
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
        if ((map = dataMap.get()) == null) {
            throw new GalaxyTracingException("Storage container not initialized, get property failed!");
        }
        return map.get(TRACE_ID);
    }
    
    @Override
    public void resetTraceId(final String traceId) {
        Map<String, String> dataMap = getDataMap();
        lastOperation.set(WRITE_OPERATION);
        if (dataMap == null || dataMap.isEmpty()) {
            throw new GalaxyTracingException("Storage container not initialized, get property failed!");
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
    
    private Map<String, String> duplicateAndInsertNewMap(final Map<String, String> oldMap, final String traceId) {
        Map<String, String> newMap = new ConcurrentHashMap<>();
        if (oldMap != null && !oldMap.isEmpty()) {
            newMap.putAll(oldMap);
        } else {
            initDataMap(newMap, traceId);
        }
        dataMap.set(newMap);
        return newMap;
    }
    
    private void initDataMap(final Map<String, String> newMap, final String traceId) {
        if (Strings.isNullOrEmpty(traceId)) {
            newMap.put(TRACE_ID, String.valueOf(snowflakeId.generateId()));
        } else {
            newMap.put(TRACE_ID, traceId);
        }
    }
    
}
