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
import com.simplechat.rooms.Room;
import com.simplechat.rooms.Rooms;
import com.simplechat.users.User;

@Component
public class StompConnectListener {

    private static Map<String, SessionInfo> sessionMap = new HashMap<>();

    @EventListener
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof SessionConnectEvent sce) {
            Map<String, Object> sessionAttributes = SimpMessageHeaderAccessor.getSessionAttributes(sce.getMessage().getHeaders());
            String id = SimpAttributesContextHolder.currentAttributes().getSessionId();
            String userCookie = (String) sessionAttributes.get("userCookie");
            String roomId = (String) sessionAttributes.get("roomId");
            try {
                User user = Cookies.getUser(userCookie);
                Room room = Rooms.getRoomCache().getRoomById(roomId);
                sessionMap.put(id, new SessionInfo(user, room));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (event instanceof SessionDisconnectEvent){
            String id = SimpAttributesContextHolder.currentAttributes().getSessionId();
            sessionMap.remove(id);
            return;
        }
    }

    public static Optional<SessionInfo> getSession(String sessionId){
        if (sessionMap.containsKey(sessionId)) {
            return Optional.of(sessionMap.get(sessionId));
        }
        return Optional.empty();
    }
    
    public static class SessionInfo{
        private User user;
        private Room room;

        public SessionInfo(User user, Room room) {
            this.user = user;
            this.room = room;
        }

        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }
        public Room getRoom() {
            return room;
        }
        public void setRoom(Room room) {
            this.room = room;
        }
    }

}
