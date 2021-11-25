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

package org.example.galaxytracing.common.constant;


import lombok.Getter;
import org.example.galaxytracing.common.excetion.GalaxyTracingException;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 雪花算法ID
 *
 * @author JiekerTime
 * @since 2021/11/25 19:49
 */
@Getter
public final class SnowflakeId {
    /**
     * 开始时间戳(2021-01-01)
     */
    private final long TWEPOCH = 1609430400000L;
    /**
     * 机器ID在雪花ID中所占的位数
     */
    private final long WORK_ID_BITS = 5;
    /**
     * 数据标识在雪花ID中所占位数
     */
    private final long DATA_ID_BITS = 5;
    /**
     * 支持的最大机器ID
     */
    private final long MAX_WORKER_ID = ~(-1L << WORK_ID_BITS);
    /**
     * 支持的最大数据标识ID
     */
    private final long MAX_DATA_ID = ~(-1L << DATA_ID_BITS);
    /**
     * 序列在雪花ID中占的位数
     */
    private final long SEQUENCE_BITS = 12;
    /**
     * 数据标识ID向左移17位(12+5)
     */
    private final long DATA_ID_SHIFT = SEQUENCE_BITS + WORK_ID_BITS;
    /**
     * 机器ID向左移12位
     */
    private final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /**
     * 时间截向左移22位(5+5+12)
     */
    private final long TIME_STAMP_LEFT_SHIFT = SEQUENCE_BITS + WORK_ID_BITS + DATA_ID_BITS;
    /**
     * 序列掩码
     */
    private final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    /**
     * 上一次生成时间戳
     */
    private long lastTimestamp = -1L;
    /**
     * 序列ID
     */
    private long sequence = 0L;
    /**
     * 机器ID
     */
    private final long WORKER_ID;
    /**
     * 数据标识
     */
    private final long DATA_ID;
    
    
    public SnowflakeId() {
        DATA_ID = generateDataId(MAX_DATA_ID);
        WORKER_ID = generateWorkId(DATA_ID, MAX_WORKER_ID);
    }
    
    /**
     * @param workerId 机器ID
     * @param dataId   数据标识
     */
    public SnowflakeId(long workerId, long dataId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new GalaxyTracingException("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID);
        }
        if (dataId > MAX_DATA_ID || dataId < 0) {
            throw new GalaxyTracingException("datacenter Id can't be greater than %d or less than 0", MAX_DATA_ID);
        }
        this.WORKER_ID = workerId;
        this.DATA_ID = dataId;
    }
    
    /**
     * 获取下一个ID
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();
        
        if (currentTimestamp < lastTimestamp) {
            throw new GalaxyTracingException("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - currentTimestamp);
        }
        
        if (lastTimestamp == currentTimestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                long timestamp = System.currentTimeMillis();
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
                currentTimestamp = timestamp;
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = currentTimestamp;
        // ID偏移组合生成最终的ID，并返回ID
        return ((currentTimestamp - TWEPOCH) << TIME_STAMP_LEFT_SHIFT) | (DATA_ID << DATA_ID_SHIFT) | (WORKER_ID << WORKER_ID_SHIFT) | sequence;
    }
    
    /**
     * 生成数据标识符
     *
     * @return dataId
     */
    private static long generateDataId(long maxDataId) {
        long result;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                result = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                result = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                result = result % (maxDataId + 1);
            }
        } catch (UnknownHostException | SocketException e) {
            throw new GalaxyTracingException("During generation dataId of SnowflakeId an exception occurs", e);
        }
        return result;
    }
    
    /**
     * 生成机器ID
     *
     * @return workId
     */
    private static long generateWorkId(long dataId, long maxWorkerId) {
        StringBuilder result = new StringBuilder(String.valueOf(dataId));
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            /*
             * GET jvmPid
             */
            result.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (result.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }
    
}
