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

package org.example.galaxytracing.common.utinitys;

import com.huawei.shade.com.alibaba.fastjson.JSONObject;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Object format conversion.
 *
 * @author JiekerTime
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataFormatTransfer {
    
    /**
     * Transfer obj to map.
     *
     * @param obj obj
     * @return map
     */
    public static Map transObject2Map(final Object obj) {
        return JSONObject.parseObject(JSONObject.toJSONString(obj), Map.class);
    }
    
    /**
     * Transfer map to obj.
     *
     * @param map   map
     * @param clazz class
     * @param <T>   obj type
     * @return obj
     */
    public static <T> T transMap2Bean(final Map<String, Object> map, final Class<T> clazz) {
        return JSONObject.parseObject(JSONObject.toJSONString(map), clazz);
    }
}
