package com.jarihanski.platformer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Hud {

    public Hud() {
    }

    public void render(Canvas canvas, Paint paint, int health, float distanceTraveled) {
        float textSize = 24f;
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(textSize);
    }
}
