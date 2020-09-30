package com.jarihanski.platformer;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.io.IOException;

public class SoundPlayer {
    private SoundPool _soundPool;
    private static final int MAX_STREAMS = 3;
    public static int CRASH;
    public static int PROJECTILE_HIT;
    public static int DEAD;
    public static int PROJECTILE;
    public static int START;
    public static int END;

    public SoundPlayer(Context context) {
        AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
        _soundPool = new SoundPool.Builder().setAudioAttributes(attributes).setMaxStreams(MAX_STREAMS).build();
        loadSounds(context);
    }

    private void loadSounds(Context context) {
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor descriptor;
        try {
            descriptor = assetManager.openFd("explosion1.wav");
            CRASH = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("explosion2.wav");
            PROJECTILE_HIT = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("explosion3.wav");
            DEAD = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("flaunch.wav");
            PROJECTILE = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("start.wav");
            START = _soundPool.load(descriptor, 1);
            descriptor = assetManager.openFd("endgame.wav");
            END = _soundPool.load(descriptor, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play(final int soundID, final float volume) {
        final int priority = 1;
        final int loop = 0;
        final float rate = 1.0f;
        if(soundID > 0) {
            _soundPool.play(soundID, volume, volume, priority, loop, rate);
        }
    }

    public void destroy() {
        _soundPool.release();
        _soundPool = null;
    }
}
