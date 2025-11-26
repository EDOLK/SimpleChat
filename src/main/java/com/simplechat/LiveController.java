package com.simplechat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.simplechat.WebsocketSessionTracker.SessionInfo;
import com.simplechat.rooms.Room;
import com.simplechat.rooms.RoomMessage;
import com.simplechat.rooms.SendMessageRequest;
import com.simplechat.users.User;
import com.simplechat.users.Users;

@Controller
public class LiveController {

    @Autowired
    public final SimpMessagingTemplate messagingTemplate;

    public LiveController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/sendmessage")
    public void onMessage(SendMessageRequest message, @Header("simpSessionId") String sessionId){
        Optional<SessionInfo> infoOpt = WebsocketSessionTracker.getSession(sessionId);
        RoomMessage sentMessage = null;
        String roomId = null;
        if (infoOpt.isPresent()) {
            SessionInfo info = infoOpt.get();
            User user = info.getUser();
            Room room = info.getRoom();
            roomId = room.getRoomId();
            if (!room.isMuted(user)) {
                RoomMessage.UserRole role = null;
                if (room.isOwner(user)) {
                    role = RoomMessage.UserRole.OWNER;
                } else if (room.isMod(user)) {
                    role = RoomMessage.UserRole.MOD;
                }
                sentMessage = new RoomMessage(
                    user.getUsername(),
                    message.getMessage(),
                    role
                );
                room.sendMessage(sentMessage);
                doCommands(sentMessage.getContent(), room, user);
            }
        }
        if (roomId != null && sentMessage != null) {
            messagingTemplate.convertAndSend("/topic/chat/" + roomId, sentMessage);
            return;
        }
    }

    private void doCommands(String message, Room room, User commander){
        if (!room.isMod(commander) && !room.isOwner(commander)) {
            return;
        }
        for (String cmd : List.of("/ban ", "/unban ", "/mute ", "/unmute ", "/mod ", "/unmod ")) {
            Optional<String> cmdTargs = isolateCommandTargets(cmd, message);
            if (cmdTargs.isPresent()) {
                for (User user : toUsers(splitCommand(cmdTargs.get()))) {
                    if (room.isMod(commander) && (room.isMod(user) || room.isOwner(user)))
                        return;
                    if (commander.equals(user))
                        return;
                    switch (cmd) {
                        case "/ban ":
                            ApiController.ban(user, room);
                            break;
                        case "/unban ":
                            room.removeBannedUser(user);
                            break;
                        case "/mute ":
                            room.addMutedUser(user);
                            break;
                        case "/unmute ":
                            room.removeMutedUser(user);
                            break;
                        case "/mod ":
                            if (room.isOwner(commander)) {
                                room.addMod(user);
                            }
                            break;
                        case "/unmod ":
                            if (room.isOwner(commander)) {
                                room.removeMod(user);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private Optional<String> isolateCommandTargets(String prefix, String commandString){
        if (commandString.startsWith(prefix)) {
            return Optional.of(commandString.substring(prefix.length()));
        }
        return Optional.empty();
    }

    private String[] splitCommand(String string){
        return string.contains(", ") ? string.split(", ") : new String[]{string};
    }

    private Collection<User> toUsers(String[] strings){
        List<User> users = new ArrayList<>();
        for (String string : strings) {
            try {
                users.add(Users.getUserCache().getUser(string));
            } catch (Exception e) {
                continue;
            }
        }
        return users;
    }

}
