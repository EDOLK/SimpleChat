package com.simplechat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.simplechat.cookies.Cookies;
import com.simplechat.users.User;

@Component
public class StompConnectListener {

    private static Map<String, User> sessionToUserMap = new HashMap<>();

    @EventListener
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionConnectEvent sce) {
            Map<String, Object> sessionAttributes = SimpMessageHeaderAccessor.getSessionAttributes(sce.getMessage().getHeaders());
            String id = SimpAttributesContextHolder.currentAttributes().getSessionId();
            String userCookie = (String) sessionAttributes.get("userCookie");
            try {
                User user = Cookies.getUser(userCookie);
                sessionToUserMap.put(id, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (event instanceof SessionDisconnectEvent){
            String id = SimpAttributesContextHolder.currentAttributes().getSessionId();
            sessionToUserMap.remove(id);
            return;
        }
    }

    public static Optional<User> getUserForSession(String sessionId){
        if (sessionToUserMap.containsKey(sessionId)) {
            return Optional.of(sessionToUserMap.get(sessionId));
        }
        return Optional.empty();
    }

    
}
