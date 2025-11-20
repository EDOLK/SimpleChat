package com.simplechat.rooms;

import java.util.ArrayList;
import java.util.List;

import com.simplechat.users.User;

import static com.simplechat.Application.generateRandomString;

public class InMemoryRoom implements Room{

    private final String roomId = generateRoomId();
    private final List<RoomMessage> messages = new ArrayList<>();
    private final String name;
    private final boolean roomPublic;
    private User owner;

    @Override
    public String getRoomId() {
        return roomId;
    }

    public InMemoryRoom(String name, boolean roomPublic) {
        this(name, roomPublic, null);
    }

    public InMemoryRoom(String name, boolean roomPublic, User owner) {
        this.name = name;
        this.roomPublic = roomPublic;
    }

    public InMemoryRoom() {
        this(generateRandomString(10), false);
    }

    @Override
    public List<RoomMessage> getMessages() {
        return messages;
    }

    @Override
    public boolean sendMessage(RoomMessage message){
        return messages.add(message);
    }

    private static String generateRoomId() {
        return generateRandomString(5);
    }

    @Override
    public boolean isPublic() {
        return roomPublic;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public User getOwner() {
        return owner;
    }
}
