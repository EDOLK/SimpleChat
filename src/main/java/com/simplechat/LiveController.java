package com.simplechat;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.simplechat.StompConnectListener.SessionInfo;
import com.simplechat.rooms.Room;
import com.simplechat.rooms.RoomMessage;
import com.simplechat.rooms.Rooms.MessageNotSentException;
import com.simplechat.rooms.SendMessageRequest;
import com.simplechat.users.User;

@Controller
public class LiveController {

    @Autowired
    public final SimpMessagingTemplate messagingTemplate;

    public LiveController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/sendmessage")
    public void onMessage(SendMessageRequest message, @Header("simpSessionId") String sessionId) throws Exception{
        Optional<SessionInfo> infoOpt = StompConnectListener.getSession(sessionId);
        RoomMessage sentMessage = null;
        String roomId = null;
        if (infoOpt.isPresent()) {
            SessionInfo info = infoOpt.get();
            User user = info.getUser();
            Room room = info.getRoom();
            roomId = room.getRoomId();
            if (!room.isMuted(user)) {
                sentMessage = new RoomMessage(user.getUsername(), message.getMessage());
                if(!room.sendMessage(sentMessage)){
                    throw new MessageNotSentException();
                }
            }
        }
        if (roomId != null && sentMessage != null) {
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, sentMessage);
        }
        throw new MessageNotSentException();
    }

}
