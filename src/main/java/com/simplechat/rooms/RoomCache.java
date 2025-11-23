package com.simplechat.rooms;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.simplechat.users.User;

public interface RoomCache {
    public Collection<Room> getRooms(RoomQuery query);
    public Room getRoomById(String id) throws RoomNotFoundException;
    public Room makeRoom(RoomRequest request, User owner) throws RoomNotCreatedException;
    public void deleteRoom(Room room) throws RoomNotDeletedException;

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Room not found.")
    public static class RoomNotFoundException extends Exception{};

    @ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE, reason="Room could not be created.")
    public static class RoomNotCreatedException extends Exception{};

    @ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE, reason="Room could not be deleted.")
    public static class RoomNotDeletedException extends Exception{};

}
