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

package org.example.galaxytracing.agent.bootstrap.storage;

import com.google.common.base.Strings;
import com.huawei.shade.com.alibaba.fastjson.JSON;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.agent.trace.TraceStorage;
import org.example.galaxytracing.agent.trace.TraceStorageFactory;
import org.example.galaxytracing.common.excetion.GalaxyTracingException;

import java.util.Map;

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
        TRACE_STORAGE = TraceStorageFactory.getSnowflakeTraceStorage();
    }
    
    /**
     * 放入值.
     *
     * @param value 存储的对象
     * @return traceId
     */
    public static String put(final Object value) {
        if (value == null) {
            throw new GalaxyTracingException("value parameter cannot be null.");
        }
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.");
        }
        return TRACE_STORAGE.put(JSON.toJSONString(value));
    }
    
    /**
     * 根据traceId取出值.
     *
     * @param traceId traceId
     * @return 存储的值
     */
    public static String get(final String traceId) {
        if (Strings.isNullOrEmpty(traceId)) {
            throw new GalaxyTracingException("key parameter cannot be null.");
        }
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.");
        }
        return TRACE_STORAGE.get(traceId);
    }
    
    /**
     * 删除TraceId对应的值.
     *
     * @param traceId traceId
     */
    public static void remove(final String traceId) {
        if (Strings.isNullOrEmpty(traceId)) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.");
        }
        TRACE_STORAGE.remove(traceId);
    }
    
    /**
     * 清除缓存.
     */
    public static void clear() {
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.");
        }
        TRACE_STORAGE.clear();
    }
    
    /**
     * 获取存储数据的Map.
     *
     * @return map
     */
    public static Map<String, String> getStorageMap() {
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.");
        }
        return TRACE_STORAGE.getCopyOfContextMap();
    }
    
    /**
     * 放入存储值的Map.
     *
     * @param contextMap contextMap.
     */
    public static void setContextMap(final Map<String, String> contextMap) {
        if (TRACE_STORAGE == null) {
            throw new GalaxyTracingException("TraceStorage cannot be null.");
        }
        TRACE_STORAGE.setContextMap(contextMap);
    }
}
