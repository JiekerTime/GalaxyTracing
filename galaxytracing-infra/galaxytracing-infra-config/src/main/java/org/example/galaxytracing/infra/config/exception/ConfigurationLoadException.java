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

package org.example.galaxytracing.infra.config.exception;

/**
 * Configuration load exceptions.
 *
 * @author JiekerTime
 */
public final class ConfigurationLoadException extends RuntimeException {
    
    private static final long serialVersionUID = 3638709065415032081L;
    
    public ConfigurationLoadException(final String message) {
        super(String.format("An error occurred while parsing yaml file : `%s`.", message));
    }
    
    public ConfigurationLoadException(final String message, final Exception exception) {
        super(String.format("An error occurred while parsing yaml file : `%s`.", message), exception);
    }
    
    public ConfigurationLoadException(final Class<?> clazz) {
        super(String.format("Wrong configuration pojo : `%s`.", clazz.getName()));
    }
    
    public ConfigurationLoadException(final Exception exception) {
        super(String.format("An exception occurred while parsing yaml file : `%s`.", exception.getMessage()), exception);
    }
}
