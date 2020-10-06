package com.jarihanski.platformer;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final String TAG = "Config";

    public final int STAGE_WIDTH;
    public final int STAGE_HEIGHT;
    public final float GRAVITY;
    public final float PLAYER_SPEED;
    public final float PLAYER_JUMP_FORCE;

    Config(Context context) {
        STAGE_WIDTH = readConfigInt(context, "STAGE_WIDTH");
        STAGE_HEIGHT = readConfigInt(context, "STAGE_HEIGHT");
        GRAVITY = readConfigFloat(context, "GRAVITY");
        PLAYER_SPEED = readConfigFloat(context, "PLAYER_SPEED");
        PLAYER_JUMP_FORCE = readConfigFloat(context, "PLAYER_JUMP_FORCE");
    }

    // https://stackoverflow.com/questions/5140539/android-config-file
    private int readConfigInt(Context context, String name) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return Integer.parseInt(properties.getProperty(name));
        } catch (IOException e) {
            Log.e(TAG, "Cant open a file");
        }

        return 0;
    }

    private float readConfigFloat(Context context, String name) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.config);
            Properties properties = new Properties();
            properties.load(rawResource);
            return Float.parseFloat(properties.getProperty(name));
        } catch (IOException e) {
            Log.e(TAG, "Cant open a file");
        }

        return 0;
    }
}
