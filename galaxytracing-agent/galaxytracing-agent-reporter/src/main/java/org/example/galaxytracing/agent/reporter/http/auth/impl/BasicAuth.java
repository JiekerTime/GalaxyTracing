package org.example.galaxytracing.agent.reporter.http.auth.impl;

import com.huawei.shade.org.apache.commons.codec.binary.Base64;
import lombok.AllArgsConstructor;
import org.example.galaxytracing.agent.reporter.http.auth.Auth;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of basic permission authentication, account password authentication.
 *
 * @author JiekerTime
 */
@AllArgsConstructor
public final class BasicAuth implements Auth {
    
    private final String username;
    
    private final String password;
    
    @Override
    public String getToken() {
        // TODO add config
        String auth = String.format("%s:%s", this.username, this.password);
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
    }
}
