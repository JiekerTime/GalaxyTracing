package org.example.galaxytracing.core.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadFactory;


/**
 * Executor thread factory builder.
 *
 * @author JiekerTime
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExecutorThreadFactoryBuilder {
    
    private static final String NAME_FORMAT_PREFIX = "GalaxyTracing-";
    
    private static final String DEFAULT_EXECUTOR_NAME_FORMAT = NAME_FORMAT_PREFIX + "%d";
    
    /**
     * Build default thread factory.
     *
     * @return default thread factory
     */
    public static ThreadFactory build() {
        return new ThreadFactoryBuilder().setDaemon(true).setNameFormat(DEFAULT_EXECUTOR_NAME_FORMAT).build();
    }
    
    /**
     * Build thread factory with thread name format.
     *
     * @param nameFormat thread name format
     * @return thread factory
     */
    public static ThreadFactory build(final String nameFormat) {
        return new ThreadFactoryBuilder().setDaemon(true).setNameFormat(NAME_FORMAT_PREFIX + nameFormat).build();
    }
}

