package com.jarihanski.platformer;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class BitmapComponent {
    private final Game _game;
    private Bitmap _bitmap;

    public BitmapComponent(Game game) {
       _game = game;
    }

    public void LoadBitMap(String spriteName, PointF size) {
        try {
            _bitmap = _game._bitmapPool.createBitmap(spriteName, size.x, size.y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap GetBitmap() {
        return _bitmap;
    }
}
