package com.simplechat.rooms;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.simplechat.StompConnectListener;
import com.simplechat.users.User;

public class Rooms {

    private static RoomCache roomCache = new InMemoryRoomCache();

    private Rooms() {}

    public static RoomCache getRoomCache() {
        return roomCache;
    }

    public static void setRoomCache(RoomCache roomCache) {
        Rooms.roomCache = roomCache;
    }

    public static RoomMessage sendMessage(SendMessageRequest request, String sessionId) throws Exception{
        Optional<User> user = StompConnectListener.getUserForSession(sessionId);
        if (user.isPresent()) {
            return sendMessage(request, user.get());
        }
        throw new MessageNotSentException();
    }

    public static RoomMessage sendMessage(SendMessageRequest request, User user) throws Exception{
        Room room = roomCache.getRoomById(request.getRoomId());
        RoomMessage message = new RoomMessage(user.getUsername(), request.getMessage());
        if (room.sendMessage(message)) {
            return message;
        }
        throw new MessageNotSentException();
    }

    @ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE, reason="Message could not be sent.")
    public static class MessageNotSentException extends Exception{};

}
