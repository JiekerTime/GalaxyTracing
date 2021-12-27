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

package org.example.galaxytracing.infra.config.entity.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.galaxytracing.infra.config.entity.Configuration;
import org.example.galaxytracing.infra.config.entity.agent.BasicConfig;
import org.example.galaxytracing.infra.config.entity.agent.ReporterConfig;

import java.util.Properties;

/**
 * Configuration pojo of agent.
 *
 * @author JiekerTime
 */
@Getter
@Setter
@NoArgsConstructor
public final class AgentConfiguration implements Configuration {
    
    private BasicConfig basic;
    
    private ReporterConfig reporter;
    
    public AgentConfiguration(final Properties configuration) {
        this.basic = new BasicConfig(configuration);
        this.reporter = new ReporterConfig(configuration);
    }
}
