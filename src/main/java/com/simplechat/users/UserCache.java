package com.simplechat.users;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface UserCache {

    public User authenticate(String username, String password) throws UserNotFoundException, InvalidCredentialsException ;

    public User signUpNewUser(String username, String password) throws UserAlreadyExistsException, BadUserOrPassException;

    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="User not found.")
    public static class UserNotFoundException extends Exception{};

    @ResponseStatus(value=HttpStatus.UNAUTHORIZED, reason="Invalid Credentials")
    public static class InvalidCredentialsException extends Exception{};

    @ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE, reason="User could not be registered.")
    public static class UserNotRegisteredException extends Exception{};

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="User already exists.")
    public static class UserAlreadyExistsException extends Exception{};

    @ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Invalid username/password.")
    public static class BadUserOrPassException extends Exception{};

}

