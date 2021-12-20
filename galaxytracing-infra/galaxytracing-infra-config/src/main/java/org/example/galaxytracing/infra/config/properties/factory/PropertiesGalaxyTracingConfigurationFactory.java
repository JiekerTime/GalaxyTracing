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

package org.example.galaxytracing.infra.config.properties.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.infra.config.exception.ConfigurationLoadException;
import org.example.galaxytracing.infra.config.pojo.impl.AgentConfigurationPojo;
import org.example.galaxytracing.infra.config.pojo.impl.ServerConfigurationPojo;
import org.example.galaxytracing.infra.config.properties.engine.PropertiesEngine;

import java.io.File;
import java.util.Properties;

/**
 * GalaxyTracing configuration factory for Properties.
 *
 * @author JiekerTime
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesGalaxyTracingConfigurationFactory {
    
    /**
     * Create agent-side profile entity class.
     *
     * @param propertiesFile properties file io stream
     * @return agent-side profile entity class
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static AgentConfigurationPojo createAgentConfiguration(final File propertiesFile) throws ConfigurationLoadException {
        return PropertiesEngine.parseProperties2Pojo(propertiesFile, AgentConfigurationPojo.class);
    }
    
    /**
     * Create server-side profile entity class.
     *
     * @param propertiesFile properties file io stream
     * @return server-side profile entity class
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static ServerConfigurationPojo createServerConfiguration(final File propertiesFile) throws ConfigurationLoadException {
        return PropertiesEngine.parseProperties2Pojo(propertiesFile, ServerConfigurationPojo.class);
    }
    
    /**
     * Create agent-side properties.
     *
     * @param propertiesFile properties file io stream
     * @return properties
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static Properties createAgentProperties(final File propertiesFile) throws ConfigurationLoadException {
        return PropertiesEngine.parseProperties(propertiesFile);
    }
}
