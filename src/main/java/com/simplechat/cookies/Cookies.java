package com.simplechat.cookies;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.simplechat.Application;
import com.simplechat.users.User;

public class Cookies {

    private static Map<String, User> cookieMap = new HashMap<>();
    public static final Duration COOKIE_LIFETIME = Duration.ofHours(1);

    public static String getCookie(User user) throws OvenBrokenException{
        Optional<String> cOpt = cookieMap.entrySet().stream()
            .filter((e) -> e.getValue().equals(user))
            .map((e) -> e.getKey())
            .findFirst();
        if (cOpt.isPresent()) {
            return cOpt.get();
        }
        return generateCookie(user);
    }

    public static void deleteCookie(String cookie){
        cookieMap.remove(cookie);
    }

    public static String generateCookie(User user) throws OvenBrokenException {
        String cookieString = Application.generateRandomString(7);
        int tries = 0;
        while (cookieMap.containsKey(cookieString) && tries < 10) {
            cookieString = Application.generateRandomString(7);
            tries++;
        }
        if (!cookieMap.containsKey(cookieString)) {
            cookieMap.put(cookieString, user);
            return cookieString;
        }
        throw new OvenBrokenException();
    }

    public static User getUser(String cookie) throws InvalidCookieException{
        if (cookieMap.containsKey(cookie)) {
            return cookieMap.get(cookie);
        }
        throw new InvalidCookieException();
    }

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="User cookie is invalid.")
    public static class InvalidCookieException extends Exception{};

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="User has no active cookie.")
    public static class NoCookieException extends Exception{};

    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Cookie could not be created. Oven is broken!")
    public static class OvenBrokenException extends Exception{};

}
