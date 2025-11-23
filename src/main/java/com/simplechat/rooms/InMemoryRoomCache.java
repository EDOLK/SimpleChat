package com.simplechat.rooms;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.simplechat.users.User;

public class InMemoryRoomCache implements RoomCache {

    private Map<String, Room> rooms = new HashMap<>();

    @Override
    public Room getRoomById(String id) throws RoomNotFoundException {
        if (rooms.containsKey(id)) {
            return rooms.get(id);
        }
        throw new RoomNotFoundException();
    }

    @Override
    public Room makeRoom(RoomRequest request, User owner) throws RoomNotCreatedException {
        if (request.getRoomName().isBlank() || rooms.values().stream()
                .map((r) -> r.getName())
                .anyMatch((n) -> request.getRoomName().equals(n))) {
            throw new RoomNotCreatedException();
        }
        Room r = new InMemoryRoom(request.getRoomName(), request.isRoomPublic(), owner);
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
    public Collection<Room> getRooms(RoomQuery query) {
        return rooms.values().stream()
            .filter(r -> query.getUser() != null ? r.getOwner() == query.getUser() : true)
            .filter(r -> query.getName() != null ? r.getName().equals(query.getName()) : true)
            .filter(r -> r.isPublic() == query.isPublicRoom())
            .collect(Collectors.toList());
    }

}
