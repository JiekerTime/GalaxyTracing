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

package org.example.galaxytracing.agent.trace.impl;

import org.example.galaxytracing.agent.trace.AbstractTraceStorage;
import org.example.galaxytracing.common.constant.SnowflakeId;

import java.util.Map;

/**
 * 雪花算法实现链路存储适配器.
 *
 * @author JiekerTime
 */
public final class SnowflakeTraceStorage extends AbstractTraceStorage {
    
    private final SnowflakeId snowflakeId;
    
    public SnowflakeTraceStorage() {
        snowflakeId = new SnowflakeId();
    }
    
    @Override
    public String put(final String value) {
        long traceId = snowflakeId.nextId();
        Map<String, String> oldMap = getCopyOnThreadLocal().get();
        
        String result = String.valueOf(traceId);
        Integer lastOp = getAndSetLastOperation();
        
        if (wasLastOpReadOrNull(lastOp) || oldMap == null) {
            Map<String, String> newMap = duplicateAndInsertNewMap(oldMap);
            newMap.put(result, value);
        } else {
            oldMap.put(result, value);
        }
        return result;
    }
}
