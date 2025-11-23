package com.simplechat;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.simplechat.cookies.Cookies;
import com.simplechat.rooms.Room;
import com.simplechat.rooms.RoomMessage;
import com.simplechat.rooms.RoomQuery;
import com.simplechat.rooms.RoomRequest;
import com.simplechat.rooms.Rooms;
import com.simplechat.users.User;
import com.simplechat.users.UserRequest;
import com.simplechat.users.Users;

@RestController
public class ApiController {

    @Autowired
    private TaskScheduler taskScheduler;
    
    private Map<String, ScheduledFuture<?>> taskMap= new HashMap<>();

    @PostMapping("/api/rooms")
    public Room getNewRoom(@RequestBody RoomRequest request, @CookieValue(name = "userCookie", required = true) String userCookie) throws Exception{
        User user = Cookies.getUser(userCookie);
        Room room = Rooms.getRoomCache().makeRoom(request, user);
        room.sendMessage(new RoomMessage("John Computer", "Your room id is: " + room.getRoomId()));
        return room;
    }

    @RequestMapping(value = "/api/rooms/{roomId}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkForRoom(@PathVariable String roomId) throws Exception {
        Rooms.getRoomCache().getRoomById(roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/rooms/{roomId}")
    public Room getRoom(@PathVariable String roomId, @CookieValue(name = "userCookie", required = true) String userCookie) throws Exception{
        return Rooms.getRoomCache().getRoomById(roomId);
    }

    @GetMapping("/api/rooms/{roomId}/messages")
    public List<RoomMessage> getMessages(@PathVariable String roomId, @CookieValue(name = "userCookie", required = true) String userCookie) throws Exception{
        Cookies.getUser(userCookie);
        return Rooms.getRoomCache().getRoomById(roomId).getMessages();
    }

    @DeleteMapping("/api/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String roomId, @CookieValue(name = "userCookie", required = true) String userCookie) throws Exception{
        User user = Cookies.getUser(userCookie);
        Room room = Rooms.getRoomCache().getRoomById(roomId);
        if (room.getOwner() == user) {
            Rooms.getRoomCache().deleteRoom(room);
            return ResponseEntity.ok().build();
        }
        throw new InvalidRoomPermissionException();
    }

    @ResponseStatus(value=HttpStatus.UNAUTHORIZED, reason="You dont have permissions for this room.")
    private static class InvalidRoomPermissionException extends Exception{}

    @GetMapping("/api/rooms")
    public Collection<Room> getRooms(
        @RequestParam(name = "public", required = false, defaultValue = "true") boolean publicRoom,
        @RequestParam(name = "name", required = false) String roomName,
        @RequestParam(name = "own", required = false) boolean ownRooms,
        @CookieValue(name = "userCookie", required = false) String userCookie
    ) throws Exception{
        User requester = null;
        if (!publicRoom) {
            if (userCookie == null)
                throw new Cookies.NoCookieException();
            requester = Cookies.getUser(userCookie);
        }
        RoomQuery roomQuery = new RoomQuery.Builder()
            .withPublicRoom(publicRoom)
            .withName(roomName)
            .withUser(requester)
            .build();
        return Rooms.getRoomCache().getRooms(roomQuery);
    }

    @PostMapping("/api/user/login")
    public ResponseEntity<Void> getCookieForUser(@RequestBody UserRequest request) throws Exception{
        User user = Users.getUserCache().authenticate(request.getUsername(), request.getPassword());
        String cookieString = Cookies.getCookie(user);
        ResponseCookie userCookie = ResponseCookie.from("userCookie", cookieString)
            .httpOnly(true)
            .maxAge(Cookies.COOKIE_LIFETIME)
            .path("/")
            .build();
        taskMap.put(cookieString, taskScheduler.schedule(() -> {
            Cookies.deleteCookie(cookieString);
        }, Instant.now().plus(Cookies.COOKIE_LIFETIME)));
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, userCookie.toString())
            .build();
    }

    @GetMapping("/api/user")
    public User getUser(
        @CookieValue(name = "userCookie", required = true) String userCookie
    )throws Exception{
        return Cookies.getUser(userCookie);
    }

    @RequestMapping(value = "/api/user/refresh", method = RequestMethod.HEAD)
    public ResponseEntity<Void> refreshUserCookie(@CookieValue(name = "userCookie", required = true) String incomingUserCookie) throws Exception{
        Cookies.getUser(incomingUserCookie);
        ResponseCookie userCookie = ResponseCookie.from("userCookie", incomingUserCookie)
            .httpOnly(true)
            .maxAge(Cookies.COOKIE_LIFETIME)
            .path("/")
            .build();
        if (taskMap.containsKey(incomingUserCookie)) {
            taskMap.remove(incomingUserCookie).cancel(false);
        }
        taskMap.put(incomingUserCookie, taskScheduler.schedule(() -> {
            Cookies.deleteCookie(incomingUserCookie);
        }, Instant.now().plus(Cookies.COOKIE_LIFETIME)));
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, userCookie.toString()).build();
    }


    @PostMapping("/api/user/logout")
    public ResponseEntity<Void> logoutUser(@CookieValue(name = "userCookie", required = false) String incomingUserCookie){
        if (incomingUserCookie != null)
            Cookies.deleteCookie(incomingUserCookie);
        ResponseCookie userCookie = ResponseCookie.from("userCookie", null)
            .httpOnly(true)
            .maxAge(0)
            .path("/")
            .build();
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, userCookie.toString())
            .build();
    }

    @PostMapping("/api/user/register")
    public ResponseEntity<Void> registerNewUser(@RequestBody UserRequest request) throws Exception{
        Users.getUserCache().signUpNewUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok().build();
    }


}
