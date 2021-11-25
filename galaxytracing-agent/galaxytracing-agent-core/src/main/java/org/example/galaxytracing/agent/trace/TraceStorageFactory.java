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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.agent.trace.impl.SnowflakeTraceStorage;

/**
 * 链路存储工厂类.
 *
 * @author JiekerTime
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TraceStorageFactory {
    /**
     * 获取实例.
     *
     * @return 雪花算法实例
     */
    public static TraceStorage getSnowflakeTraceStorage() {
        return new SnowflakeTraceStorage();
    }
}
