package com.simplechat.rooms;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simplechat.users.User;

public interface Room {
    public String getRoomId();
    @JsonIgnore
    public List<RoomMessage> getMessages();
    public boolean sendMessage(RoomMessage message);
    public boolean isPublic();
    public String getName();
    public User getOwner();
    @JsonIgnore
    public Collection<User> getBannedUsers();
    @JsonIgnore
    public Collection<User> getMutedUsers();
    @JsonIgnore
    public Collection<User> getMods();
    default boolean isBanned(User user){
        return getBannedUsers().contains(user);
    };
    default boolean isMuted(User user){
        return getMutedUsers().contains(user);
    };
    default boolean isMod(User user){
        return getMods().contains(user);
    }
    default boolean isOwner(User user){
        return getOwner().equals(user);
    }
    public boolean addBannedUser(User user);
    public boolean addMutedUser(User user);
    public boolean addMod(User user);
    public boolean removeMod(User user);
    public boolean removeBannedUser(User user);
    public boolean removeMutedUser(User user);
}
