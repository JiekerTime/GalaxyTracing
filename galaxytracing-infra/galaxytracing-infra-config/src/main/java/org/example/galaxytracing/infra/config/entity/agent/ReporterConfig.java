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

package org.example.galaxytracing.infra.config.entity.agent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.galaxytracing.infra.config.constant.AgentConfigParamsConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Config of agent reporter.
 *
 * @author JiekerTime
 */
@Getter
@Setter
@NoArgsConstructor
public final class ReporterConfig {
    
    private static final String PREFIX = AgentConfigParamsConstant.REPORTER + ".";
    
    private String type;
    
    private Map<String, String> props;
    
    public ReporterConfig(final Properties configuration) {
        this.type = configuration.getProperty(PREFIX + AgentConfigParamsConstant.TYPE);
        props = new HashMap();
        for (String key : configuration.stringPropertyNames()) {
            String propsPrefix = PREFIX + AgentConfigParamsConstant.PROPS + ".";
            if (key.contains(propsPrefix)) {
                props.put(key.replace(propsPrefix, ""), configuration.getProperty(key));
            }
        }
    }
}
