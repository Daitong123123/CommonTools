package com.daitong.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-websocket")
                .setHandshakeHandler(new CustomHandshakeHandler())
                .setAllowedOrigins("*")
                .addInterceptors(new CustomHandshakeInterceptor(), new CustomOriginInterceptor());
    }

    private static class CustomHandshakeHandler implements HandshakeHandler {

        private final HandshakeHandler defaultHandler = new org.springframework.web.socket.server.support.DefaultHandshakeHandler();

        @Override
        public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
            if (request instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                HttpServletRequest httpRequest = servletRequest.getServletRequest();
                String userId = httpRequest.getParameter("userId");
                if (userId != null) {
                    attributes.put("userId", userId);
                }
            }
            return defaultHandler.doHandshake(request, response, wsHandler, attributes);
        }
    }

    private static class CustomHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            try {
                // 打印请求头信息用于调试
                org.springframework.http.HttpHeaders headers = request.getHeaders();
                for (Map.Entry<String, java.util.List<String>> entry : headers.entrySet()) {
                    System.out.println("Header: " + entry.getKey() + " - " + entry.getValue());
                }
                return true;
            } catch (Exception e) {
                System.err.println("Exception in beforeHandshake: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            // 可以在这里添加握手后的处理逻辑
        }
    }
}