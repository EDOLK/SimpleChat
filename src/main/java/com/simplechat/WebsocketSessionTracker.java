package com.simplechat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.simplechat.cookies.Cookies;
import com.simplechat.rooms.Room;
import com.simplechat.rooms.Rooms;
import com.simplechat.users.User;

@Component
public class WebsocketSessionTracker extends WebSocketHandlerDecorator {

    private final static Map<String, SessionInfo> sessionMap = new HashMap<>();
    private final static Map<String, WebSocketSession> standingSessions = new HashMap<>();

    public WebsocketSessionTracker(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, Object> sessionAttributes = session.getAttributes();
        String id = session.getId();
        sessionAttributes.put("webSocketId", id);
        standingSessions.put(id, session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionMap.remove(session.getId());
        super.afterConnectionClosed(session, status);
    }

    @EventListener
    public void onConnect(SessionConnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String stompId = accessor.getSessionId();
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        String userCookie = (String) sessionAttributes.get("userCookie");
        String roomId = (String) sessionAttributes.get("roomId");
        String webSocketId = (String) sessionAttributes.get("webSocketId");
        WebSocketSession webSocketSession = standingSessions.remove(webSocketId);
        try {
            User user = Cookies.getUser(userCookie);
            Room room = Rooms.getRoomCache().getRoomById(roomId);
            sessionMap.put(stompId, new SessionInfo(user, room, webSocketSession));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String stompId = accessor.getSessionId();
        sessionMap.remove(stompId);
    }

    public static Optional<SessionInfo> getSession(String sessionId){
        if (sessionMap.containsKey(sessionId)) {
            return Optional.of(sessionMap.get(sessionId));
        }
        return Optional.empty();
    }

    public static Collection<SessionInfo> getSessions(SessionRequest request){
        return sessionMap.values().stream()
            .filter((si) -> request.getUser() != null ? si.getUser().equals(request.getUser()) : true)
            .filter((si) -> request.getRoom() != null ? si.getRoom().equals(request.getRoom()) : true)
            .collect(Collectors.toList());
    }

    public static class SessionRequest{
        private User user;
        private Room room;
        public SessionRequest(User user, Room room) {
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

    public static class SessionInfo{
        private User user;
        private Room room;
        private WebSocketSession session;

        public SessionInfo(User user, Room room, WebSocketSession session) {
            this.user = user;
            this.room = room;
            this.session = session;
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
        public WebSocketSession getSession() {
            return session;
        }
        public void setSession(WebSocketSession session) {
            this.session = session;
        }

    }
}
