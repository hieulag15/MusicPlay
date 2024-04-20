package com.example.musicplay;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.musicplay.domain.MusicServiceListener;
import com.example.musicplay.domain.Song;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private ExecutorService executorService;
    private int position;
    private List<Song> songs;

    public static final String BROADCAST_ACTION = "com.example.musicplay.seekbarupdate";

    private final Handler handler = new Handler();
    private Intent seekbarIntent;

    private MusicServiceListener listener;

    public void setListener(MusicServiceListener listener) {
        this.listener = listener;
    }



    private final IBinder binder = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        executorService = Executors.newSingleThreadExecutor();
        seekbarIntent = new Intent(BROADCAST_ACTION);
        updateSeekBar();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        String actionName = intent.getStringExtra("actionName");
        if (actionName != null) {
            switch (actionName) {
                case "playPause":
                    if (PlayerActivity.isPlaying) {
                        PlayerActivity.isPlaying = false;
                        mediaPlayer.pause();
                    } else {
                        PlayerActivity.isPlaying = true;
                        mediaPlayer.start();
                    }
                    break;
                case "next":
                    break;
                case "prev":
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void playSong(String songLink) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        executorService.execute(() -> {
            try {
                mediaPlayer.setDataSource(songLink);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public void playNextSong() {
        if (position < songs.size() - 1) {
            mediaPlayer.stop();
            position++;
        } else {
            mediaPlayer.stop();
            position = 0;
        }
        playSong(songs.get(position).getLink());
    }

    public void playPreSong() {
        if (position > 0) {
            mediaPlayer.stop();
            position--;
        } else {
            mediaPlayer.stop();
            position = songs.size() - 1;
        }
        playSong(songs.get(position).getLink());
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void updateSeekBar() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    // Broadcast the current position
                    Intent intent = new Intent(BROADCAST_ACTION);
                    intent.putExtra("currentPosition", currentPosition);
                    sendBroadcast(intent);
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public String formatTime(int timeInMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(timeInMillis);
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    public void start() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
}
