package com.simplechat.rooms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<Room> getPublicRooms() {
        return rooms.entrySet().stream()
            .map((e) -> e.getValue())
            .filter((r) -> r.isPublic())
            .collect(Collectors.toList());
    }

    @Override
    public Room makeRoom(RoomRequest request, User owner) throws RoomNotCreatedException {
        if (request.getRoomName().isBlank()) {
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

    


}
