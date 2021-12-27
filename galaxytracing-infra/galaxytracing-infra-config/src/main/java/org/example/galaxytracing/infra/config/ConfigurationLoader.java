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

package org.example.galaxytracing.infra.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.infra.config.exception.ConfigurationLoadException;
import org.example.galaxytracing.infra.config.entity.impl.AgentConfiguration;
import org.example.galaxytracing.infra.config.entity.impl.ServerConfiguration;
import org.example.galaxytracing.infra.config.engine.properties.PropertiesEngine;
import org.example.galaxytracing.infra.config.engine.yaml.YamlEngine;

import java.io.File;
import java.util.Properties;

/**
 * Configuration Loader.
 *
 * @author JiekerTime
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationLoader {
    /**
     * Get the io stream of the file.
     *
     * @param fileName configuration file name
     * @param clazz    class
     * @return : stream of the file
     */
    private static File getFile(final String fileName, final Class<?> clazz) {
        try {
            return new File(clazz.getResource(fileName).getFile());
        } catch (NullPointerException ex) {
            throw new ConfigurationLoadException(String.format("Can't find the configuration file %s", fileName), ex);
        }
    }
    
    /**
     * load agent yaml.
     *
     * @param fileName configuration properties file name
     * @param clazz    class
     * @return AgentConfiguration
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static AgentConfiguration loadAgentYaml(final String fileName, final Class<?> clazz) throws ConfigurationLoadException {
        return loadAgentYaml(getFile(String.format(fileName, "yaml"), clazz));
    }
    
    
    /**
     * load agent yaml.
     *
     * @param yamlFile yaml file io stream
     * @return AgentConfiguration
     * @throws ConfigurationLoadException Configuration file load exception
     */
    private static AgentConfiguration loadAgentYaml(final File yamlFile) throws ConfigurationLoadException {
        return (AgentConfiguration) YamlEngine.loadYaml(yamlFile, AgentConfiguration.class);
    }
    
    
    
    /**
     * load yaml.
     *
     * @param fileName configuration properties file name
     * @param clazz    class
     * @return ServerConfiguration
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static ServerConfiguration loadServerYaml(final String fileName, final Class<?> clazz) throws ConfigurationLoadException {
        return loadServerYaml(getFile(String.format(fileName, "yaml"), clazz));
    }
    
    
    /**
     * load yaml.
     *
     * @param yamlFile yaml file io stream
     * @return ServerConfiguration
     * @throws ConfigurationLoadException Configuration file load exception
     */
    private static ServerConfiguration loadServerYaml(final File yamlFile) throws ConfigurationLoadException {
        return (ServerConfiguration) YamlEngine.loadYaml(yamlFile, ServerConfiguration.class);
    }
    
    /**
     * load properties.
     *
     * @param fileName configuration properties file name
     * @param clazz    class
     * @return Properties of configuration
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static Properties loadProperties(final String fileName, final Class<?> clazz) throws ConfigurationLoadException {
        return loadProperties(getFile(String.format(fileName, "properties"), clazz));
    }
    
    /**
     * load properties.
     *
     * @param propertiesFile properties file io stream
     * @return properties
     * @throws ConfigurationLoadException Configuration file load exception
     */
    private static Properties loadProperties(final File propertiesFile) throws ConfigurationLoadException {
        return PropertiesEngine.parseProperties(propertiesFile);
    }
}
