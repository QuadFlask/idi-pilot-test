package com.flask.idi.user;

public class UserNotFoundException extends RuntimeException {
    Long id;

    public UserNotFoundException(Long id) {
        this.id = id;
    }
}
