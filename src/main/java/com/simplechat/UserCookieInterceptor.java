package com.simplechat;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.simplechat.cookies.Cookies;
import com.simplechat.rooms.Room;
import com.simplechat.rooms.Rooms;
import com.simplechat.users.User;

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
        if (cookies == null)
            return false;

        String userCookie = null;
        String roomId = null;
        for (Cookie c : cookies) {
            if ("userCookie".equals(c.getName()))
                userCookie = c.getValue();

            if ("roomId".equals(c.getName()))
                roomId = c.getValue();

            if (userCookie != null && roomId != null)
                break;

        }

        if (userCookie == null || roomId == null) {
            return false; // deny connection
        } 

        User user = null;
        Room room = null;

        try {
            user = Cookies.getUser(userCookie);
            room = Rooms.getRoomCache().getRoomById(roomId);
        } catch (Exception e) {
            return false;
        }

        if (room.isBanned(user)) {
            return false;
        }

        attributes.put("userCookie", userCookie);
        attributes.put("roomId", roomId);

        return true;
    }

}
