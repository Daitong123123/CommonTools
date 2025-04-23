package com.daitong.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomOriginInterceptor extends OriginHandshakeInterceptor {

    private static final List<String> ALLOWED_ORIGINS = Arrays.asList("http://localhost:3000","http://47.108.130.95","http://dinner.daitong.xyz:3000");

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // 握手后的处理逻辑
    }
}
