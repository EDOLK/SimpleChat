package com.simplechat.rooms;

import com.simplechat.users.User;

public class RoomQuery {
    private boolean publicRoom;
    private String name;
    private User user;
    private RoomQuery(){};
    public boolean isPublicRoom() {
        return publicRoom;
    }
    public String getName() {
        return name;
    }
    public User getUser() {
        return user;
    }

    public static class Builder {
        private RoomQuery query = new RoomQuery();
        public Builder(){

        }
        public Builder withPublicRoom(boolean publicRoom) {
            this.query.publicRoom = publicRoom;
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

