package com.example.musicplay.domain;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Favourite implements Serializable {

    @SerializedName("id")
    private long id;

    @SerializedName("song")
    private Song song;

    @SerializedName("user")
    private User user;

    public Favourite() {
    }

    public Favourite(long id, Song song, User user) {
        this.id = id;
        this.song = song;
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
