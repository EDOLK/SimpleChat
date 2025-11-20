package com.simplechat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.simplechat.rooms.RoomMessage;
import com.simplechat.rooms.Rooms;
import com.simplechat.rooms.SendMessageRequest;

@Controller
public class LiveController {

    @Autowired
    public final SimpMessagingTemplate messagingTemplate;

    public LiveController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/sendmessage")
    public void onMessage(SendMessageRequest message, @Header("simpSessionId") String sessionId) throws Exception{
        try {
            RoomMessage sentMessage = Rooms.sendMessage(message, sessionId);
            messagingTemplate.convertAndSend("/topic/chat/" + message.getRoomId(), sentMessage);
        } catch (Exception e) {
            throw e;
        }
    }

}
