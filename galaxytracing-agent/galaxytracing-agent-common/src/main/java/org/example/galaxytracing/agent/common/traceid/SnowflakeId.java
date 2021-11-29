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

package org.example.galaxytracing.agent.common.traceid;

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import org.example.galaxytracing.core.exception.GalaxyTracingException;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;

/**
 * SnowflakeId Core.
 *
 * @author JiekerTime
 */
public final class SnowflakeId {
    
    /**
     * 开始时间戳.
     */
    public static final long EPOCH;
    
    /**
     * 机器ID在雪花ID中所占的位数.
     */
    public static final long WORKER_ID_BITS = 5L;
    
    /**
     * 数据标识在雪花ID中所占位数.
     */
    public static final long DATA_ID_BITS = 5L;
    
    /**
     * 序列在雪花ID中占的位数.
     */
    public static final long SEQUENCE_BITS = 12L;
    
    /**
     * 支持的最大机器ID.
     */
    public static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    
    /**
     * 支持的最大数据标识ID.
     */
    public static final long MAX_DATA_ID = ~(-1L << DATA_ID_BITS);
    
    
    /**
     * 数据标识ID向左移17位(12+5).
     */
    public static final long DATA_ID_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    
    /**
     * 机器ID向左移12位.
     */
    public static final long WORKER_ID_LEFT_SHIFT = SEQUENCE_BITS;
    
    /**
     * 时间截向左移22位(5+5+12).
     */
    public static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_ID_BITS;
    
    /**
     * 序列掩码.
     */
    public static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    
    /**
     * 上一次生成时间戳.
     */
    private long lastTimestamp = -1L;
    
    /**
     * 机器ID.
     */
    private final long workerId;
    
    /**
     * 数据标识.
     */
    private final long dataId;
    
    /**
     * 序列ID.
     */
    private long sequence;
    
    private int sequenceOffset = -1;
    
    private final int maxVibrationOffset;
    
    private final int maxTolerateTimeDifferenceMilliseconds;
    
    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.NOVEMBER, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        EPOCH = calendar.getTimeInMillis();
    }
    
    public SnowflakeId() {
        this(generateWorkId(generateDataId()), 1, 10);
        
    }
    
    public SnowflakeId(final long workerId, final long dataId) {
        this(workerId, dataId, 1, 10);
    }
    
    public SnowflakeId(final long dataId, final int maxVibrationOffset, final int maxTolerateTimeDifferenceMilliseconds) {
        this(generateWorkId(dataId), dataId, maxVibrationOffset, maxTolerateTimeDifferenceMilliseconds);
    }
    
    public SnowflakeId(final long workerId, final long dataId, final int maxVibrationOffset, final int maxTolerateTimeDifferenceMilliseconds) {
        Preconditions.checkArgument(workerId >= 0L && workerId < MAX_WORKER_ID, "Illegal worker id.");
        Preconditions.checkArgument(dataId >= 0L && dataId < MAX_DATA_ID, "Illegal data id.");
        Preconditions.checkArgument(maxVibrationOffset >= 0 && maxVibrationOffset <= SEQUENCE_MASK, "Illegal max vibration offset.");
        this.workerId = workerId;
        this.dataId = dataId;
        this.maxVibrationOffset = maxVibrationOffset;
        this.maxTolerateTimeDifferenceMilliseconds = maxTolerateTimeDifferenceMilliseconds;
    }
    
    /**
     * 获生成一个ID.
     *
     * @return SnowflakeId
     */
    public synchronized long generateId() {
        long currentTimestamp = System.currentTimeMillis();
        
        if (waitTolerateTimeDifferenceIfNeed(currentTimestamp)) {
            currentTimestamp = System.currentTimeMillis();
        }
        
        if (lastTimestamp == currentTimestamp) {
            if (0L == (sequence = (sequence + 1) & SEQUENCE_MASK)) {
                currentTimestamp = waitUntilNextTime(currentTimestamp);
            }
        } else {
            vibrateSequenceOffset();
            sequence = sequenceOffset;
        }
        lastTimestamp = currentTimestamp;
        // ID偏移组合生成最终的ID，并返回ID
        return ((currentTimestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT) | (dataId << DATA_ID_LEFT_SHIFT) | (workerId << WORKER_ID_LEFT_SHIFT) | sequence;
    }
    
    private void vibrateSequenceOffset() {
        sequenceOffset = sequenceOffset >= maxVibrationOffset ? 0 : sequenceOffset + 1;
    }
    
    private long waitUntilNextTime(final long lastTime) {
        long result = System.currentTimeMillis();
        while (result <= lastTime) {
            result = System.currentTimeMillis();
        }
        return result;
    }
    
    @SneakyThrows(InterruptedException.class)
    private boolean waitTolerateTimeDifferenceIfNeed(final long currentMilliseconds) {
        if (lastTimestamp <= currentMilliseconds) {
            return false;
        }
        long timeDifferenceMilliseconds = lastTimestamp - currentMilliseconds;
        Preconditions.checkState(timeDifferenceMilliseconds < maxTolerateTimeDifferenceMilliseconds,
                "Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds", lastTimestamp, currentMilliseconds);
        Thread.sleep(timeDifferenceMilliseconds);
        return true;
    }
    
    /**
     * 生成数据标识符.
     *
     * @return dataId
     */
    public static long generateDataId() {
        long result;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                result = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                result = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                result = result % (MAX_DATA_ID + 1);
            }
        } catch (UnknownHostException | SocketException e) {
            throw new GalaxyTracingException("During generation dataId of SnowflakeId an exception occurs", e);
        }
        return result;
    }
    
    /**
     * 生成机器ID.
     *
     * @param dataId 数据标识
     * @return workId
     */
    public static long generateWorkId(final long dataId) {
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
        return (result.toString().hashCode() & 0xffff) % (MAX_WORKER_ID + 1);
    }
    
}
