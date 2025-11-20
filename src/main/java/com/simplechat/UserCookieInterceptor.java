package com.simplechat;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.simplechat.cookies.Cookies;
import com.simplechat.cookies.Cookies.InvalidCookieException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class UserCookieInterceptor implements HandshakeInterceptor {

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {}

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        HttpServletRequest httpReq = servletRequest.getServletRequest();
        Cookie[] cookies = httpReq.getCookies();
        if (cookies == null) {
            return false;
        }

        String userCookie = null;
        for (Cookie c : cookies) {
            if ("userCookie".equals(c.getName())) {
                userCookie = c.getValue();
                break;
            }
        }

        if (userCookie == null) {
            return false; // deny connection
        } 

        try {
            Cookies.getUser(userCookie);
        } catch (InvalidCookieException e) {
            return false;
        }

        attributes.put("userCookie", userCookie);

        return true;
    }

}
