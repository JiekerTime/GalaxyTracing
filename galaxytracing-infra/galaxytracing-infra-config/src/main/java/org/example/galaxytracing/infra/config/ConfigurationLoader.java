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
import org.example.galaxytracing.infra.config.pojo.impl.AgentConfigurationPojo;
import org.example.galaxytracing.infra.config.pojo.impl.ServerConfigurationPojo;
import org.example.galaxytracing.infra.config.properties.factory.PropertiesGalaxyTracingConfigurationFactory;
import org.example.galaxytracing.infra.config.yaml.factory.YamlGalaxyTracingConfigurationFactory;

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
     * Load the agent's yaml configuration file.
     *
     * @param fileName configuration yaml file name
     * @param clazz    class
     * @return Agent Configuration Pojo
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static AgentConfigurationPojo loadAgentYaml(final String fileName, final Class<?> clazz) throws ConfigurationLoadException {
        return YamlGalaxyTracingConfigurationFactory.createAgentConfiguration(getFile(String.format(fileName, "yaml"), clazz));
    }
    
    /**
     * Load the server's yaml configuration file.
     *
     * @param fileName configuration yaml file name
     * @param clazz    class
     * @return Server Configuration Pojo
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static ServerConfigurationPojo loadServerYaml(final String fileName, final Class<?> clazz) throws ConfigurationLoadException {
        return YamlGalaxyTracingConfigurationFactory.createServerConfiguration(getFile(String.format(fileName, "yaml"), clazz));
    }
    
    /**
     * Load the agent's properties configuration file.
     *
     * @param fileName configuration properties file name
     * @param clazz    class
     * @return Agent Configuration Pojo
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static AgentConfigurationPojo loadAgentProperties(final String fileName, final Class<?> clazz) throws ConfigurationLoadException {
        return PropertiesGalaxyTracingConfigurationFactory.createAgentConfiguration(getFile(String.format(fileName, "properties"), clazz));
    }
    
    /**
     * Load the server's properties configuration file.
     *
     * @param fileName configuration properties file name
     * @param clazz    class
     * @return Server Configuration Pojo
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static ServerConfigurationPojo loadServerProperties(final String fileName, final Class<?> clazz) throws ConfigurationLoadException {
        return PropertiesGalaxyTracingConfigurationFactory.createServerConfiguration(getFile(String.format(fileName, "properties"), clazz));
    }
    
    /**
     * Converting yaml configuration files to properties.
     *
     * @param fileName configuration properties file name
     * @param clazz    class
     * @return Properties of configuration
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static Properties getYamlConfigProperties(final String fileName, final Class<?> clazz) throws ConfigurationLoadException {
        return YamlGalaxyTracingConfigurationFactory.createAgentProperties(getFile(String.format(fileName, "yaml"), clazz));
    }
    
    /**
     * Converting properties configuration files to properties pojo.
     *
     * @param fileName configuration properties file name
     * @param clazz    class
     * @return Properties of configuration
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static Properties getPropertiesConfigProperties(final String fileName, final Class<?> clazz) throws ConfigurationLoadException {
        return PropertiesGalaxyTracingConfigurationFactory.createAgentProperties(getFile(String.format(fileName, "properties"), clazz));
    }
}
