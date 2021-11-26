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

package org.example.galaxytracing.agent.storage;

import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.agent.core.TraceStorage;
import org.example.galaxytracing.agent.core.TraceStorageBinder;
import org.example.galaxytracing.common.excetion.GalaxyTracingException;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * GalaxyTracing 存储入口.
 *
 * @author JiekerTime
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Storage {
    @Getter
    private static final TraceStorage TRACE_STORAGE;
    
    static {
        TRACE_STORAGE = TraceStorageBinder.SINGLETON.getTraceStorage();
    }
    
    /**
     * Putting data into the storage.
     *
     * @param key  key
     * @param data data
     * @throws GalaxyTracingException 系统异常
     */
    public static void put(final String key, final String data) throws GalaxyTracingException {
        if (Strings.isNullOrEmpty(key)) {
            throw new GalaxyTracingException("Key cannot be null or empty, get property failed!");
        }
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.It may cause by init failed!");
        }
        TRACE_STORAGE.put(key, data);
    }
    
    /**
     * Get the data according to the key.
     *
     * @param key key
     * @return data
     * @throws GalaxyTracingException 系统异常
     */
    public static String get(final String key) throws GalaxyTracingException {
        if (Strings.isNullOrEmpty(key)) {
            throw new GalaxyTracingException("Key cannot be null or empty, get property failed!");
        }
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.It may cause by init failed!");
        }
        return TRACE_STORAGE.get(key);
    }
    
    /**
     * Remove the data matching the key.
     *
     * @param key key
     * @throws GalaxyTracingException 系统异常
     */
    public static void remove(final String key) throws GalaxyTracingException {
        if (Strings.isNullOrEmpty(key)) {
            throw new GalaxyTracingException("Key cannot be null or empty, remove property failed!");
        }
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.It may cause by init failed!");
        }
        TRACE_STORAGE.remove(key);
    }
    
    /**
     * Clear all entries in the storage.
     *
     * @throws GalaxyTracingException 系统异常
     */
    public static void clear() throws GalaxyTracingException {
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.It may cause by init failed!");
        }
        TRACE_STORAGE.clear();
    }
    
    /**
     * Get the Map storing the data.
     *
     * @return dataMap
     * @throws GalaxyTracingException 系统异常
     */
    public static Map<String, String> getDataMap() throws GalaxyTracingException {
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.It may cause by init failed!");
        }
        return TRACE_STORAGE.getDataMap();
    }
    
    /**
     * Set the Map that stores the data.
     *
     * @param dataMap dataMap
     * @throws GalaxyTracingException 系统异常
     */
    public static void setDataMap(final Map<String, String> dataMap) throws GalaxyTracingException {
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.It may cause by init failed!");
        }
        TRACE_STORAGE.setDataMap(dataMap);
    }
    
    /**
     * Get the Trace Storage.
     *
     * @return TraceStorage
     * @throws GalaxyTracingException 系统异常
     */
    public static TraceStorage getTraceStorage() {
        return TRACE_STORAGE;
    }
    
    /**
     * Get the keys of the storage.
     *
     * @return keys
     * @throws GalaxyTracingException 系统异常
     */
    public static Set<String> getKeys() throws GalaxyTracingException {
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.It may cause by init failed!");
        }
        return TRACE_STORAGE.getKeys();
    }
    
    /**
     * Get the traceId of the storage.
     *
     * @return traceId
     * @throws GalaxyTracingException 系统异常
     */
    public static String getTraceId() throws GalaxyTracingException {
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.It may cause by init failed!");
        }
        return TRACE_STORAGE.getTraceId();
        
    }
    
    /**
     * Reset the traceId in the storage.
     *
     * @param traceId traceId
     * @throws GalaxyTracingException 系统异常
     */
    public static void resetTraceId(@Nullable final String traceId) throws GalaxyTracingException {
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.It may cause by init failed!");
        }
        TRACE_STORAGE.resetTraceId(traceId);
    }
}
