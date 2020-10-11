package com.jarihanski.platformer;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Random;

import static android.media.MediaPlayer.create;

public class MusicPlayer {
    private MediaPlayer _mediaPlayer = null;
    private final Context _context;
    private final Random _random = new Random();

    public MusicPlayer(Context context) {
        _context = context;
    }

    public void start() {
        if(_mediaPlayer != null && !_mediaPlayer.isPlaying()) {
            _mediaPlayer.start();
        }
    }

    public void pause() {
        if(_mediaPlayer != null && _mediaPlayer.isPlaying()) {
            _mediaPlayer.pause();
        }
    }

    public void stop() {
        if(_mediaPlayer != null && _mediaPlayer.isPlaying()) {
            _mediaPlayer.pause();
            _mediaPlayer.stop();
        }
    }

    public void destroy() {
        if(_mediaPlayer != null && _mediaPlayer.isPlaying()) {
            _mediaPlayer.stop();
            _mediaPlayer.release();
        }
        _mediaPlayer = null;
    }

    public void playMusic(int level) {
        destroy();
        if(level == 1) {
            _mediaPlayer = create(_context, R.raw.level1);
        } else if(level == 2) {
            _mediaPlayer = create(_context, R.raw.level2);
        } else if(level == 3){
            _mediaPlayer = create(_context, R.raw.level3);
        } else {
            _mediaPlayer = create(_context, R.raw.level1);
        }

        _mediaPlayer.start();
        _mediaPlayer.setLooping(true);

        _mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
    }
}
