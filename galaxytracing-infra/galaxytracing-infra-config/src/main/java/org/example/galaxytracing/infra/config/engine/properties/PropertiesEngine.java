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

package org.example.galaxytracing.infra.config.engine.properties;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.infra.config.exception.ConfigurationLoadException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Properties engine.
 *
 * @author JiekerTime
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PropertiesEngine {
    
    /**
     * Parsing the configuration file.
     *
     * @param propertiesFile properties file io stream
     * @return Properties
     * @throws ConfigurationLoadException Configuration file load exception
     */
    public static Properties parseProperties(final File propertiesFile) throws ConfigurationLoadException {
        Properties result = new Properties();
        try (
                FileInputStream fileInputStream = new FileInputStream(propertiesFile);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream)
        ) {
            result.load(inputStreamReader);
        } catch (IOException ex) {
            throw new ConfigurationLoadException(ex);
        }
        return result;
    }
}
