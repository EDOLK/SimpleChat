package com.simplechat.rooms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.simplechat.users.User;

import static com.simplechat.Application.generateRandomString;

public class InMemoryRoom implements Room{

    private final String roomId = generateRoomId();
    private final List<RoomMessage> messages = new ArrayList<>();
    private final String name;
    private final boolean roomPublic;
    private final Set<User> bannedUsers = new HashSet<>();
    private final Set<User> mutedUsers = new HashSet<>();
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
        this.owner = owner;
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

    @Override
    public Collection<User> getBannedUsers() {
        return bannedUsers;
    }

    @Override
    public Collection<User> getMutedUsers() {
        return mutedUsers;
    }

    @Override
    public boolean ban(User user) {
        return bannedUsers.add(user);
    }

    @Override
    public boolean mute(User user) {
        return mutedUsers.add(user);
    }

    @Override
    public boolean unBan(User user) {
        return bannedUsers.remove(user);
    }

    @Override
    public boolean unMute(User user) {
        return mutedUsers.remove(user);
    }
}
