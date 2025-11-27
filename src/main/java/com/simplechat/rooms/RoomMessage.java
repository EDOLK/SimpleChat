package com.simplechat.rooms;

public class RoomMessage{

    private String username;

    private String content;

    private UserRole role;

    public RoomMessage(String username, String content) {
        this.username = username;
        this.content = content;
    }

    public RoomMessage(String username, String content, UserRole role) {
        this.username = username;
        this.content = content;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public static enum UserRole{
        OWNER, MOD, BOT,
    }

}

