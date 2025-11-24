package com.simplechat.rooms;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class Rooms {

    private static RoomCache roomCache = new InMemoryRoomCache();

    private Rooms() {}

    public static RoomCache getRoomCache() {
        return roomCache;
    }

    public static void setRoomCache(RoomCache roomCache) {
        Rooms.roomCache = roomCache;
    }

    @ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE, reason="Message could not be sent.")
    public static class MessageNotSentException extends Exception{};

}
