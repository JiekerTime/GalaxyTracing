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

package org.example.galaxytracing.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Error Message Constant.
 *
 * @author JiekerTime
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GalaxyTracingAgentMessage {
    
    public static final String NULL_TRACE_STORAGE_ERROR = "TraceStorage is null causing the operation to fail. This may be due to a failed initialization.";
    
    public static final String NULL_KEY_ERROR = "The key value cannot be empty, the operation fails.";
    
    public static final String NULL_OBJ_ERROR = "The entity cannot be empty, the operation fails.";
    
    public static final String KEY_VALUE_NOT_MATCH_ERROR = "Please enter the standard key value format, the number of keys does not match, the operation fails.";
    
    public static final String REPORTER_SHUTDOWN_ERROR = "Reporter service has been shut down, please restart GalaxyTracing service!";
    
    public static final String STORAGE_NOT_INIT_ERROR = "The storage container was not initialized and the operation failed!";
}
