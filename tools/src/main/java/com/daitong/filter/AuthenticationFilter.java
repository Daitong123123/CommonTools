package com.daitong.filter;

import com.daitong.bo.common.UserInfo;
import com.daitong.manager.SessionManager;
import com.daitong.manager.UserManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {



    private static final String[] WHITELIST = {"/login", "/register", "/phone-binding"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        // 检查请求的 URL 是否在白名单中
        for (String url : WHITELIST) {
            if (requestURI.startsWith(url)) {
                chain.doFilter(request, response);
                return;
            }
        }
        // 获取请求中的 Cookie
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    if (SessionManager.isValidSession(sessionId)) {
                        chain.doFilter(request, response);
                        return;
                    }
//                    UserManager.setCurrentUser();
                    chain.doFilter(request, response);
                    return;
                }
            }
        }

        // 如果没有有效的 Cookie，返回未授权响应
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpResponse.getWriter().write("未授权");
    }
}
