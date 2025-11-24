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
    default boolean isBanned(User user){
        return getBannedUsers().contains(user);
    };
    default boolean isMuted(User user){
        return getMutedUsers().contains(user);
    };
    public boolean ban(User user);
    public boolean mute(User user);
    public boolean unBan(User user);
    public boolean unMute(User user);
}
