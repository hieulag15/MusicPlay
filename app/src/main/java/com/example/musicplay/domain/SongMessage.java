package com.example.musicplay.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SongMessage {
    @SerializedName("message")
    private String message;

    @SerializedName("songs")
    private List<Song> songs;

    @SerializedName("song")
    private Song song;

    public SongMessage() {
    }

    public SongMessage(String message, List<Song> songs, Song song) {
        this.message = message;
        this.songs = songs;
        this.song = song;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
