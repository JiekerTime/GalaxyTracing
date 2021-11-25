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

import java.util.Map;
import java.util.Set;

/**
 * 链路数据存储适配器.
 *
 * @author JiekerTime
 */
public interface TraceStorage {
    /**
     * 放入值.
     *
     * @param value 存储的值
     * @return traceId
     */
    String put(String value);
    
    /**
     * 根据traceId取出值.
     *
     * @param traceId traceId
     * @return 存储的值
     */
    String get(String traceId);
    
    /**
     * 删除TraceId对应的值.
     *
     * @param traceId traceId
     */
    void remove(String traceId);
    
    /**
     * 清除缓存.
     */
    void clear();
    
    /**
     * 获取存储数据的Map.
     *
     * @return map
     */
    Map<String, String> getCopyOfContextMap();
    
    /**
     * 放入存储值的Map.
     *
     * @param contextMap contextMap.
     */
    void setContextMap(Map<String, String> contextMap);
    
    /**
     * 获取所有TraceId.
     *
     * @return TraceId
     */
    Set<String> getTraceIds();
    
}
