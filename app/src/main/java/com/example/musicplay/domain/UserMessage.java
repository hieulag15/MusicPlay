package com.example.musicplay.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserMessage {
    @SerializedName("message")
    private String message;

    @SerializedName("userDTO")
    private User user;

    @SerializedName("userDTOS")
    private List<User> users;

    public UserMessage() {
    }

    public UserMessage(String message, User user, List<User> users) {
        this.message = message;
        this.user = user;
        this.users = users;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
