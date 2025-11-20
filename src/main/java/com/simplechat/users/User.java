package com.simplechat.users;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface User {
    public String getUsername();
    @JsonIgnore
    public String getPassword();
}
