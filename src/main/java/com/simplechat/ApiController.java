package com.simplechat;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.simplechat.cookies.Cookies;
import com.simplechat.cookies.Cookies.InvalidCookieException;
import com.simplechat.rooms.Room;
import com.simplechat.rooms.RoomCache.RoomNotFoundException;
import com.simplechat.rooms.RoomMessage;
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

    @PostMapping("/api/newroom")
    public String getNewRoom(@RequestBody RoomRequest request, @CookieValue(name = "userCookie", required = true) String userCookie) throws Exception{
        User checkCookie = checkCookie(userCookie);
        Room room = Rooms.getRoomCache().makeRoom(request, checkCookie);
        room.sendMessage(new RoomMessage("John Computer", "Your room id is: " + room.getRoomId()));
        return room.getRoomId();
    }

    @GetMapping("/api/checkroom")
    public boolean checkForRoom(@RequestParam(name = "id", required = true) String id) {
        try {
            Rooms.getRoomCache().getRoomById(id);
        } catch (RoomNotFoundException e) {
            return false;
        }
        return true;
    }

    @GetMapping("/api/messages")
    public List<RoomMessage> getMessages(@RequestParam(name = "id", required = true) String id, @CookieValue(name = "userCookie", required = true) String userCookie) throws Exception{
        checkCookie(userCookie);
        return Rooms.getRoomCache().getRoomById(id).getMessages();
    }

    @GetMapping("/api/publicrooms")
    public List<String> getPublicRooms() throws Exception{
        return Rooms.getRoomCache().getPublicRooms().stream()
            .map(r -> r.getName())
            .collect(Collectors.toList());
    }

    @GetMapping("/api/roomnametoid")
    public String getIdFromName(@RequestParam(name = "name", required = true) String name, @CookieValue(name = "userCookie", required = true) String userCookie) throws Exception{
        checkCookie(userCookie);
        Room room = Rooms.getRoomCache().getRoomByName(name);
        if (room.isPublic()) {
            return room.getRoomId();
        }
        throw new RoomNotFoundException();
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

    @GetMapping("/api/user/check")
    public ResponseEntity<String> checkForUser(@CookieValue(name = "userCookie", required = true) String incomingUserCookie) throws Exception{
        return ResponseEntity.ok()
            .body(Cookies.getUser(incomingUserCookie).getUsername());
    }

    @GetMapping("/api/user/refresh")
    public ResponseEntity<Void> refreshUserCookie(@CookieValue(name = "userCookie", required = true) String incomingUserCookie) throws Exception{
        checkCookie(incomingUserCookie);
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

    private static User checkCookie(String userCookie) throws InvalidCookieException{
        if (userCookie != null) {
            return Cookies.getUser(userCookie);
        }
        throw new InvalidCookieException();
    }


}
