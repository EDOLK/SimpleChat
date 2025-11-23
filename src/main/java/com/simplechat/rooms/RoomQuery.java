package com.simplechat.rooms;

import com.simplechat.users.User;

public class RoomQuery {
    private boolean publicRooms;
    private boolean privateRooms;
    private String name;
    private User user;
    private RoomQuery(){};
    public boolean isPrivateRooms() {
        return privateRooms;
    }
    public boolean isPublicRooms() {
        return publicRooms;
    }
    public String getName() {
        return name;
    }
    public User getUser() {
        return user;
    }

    public static class Builder {
        private RoomQuery query = new RoomQuery();
        public Builder(){}
        public Builder withPublicRooms(boolean publicRooms) {
            this.query.publicRooms = publicRooms;
            return this;
        }
        public Builder withPrivateRooms(boolean privateRooms) {
            this.query.privateRooms = privateRooms;
            return this;
        }
        public Builder withName(String name) {
            this.query.name = name;
            return this;
        }
        public Builder withUser(User user) {
            this.query.user = user;
            return this;
        }
        public RoomQuery build(){
            return this.query;
        };
    }
}

