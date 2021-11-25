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

import org.example.galaxytracing.common.excetion.GalaxyTracingException;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 雪花算法ID.
 *
 * @author JiekerTime
 */
public final class SnowflakeId {
    
    /**
     * 开始时间戳(2021-01-01).
     */
    private final long baseTimeStamp = 1609430400000L;
    
    /**
     * 机器ID在雪花ID中所占的位数.
     */
    private final long workIdBits = 5;
    
    /**
     * 数据标识在雪花ID中所占位数.
     */
    private final long dataIdBits = 5;
    
    /**
     * 支持的最大机器ID.
     */
    private final long maxWorkerId = ~(-1L << workIdBits);
    
    /**
     * 支持的最大数据标识ID.
     */
    private final long maxDataId = ~(-1L << dataIdBits);
    
    /**
     * 序列在雪花ID中占的位数.
     */
    private final long sequenceBits = 12;
    
    /**
     * 数据标识ID向左移17位(12+5).
     */
    private final long dataIdShift = sequenceBits + workIdBits;
    
    /**
     * 机器ID向左移12位.
     */
    private final long workerIdShift = sequenceBits;
    
    /**
     * 时间截向左移22位(5+5+12).
     */
    private final long timeStampLeftShift = sequenceBits + workIdBits + dataIdBits;
    
    /**
     * 序列掩码.
     */
    private final long sequenceMask = ~(-1L << sequenceBits);
    
    /**
     * 上一次生成时间戳.
     */
    private long lastTimestamp = -1L;
    
    /**
     * 序列ID.
     */
    private long sequence;
    
    /**
     * 机器ID.
     */
    private final long workerId;
    
    /**
     * 数据标识.
     */
    private final long dataId;
    
    public SnowflakeId() {
        dataId = generateDataId(maxDataId);
        workerId = generateWorkId(dataId, maxWorkerId);
    }
    
    /**
     * 构造方法.
     *
     * @param workerId 机器ID
     * @param dataId   数据标识
     */
    public SnowflakeId(final long workerId, final long dataId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new GalaxyTracingException("worker Id can't be greater than %d or less than 0", maxWorkerId);
        }
        if (dataId > maxDataId || dataId < 0) {
            throw new GalaxyTracingException("datacenter Id can't be greater than %d or less than 0", maxDataId);
        }
        this.workerId = workerId;
        this.dataId = dataId;
    }
    
    /**
     * 获取下一个ID.
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
            sequence = (sequence + 1) & sequenceMask;
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
        return ((currentTimestamp - baseTimeStamp) << timeStampLeftShift) | (dataId << dataIdShift) | (workerId << workerIdShift) | sequence;
    }
    
    /**
     * 生成数据标识符.
     *
     * @param maxDataId 数据标识符最大值
     * @return dataId
     */
    public static long generateDataId(final long maxDataId) {
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
     * 生成机器ID.
     *
     * @param dataId   数据标识
     * @param maxWorkerId 机器ID最大值
     * @return workId
     */
    public static long generateWorkId(final long dataId, final long maxWorkerId) {
        StringBuilder result = new StringBuilder(String.valueOf(dataId));
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            /*
             * GET jvmPid.
             */
            result.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位.
         */
        return (result.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }
    
}
