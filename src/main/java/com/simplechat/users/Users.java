package com.simplechat.users;

public class Users {

    private static UserCache userCache = new InMemoryUserCache();

    private Users() {}

    public static UserCache getUserCache() {
        return userCache;
    }

    public static void setUserCache(UserCache userCache) {
        Users.userCache = userCache;
    }

}
