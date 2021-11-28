package org.example.galaxytracing.agent.reporter.http.auth;

/**
 * Permission authentication interface.
 *
 * @author JiekerTime
 */
public interface Auth {
    
    /**
     * Obtain the authenticated Token.
     *
     * @return token
     */
    String getToken();
}
