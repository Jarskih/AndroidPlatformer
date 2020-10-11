package com.jarihanski.platformer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Hud {

    public Hud() {
    }

    public void render(Canvas canvas, Paint paint, Player player, int coins, Context context) {
        float textSize = 24f;
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(textSize);
        canvas.drawText(context.getString(R.string.player_health) + " " + player.getHealth(), 10, textSize, paint);
        canvas.drawText(context.getString(R.string.collected) + " " + player.getCollected(), 10, textSize*2, paint);
        canvas.drawText(context.getString(R.string.coins_left) + " " + coins, 10, textSize*3, paint);
    }
}
