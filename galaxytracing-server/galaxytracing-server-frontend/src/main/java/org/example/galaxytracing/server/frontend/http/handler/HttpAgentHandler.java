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

package org.example.galaxytracing.server.frontend.http.handler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.galaxytracing.infra.common.constant.GalaxyTracingServerMessage;
import org.example.galaxytracing.infra.common.thread.ExecutorServiceManager;

/**
 * HTTP handler of Agent.
 *
 * @author JiekerTime
 */
@Slf4j(topic = "server")
public final class HttpAgentHandler extends ChannelInboundHandlerAdapter {
    
    private static final String DEFAULT_URI_PATH = "/collector";
    
    private static final String THREAD_NAME_FORMAT = "Collector-%d";
    
    private static final ExecutorServiceManager EXECUTOR_SERVICE_MANAGER;
    
    static {
        EXECUTOR_SERVICE_MANAGER = new ExecutorServiceManager(10, THREAD_NAME_FORMAT);
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        EXECUTOR_SERVICE_MANAGER.getExecutorService().execute(() -> {
            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            
            if (DEFAULT_URI_PATH.equals(httpRequest.uri()) && HttpMethod.POST.equals(httpRequest.method())) {
                String data = httpRequest.content().toString(CharsetUtil.UTF_8);
                log.info("Received data :{}", data);
                send(GalaxyTracingServerMessage.RESPONSE_OK, ctx, HttpResponseStatus.OK);
            } else {
                send(GalaxyTracingServerMessage.WRONG_REQUEST_ERROR, ctx, HttpResponseStatus.BAD_REQUEST);
            }
        });
    }
    
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        log.info("Address of the connected client:" + ctx.channel().remoteAddress());
    }
    
    private void send(final String content, final ChannelHandlerContext ctx, final HttpResponseStatus status) {
        FullHttpResponse response =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                        Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        
    }
}
