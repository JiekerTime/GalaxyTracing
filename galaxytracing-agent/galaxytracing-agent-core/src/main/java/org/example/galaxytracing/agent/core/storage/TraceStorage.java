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

package org.example.galaxytracing.agent.core.storage;

import org.example.galaxytracing.agent.core.storage.impl.SnowFlakeTraceStorage;

import java.util.Map;
import java.util.Set;

/**
 * Trace Data Storage.
 *
 * @author JiekerTime
 */
public interface TraceStorage {
    /**
     * Putting data into the storage.
     *
     * @param key  key
     * @param data data
     * @return default storage
     */
    SnowFlakeTraceStorage put(String key, String data);
    
    /**
     * Get the data according to the key.
     *
     * @param key key
     * @return data
     */
    String get(String key);
    
    /**
     * Remove the data matching the key.
     *
     * @param key key
     */
    void remove(String key);
    
    /**
     * Clear all entries in the storage.
     *
     * @return json of whole map
     */
    String clear();
    
    /**
     * Get the Map storing the data.
     *
     * @return dataMap
     */
    Map<String, String> getDataMap();
    
    /**
     * Set the Map that stores the data.
     *
     * @param dataMap dataMap.
     */
    void setDataMap(Map<String, String> dataMap);
    
    /**
     * Get the keys of the storage.
     *
     * @return keys
     */
    Set<String> getKeys();
    
    /**
     * Get the traceId of the storage.
     *
     * @return traceId
     */
    String getTraceId();
    
    /**
     * Reset the traceId in the storage.
     *
     * @param traceId traceId
     */
    void resetTraceId(String traceId);
    
}
