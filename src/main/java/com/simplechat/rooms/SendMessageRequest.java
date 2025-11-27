package com.simplechat.rooms;

public class SendMessageRequest {

    private String message;

    public SendMessageRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
