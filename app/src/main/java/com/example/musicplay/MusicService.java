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
    // Để sử dụng MediaPlayer và xử lý âm nhạc
    private MediaPlayer mediaPlayer;

    // ExecutorService để thực thi các tác vụ nền
    private ExecutorService executorService;

    // Vị trí hiện tại của bài hát đang phát
    private int position;

    // Danh sách các bài hát
    private List<Song> songs;

    // Action để cập nhật thanh seekbar
    public static final String BROADCAST_ACTION = "com.example.musicplay.seekbarupdate";

    // Intent để gửi thông điệp cập nhật seekbar
    private Intent seekbarIntent;

    // Listener để lắng nghe sự kiện của MusicService
    private MusicServiceListener listener;

    // Phương thức để thiết lập listener
    public void setListener(MusicServiceListener listener) {
        this.listener = listener;
    }

    // Cờ cho việc lặp lại bài hát
    private boolean isRepeat = false;

    // Cờ cho việc phát ngẫu nhiên bài hát
    private boolean isShuffle = false;

    private final IBinder binder = new MusicBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        executorService = Executors.newSingleThreadExecutor();
        seekbarIntent = new Intent(BROADCAST_ACTION);
        updateSeekBar();
        setSongs(songs);
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
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
        if (mediaPlayer.isPlaying() && mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        executorService.execute(() -> {
            try {
                mediaPlayer.setDataSource(songLink);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (isRepeat) {
                            mediaPlayer.seekTo(0);
                            mediaPlayer.start();
                        } else {
                            if (isShuffle) {
                                playNextSong();
                            } else {
                                playNextSong();
                            }
                        }
                    }
                });
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
            System.out.println("position trong musicService: " + position);
        } else {
            mediaPlayer.stop();
            position = 0;
        }
        playSong(songs.get(position).getLink());
        if (listener != null) {
            listener.onSongChanged(songs.get(position));
        }
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
        if (listener != null) {
            listener.onSongChanged(songs.get(position));
        }
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

    public Song getCurrentSong() {
        return songs.get(position);
    }

    public void setRepeat(boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    public void setShuffle(boolean isShuffle) {
        this.isShuffle = isShuffle;
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
}
