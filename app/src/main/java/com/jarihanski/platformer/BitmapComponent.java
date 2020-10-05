package com.jarihanski.platformer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;

public class BitmapComponent {
    private Bitmap _bitmap = null;

    public BitmapComponent(Context context, String spriteName, Point size) {
        LoadBitMap(context, spriteName, size);
    }

    public void LoadBitMap(Context context, String spriteName, Point size) {
        Destroy();
        try {
            _bitmap = BitmapUtils.loadScaledBitmap(context, spriteName, size.x, size.y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap GetBitmap() {
        return _bitmap;
    }

    public void Destroy() {
        _bitmap = null;
    }
}
