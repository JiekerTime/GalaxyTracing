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

package org.example.galaxytracing.agent.reporter.http.client;

import com.huawei.shade.com.cloud.sdk.http.HttpMethodName;
import com.huawei.shade.com.cloud.sdk.util.StringUtils;
import com.huawei.shade.org.apache.http.HeaderElement;
import com.huawei.shade.org.apache.http.HeaderElementIterator;
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
import lombok.extern.slf4j.Slf4j;
import org.example.galaxytracing.infra.common.exception.GalaxyTracingException;
import org.example.galaxytracing.infra.config.constant.AgentConfigParamsConstant;
import org.example.galaxytracing.infra.config.entity.agent.ReporterConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * A client that sends data using the Http protocol.
 *
 * @author JiekerTime
 */
@Slf4j(topic = "agent")
@NoArgsConstructor
public final class HttpReporterClient implements IReporterClient {
    
    private static final String DEFAULT_URL = "http://localhost:9000/collector";
    
    /**
     * Maximum number of client connections.
     */
    private static final int DEFAULT_MAX_CONN_COUNT = 100;
    
    /**
     * Maximum number of server-side connections.
     */
    private static final int DEFAULT_MAX_ROUTE_CONN_COUNT = 10;
    
    private HttpClient httpClient;
    
    private IdleConnectionMonitor idleConnectionMonitor;
    
    private String url;
    
    public HttpReporterClient(final ReporterConfig reporterConfig) {
        this.url = reporterConfig.getProps().get(AgentConfigParamsConstant.URL);
        if (url == null || "".equals(url)) {
            url = DEFAULT_URL;
        }
        final String configMaxConnCount = reporterConfig.getProps().get(AgentConfigParamsConstant.MAX_CONN_COUNT);
        final String configMaxRouteConnCount = reporterConfig.getProps().get(AgentConfigParamsConstant.MAX_ROUTE_CONN_COUNT);
        final int maxConnCount = configMaxConnCount == null ? DEFAULT_MAX_CONN_COUNT : Integer.parseInt(configMaxConnCount);
        final int maxRouteConnCount = configMaxRouteConnCount == null ? DEFAULT_MAX_ROUTE_CONN_COUNT : Integer.parseInt(configMaxRouteConnCount);
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxConnCount);
        connectionManager.setDefaultMaxPerRoute(maxRouteConnCount);
        this.httpClient = HttpClients.custom().setConnectionManager(connectionManager)
                .setKeepAliveStrategy(HttpReporterClient::getKeepAliveDuration).build();
        this.idleConnectionMonitor = new IdleConnectionMonitor(connectionManager);
        this.idleConnectionMonitor.start();
        log.info("Agent's connection monitoring service start success!");
    }
    
    /**
     * Sending data to the server.
     *
     * @param value value
     * @throws GalaxyTracingException System Exception
     */
    @Override
    public void doPost(final String value) throws GalaxyTracingException {
        RequestBuilder reqBuilder = RequestBuilder.create(HttpMethodName.POST.toString())
                .setUri(url)
                .addHeader("Accept", ContentType.APPLICATION_JSON.toString())
                .addHeader("Content-type", ContentType.APPLICATION_JSON.toString());
        if (!StringUtils.isNullOrEmpty(value)) {
            reqBuilder.setEntity(new StringEntity(value, ContentType.APPLICATION_JSON));
        }
        log.info("Posting data {}", value);
        try {
            HttpResponse response = httpClient.execute(reqBuilder.build());
            String msg = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (log.isDebugEnabled()) {
                log.info("Response from GalaxyTracing server: {}", msg);
            }
            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            throw new GalaxyTracingException("An exception occurred in posting data, cause:%s", e.getLocalizedMessage(), e);
        }
    }
    
    /**
     * Shutdown the reporter server.
     */
    @Override
    public void shutdown() {
        idleConnectionMonitor.shutdown();
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
                log.error("Agent IdleConnectionMonitor got an exception :{}", ex.getLocalizedMessage());
            } finally {
                connectionManager.shutdown();
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
