package com.example.project_pbo;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

public class AudioPlayerManager {
    private static AudioPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private Lagu currentSong;
    private boolean isPlaying = false;
    private OnPlaybackListener listener;

    public interface OnPlaybackListener {
        void onPlay(Lagu song);
        void onPause();
        void onStop();
        void onError(String msg);
    }

    private AudioPlayerManager() {}

    public static synchronized AudioPlayerManager getInstance() {
        if (instance == null) {
            instance = new AudioPlayerManager();
        }
        return instance;
    }

    public void setListener(OnPlaybackListener listener) {
        this.listener = listener;
    }

    public void playSong(Lagu song) {
        if (song == null) return;
        this.currentSong = song;

        releaseMediaPlayer();

        if (song.getPreviewUrl() == null || song.getPreviewUrl().isEmpty()) {
            if (listener != null) listener.onError("Preview audio tidak tersedia");
            return;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            mediaPlayer.setDataSource(song.getPreviewUrl());
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                song.putar(); // OOP method call
                if (listener != null) listener.onPlay(song);
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                song.hentikan();
                if (listener != null) listener.onStop();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                isPlaying = false;
                if (listener != null) listener.onError("Gagal memutar audio streaming");
                return true;
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            if (listener != null) listener.onError("Error: " + e.getMessage());
        }
    }

    public void togglePlayPause() {
        if (mediaPlayer == null) {
            if (currentSong != null) playSong(currentSong);
            return;
        }
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            if (currentSong != null) currentSong.jeda();
            if (listener != null) listener.onPause();
        } else {
            mediaPlayer.start();
            isPlaying = true;
            if (currentSong != null) currentSong.putar();
            if (listener != null) listener.onPlay(currentSong);
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public Lagu getCurrentSong() {
        return currentSong;
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception ignored) {}
            mediaPlayer = null;
            isPlaying = false;
        }
    }
}
