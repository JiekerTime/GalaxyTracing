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

package org.example.galaxytracing.agent.trace;

import com.google.common.base.Strings;
import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 链路数据存储抽象类.
 *
 * @author JiekerTime
 */
@Getter
public abstract class AbstractTraceStorage implements TraceStorage {
    
    protected static final int WRITE_OPERATION = 1;
    
    protected static final int MAP_COPY_OPERATION = 2;
    
    private final ThreadLocal<Map<String, String>> copyOnThreadLocal = new ThreadLocal<>();
    
    private final ThreadLocal<Integer> lastOperation = new ThreadLocal<>();
    
    @Override
    public String get(final String key) {
        Map<String, String> map = copyOnThreadLocal.get();
        if (map != null && !map.isEmpty() && !Strings.isNullOrEmpty(key)) {
            return map.get(key);
        } else {
            return null;
        }
    }
    
    @Override
    public void remove(final String traceId) {
        if (Strings.isNullOrEmpty(traceId)) {
            return;
        }
        Map<String, String> oldMap = copyOnThreadLocal.get();
        if (oldMap == null) {
            return;
        }
        
        Integer lastOp = getAndSetLastOperation();
        if (wasLastOpReadOrNull(lastOp)) {
            Map<String, String> newMap = duplicateAndInsertNewMap(oldMap);
            newMap.remove(traceId);
        } else {
            oldMap.remove(traceId);
        }
    }
    
    @Override
    public void clear() {
        lastOperation.set(WRITE_OPERATION);
        copyOnThreadLocal.remove();
        lastOperation.remove();
    }
    
    @Override
    public Map<String, String> getCopyOfContextMap() {
        return copyOnThreadLocal.get();
    }
    
    @Override
    public void setContextMap(final Map<String, String> contextMap) {
        lastOperation.set(WRITE_OPERATION);
        
        Map<String, String> newMap = new ConcurrentHashMap<>(contextMap);
        copyOnThreadLocal.set(newMap);
    }
    
    @Override
    public Set<String> getTraceIds() {
        lastOperation.set(MAP_COPY_OPERATION);
        Map<String, String> map = copyOnThreadLocal.get();
        
        if (map != null && !map.isEmpty()) {
            return map.keySet();
        } else {
            return null;
        }
    }
    
    protected Integer getAndSetLastOperation() {
        Integer lastOp = lastOperation.get();
        lastOperation.set(WRITE_OPERATION);
        return lastOp;
    }
    
    protected boolean wasLastOpReadOrNull(final Integer lastOp) {
        return lastOp == null || lastOp == MAP_COPY_OPERATION;
    }
    
    protected Map<String, String> duplicateAndInsertNewMap(final Map<String, String> oldMap) {
        Map<String, String> newMap = new ConcurrentHashMap<>();
        if (oldMap != null) {
            newMap.putAll(oldMap);
        }
        copyOnThreadLocal.set(newMap);
        return newMap;
    }
}
