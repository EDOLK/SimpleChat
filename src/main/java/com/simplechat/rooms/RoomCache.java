package com.simplechat.rooms;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.simplechat.users.User;

public interface RoomCache {
    public Room getRoomById(String id) throws RoomNotFoundException;
    public Room getRoomByName(String name) throws RoomNotFoundException;
    public List<Room> getPublicRooms();
    public Room makeRoom(RoomRequest request, User owner) throws RoomNotCreatedException;

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Room not found.")
    public static class RoomNotFoundException extends Exception{};

    @ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE, reason="Room could not be created.")
    public static class RoomNotCreatedException extends Exception{};

}
