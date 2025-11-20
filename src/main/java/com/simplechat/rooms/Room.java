package com.simplechat.rooms;

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
}
