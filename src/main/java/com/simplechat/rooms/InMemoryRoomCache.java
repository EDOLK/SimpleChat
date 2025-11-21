package com.simplechat.rooms;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.simplechat.users.User;

public class InMemoryRoomCache implements RoomCache {

    private Map<String, InMemoryRoom> rooms = new HashMap<>();

    @Override
    public Room getRoomById(String id) throws RoomNotFoundException {
        if (rooms.containsKey(id)) {
            return rooms.get(id);
        }
        throw new RoomNotFoundException();
    }

    @Override
    public Room getRoomByName(String name) throws RoomNotFoundException{
        Optional<Room> ropt = rooms.entrySet().stream()
            .map(e -> (Room)e.getValue())
            .filter(r -> r.getName().equals(name))
            .findFirst();
        if (ropt.isPresent()) {
            return ropt.get();
        }
        throw new RoomNotFoundException();
    }

    @Override
    public Stream<Room> getPublicRooms() {
        return rooms.entrySet().stream()
            .map((e) -> (Room)e.getValue())
            .filter((r) -> r.isPublic());
    }

    @Override
    public Room makeRoom(RoomRequest request, User owner) throws RoomNotCreatedException {
        if (request.getRoomName().isBlank() || rooms.values().stream()
                .map((r) -> r.getName())
                .anyMatch((n) -> request.getRoomName().equals(n))) {
            throw new RoomNotCreatedException();
        }
        InMemoryRoom r = new InMemoryRoom(request.getRoomName(), request.isRoomPublic(), owner);
        int tries = 1;
        while (rooms.containsKey(r.getRoomId()) && tries <= 10) {
            r = new InMemoryRoom(request.getRoomName(), request.isRoomPublic(), owner);
            tries++;
        }
        if (rooms.containsKey(r.getRoomId())) {
            throw new RoomNotCreatedException();
        }
        rooms.put(r.getRoomId(), r);
        return r;
    }

    @Override
    public void deleteRoom(Room room) throws RoomNotDeletedException {
        if (rooms.remove(room.getRoomId()) == null) {
            throw new RoomNotDeletedException();
        }
    }

    @Override
    public Stream<Room> getRoomsByOwner(User user) {
        return rooms.values().stream()
            .filter((r) -> r.getOwner() == user)
            .map(r -> (Room)r);
    }

}
