package com.simplechat.users;

import java.util.HashMap;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class InMemoryUserCache implements UserCache {

    private HashMap<String, User> users = new HashMap<>();

    private PasswordEncoder passEncoder = new BCryptPasswordEncoder();

    @Override
    public User authenticate(String username, String password) throws UserNotFoundException, InvalidCredentialsException {
        if (users.containsKey(username)){
            User user = users.get(username);
            if (passEncoder.matches(password, user.getPassword())){
                return user;
            }
            throw new InvalidCredentialsException();
        }
        throw new UserNotFoundException();
    }

    @Override
    public User signUpNewUser(String username, String password) throws UserAlreadyExistsException, BadUserOrPassException{
        if (username.isBlank() || password.isBlank()) {
            throw new BadUserOrPassException();
        }
        if (!users.containsKey(username)) {
            InMemoryUser user = new InMemoryUser(username, passEncoder.encode(password));
            users.put(username, user);
            return user;
        }
        throw new UserAlreadyExistsException();
    }

}
