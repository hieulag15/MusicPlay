package com.example.musicplay.domain;

public interface MusicServiceListener {
    void onProgressChanged(int progress);
    void onStartTrackingTouch();
    void onStopTrackingTouch();
    void onCompletion();
    void onSongChanged(Song song);
}
