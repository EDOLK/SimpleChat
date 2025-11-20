package com.simplechat;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.simplechat.rooms.Rooms;

@Controller
public class StaticPageController {

    @GetMapping("/room")
    public String goToRoom(@RequestParam(name = "id", required = true) String id, @CookieValue(name = "userCookie", required = true) String userCookie) throws Exception{
        Rooms.getRoomCache().getRoomById(id);
        return "room.html";
    }

}
