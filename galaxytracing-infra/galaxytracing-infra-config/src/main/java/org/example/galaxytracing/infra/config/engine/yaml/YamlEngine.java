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

package org.example.galaxytracing.infra.config.engine.yaml;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.infra.config.exception.ConfigurationLoadException;
import org.example.galaxytracing.infra.config.entity.Configuration;
import org.example.galaxytracing.infra.config.entity.impl.AgentConfiguration;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
     * @param clazz    configuration pojo
     * @return java.util.Properties
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static Configuration loadYaml(final File yamlFile, final Class<?> clazz) throws ConfigurationLoadException {
        AgentConfiguration result;
        try (
                FileInputStream fileInputStream = new FileInputStream(yamlFile);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream)
        ) {
            Yaml yaml = new Yaml(new Constructor(clazz));
            result = yaml.load(inputStreamReader);
        } catch (IOException ex) {
            throw new ConfigurationLoadException(ex);
        }
        return result;
    }
    
}
