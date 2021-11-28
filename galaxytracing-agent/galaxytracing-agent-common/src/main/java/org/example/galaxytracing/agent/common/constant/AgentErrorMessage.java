package org.example.galaxytracing.agent.common.constant;

/**
 * Error Message Constant.
 *
 * @author JiekerTime
 */
public final class AgentErrorMessage {
    
    public static final String NULL_TRACE_STORAGE_ERROR = "TraceStorage is null causing the operation to fail. This may be due to a failed initialization.";
    
    public static final String NULL_KEY_ERROR = "The key value cannot be empty, the operation fails.";
    
    public static final String KEY_VALUE_NOT_MATCH_ERROR = "Please enter the standard key value format, the number of keys does not match, the operation fails.";
    
    public static final String REPORTER_SHUTDOWN_ERROR = "Reporter service has been shut down, please restart GalaxyTracing service!";
    
    public static final String STORAGE_NOT_INIT_ERROR = "The storage container was not initialized and the operation failed!";
}
