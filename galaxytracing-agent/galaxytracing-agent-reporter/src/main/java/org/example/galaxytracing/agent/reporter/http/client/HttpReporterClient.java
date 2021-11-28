package org.example.galaxytracing.agent.reporter.http.client;

import com.huawei.shade.com.cloud.sdk.http.HttpMethodName;
import com.huawei.shade.com.cloud.sdk.util.StringUtils;
import com.huawei.shade.org.apache.http.HeaderElement;
import com.huawei.shade.org.apache.http.HeaderElementIterator;
import com.huawei.shade.org.apache.http.HttpHeaders;
import com.huawei.shade.org.apache.http.HttpResponse;
import com.huawei.shade.org.apache.http.client.HttpClient;
import com.huawei.shade.org.apache.http.client.methods.RequestBuilder;
import com.huawei.shade.org.apache.http.conn.HttpClientConnectionManager;
import com.huawei.shade.org.apache.http.entity.ContentType;
import com.huawei.shade.org.apache.http.entity.StringEntity;
import com.huawei.shade.org.apache.http.impl.client.HttpClients;
import com.huawei.shade.org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import com.huawei.shade.org.apache.http.message.BasicHeaderElementIterator;
import com.huawei.shade.org.apache.http.protocol.HTTP;
import com.huawei.shade.org.apache.http.protocol.HttpContext;
import com.huawei.shade.org.apache.http.util.EntityUtils;
import lombok.NoArgsConstructor;
import org.example.galaxytracing.agent.reporter.http.auth.Auth;
import org.example.galaxytracing.agent.reporter.http.auth.impl.BasicAuth;
import org.example.galaxytracing.common.excetion.GalaxyTracingException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * A client that sends data using the Http protocol.
 *
 * @author JiekerTime
 */
@NoArgsConstructor
public final class HttpReporterClient {
    /**
     * Maximum number of client connections.
     */
    private static final int MAX_CONN_COUNT = 100;
    
    /**
     * Maximum number of server-side connections.
     */
    private static final int MAX_ROUTE_CONN_COUNT = 10;
    
    private static final HttpClient HTTP_CLIENT;
    
    private static final Auth AUTH;
    
    private static final PoolingHttpClientConnectionManager CONNECTION_MANAGER;
    
    private static final IdleConnectionMonitor CONNECTION_MONITOR;
    
    static {
        // TODO add config
        AUTH = new BasicAuth("root", "root");
        CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();
        CONNECTION_MANAGER.setMaxTotal(MAX_CONN_COUNT);
        CONNECTION_MANAGER.setDefaultMaxPerRoute(MAX_ROUTE_CONN_COUNT);
        HTTP_CLIENT = HttpClients.custom().setConnectionManager(CONNECTION_MANAGER)
                .setKeepAliveStrategy(HttpReporterClient::getKeepAliveDuration).build();
        CONNECTION_MONITOR = new IdleConnectionMonitor(CONNECTION_MANAGER);
        CONNECTION_MONITOR.start();
    }
    
    /**
     * Sending data to the server.
     *
     * @param url url
     * @param value value
     * @throws GalaxyTracingException System Exception
     */
    public static void doPost(final String url, final String value) throws GalaxyTracingException {
        RequestBuilder reqBuilder = RequestBuilder.create(HttpMethodName.POST.toString())
                .setUri(url)
                .addHeader(HttpHeaders.AUTHORIZATION, AUTH.getToken())
                .addHeader("Accept", ContentType.APPLICATION_JSON.toString())
                .addHeader("Content-type", ContentType.APPLICATION_JSON.toString());
        if (StringUtils.isNullOrEmpty(value)) {
            reqBuilder.setEntity(new StringEntity(value, ContentType.APPLICATION_JSON));
        }
        try {
            EntityUtils.consume(HTTP_CLIENT.execute(reqBuilder.build()).getEntity());
        } catch (IOException e) {
            throw new GalaxyTracingException("发送数据发生异常, case:%s", e.getLocalizedMessage(), e);
        }
    }
    
    /**
     * Shutdown the reporter server.
     */
    public static void shutdown() {
        CONNECTION_MANAGER.shutdown();
    }
    
    private static long getKeepAliveDuration(final HttpResponse response, final HttpContext httpContext) {
        HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && "timeout".equalsIgnoreCase(param)) {
                return Long.parseLong(value) * 1000;
            }
        }
        /*
         If there is no agreement, the default definition of the duration is 60s.
        */
        return 60 * 1000;
    }
    
    private static class IdleConnectionMonitor extends Thread {
        
        private final HttpClientConnectionManager connectionManager;
        
        private volatile boolean shutdown;
        
        IdleConnectionMonitor(final HttpClientConnectionManager connectionManager) {
            super();
            this.connectionManager = connectionManager;
        }
        
        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(5000);
                        /* Close expired connections */
                        connectionManager.closeExpiredConnections();
                    /*
                     Optionally, close connections
                     that have been idle longer than 30 sec
                    */
                        connectionManager.closeIdleConnections(30, TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                /* terminate */
            }
        }
        
        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
        
    }
}
