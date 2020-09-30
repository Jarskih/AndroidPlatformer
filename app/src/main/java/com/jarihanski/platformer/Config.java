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

    Config(Context context) {
        STAGE_WIDTH = readConfigInt(context, "STAGE_WIDTH");
        STAGE_HEIGHT = readConfigInt(context, "STAGE_HEIGHT");
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
