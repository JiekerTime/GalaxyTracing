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

package org.example.galaxytracing.infra.config.yaml.engine;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.infra.config.exception.ConfigurationLoadException;
import org.example.galaxytracing.infra.config.pojo.ConfigurationPojo;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * YAML engine.
 *
 * @author JiekerTime
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YamlEngine {
    
    /**
     * Parsing the configuration file.
     *
     * @param yamlFile yaml file io stream
     * @return java.util.Properties
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static Properties parseYaml2Properties(final File yamlFile) throws ConfigurationLoadException {
        Properties result = new Properties();
        try (
                FileInputStream fileInputStream = new FileInputStream(yamlFile);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream)
        ) {
            final Map<String, Object> configMap = new Yaml().load(inputStreamReader);
            for (String key : configMap.keySet()) {
                result.setProperty(key, String.valueOf(configMap.get(key)));
            }
        } catch (IOException ex) {
            throw new ConfigurationLoadException(ex);
        }
        return result;
    }
    
    /**
     * Parsing configuration files to entity classes.
     *
     * @param <T> Profile entities class
     * @param classType Profile entities class
     * @param yamlFile yaml file io stream
     * @return T Profile entities class
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static <T extends ConfigurationPojo> T parseYaml2Pojo(final File yamlFile, final Class<T> classType) throws ConfigurationLoadException {
        try (
                FileInputStream fileInputStream = new FileInputStream(yamlFile);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream)
        ) {
            if (Arrays.stream(classType.getInterfaces()).anyMatch(clazz -> clazz == ConfigurationPojo.class)) {
                return new Yaml().loadAs(inputStreamReader, classType);
            }
        } catch (IOException ex) {
            throw new ConfigurationLoadException(ex);
        }
        throw new ConfigurationLoadException(classType);
    }
    
}
