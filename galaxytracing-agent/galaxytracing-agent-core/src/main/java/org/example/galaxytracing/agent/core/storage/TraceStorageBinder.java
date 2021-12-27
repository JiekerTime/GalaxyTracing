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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.agent.core.storage.impl.SnowFlakeTraceStorage;
import org.example.galaxytracing.infra.common.exception.GalaxyTracingException;
import org.example.galaxytracing.infra.config.constant.AgentBasicParamsValuesConstant;
import org.example.galaxytracing.infra.config.entity.impl.AgentConfiguration;

/**
 * This implementation is bound to {@link SnowFlakeTraceStorage}.
 *
 * @author JiekerTime
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraceStorageBinder {
    /**
     * The unique instance of this class.
     */
    public static final TraceStorageBinder INSTANCE = new TraceStorageBinder();
    
    private static TraceStorage singleton;
    
    /**
     * The instance of trace data storage.
     *
     * @param configuration configuration of agent
     * @return TraceStorage
     */
    public TraceStorage getInstance(final AgentConfiguration configuration) {
        if (singleton == null) {
            synchronized (INSTANCE) {
                if (singleton == null) {
                    switch (configuration.getBasic().getTracingType()) {
                        case AgentBasicParamsValuesConstant.TYPE_DEFAULT:
                        case AgentBasicParamsValuesConstant.TYPE_SNOWFLAKE:
                            singleton = new SnowFlakeTraceStorage();
                            break;
                        default:
                            throw new GalaxyTracingException("Unsupported agent types %s.",
                                    configuration.getBasic().getTracingType());
                    }
                }
            }
        }
        return singleton;
    }
}
