package com.jarihanski.platformer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapComponent {
    private Bitmap _bitmap = null;

    public BitmapComponent(Context context, String spriteName, Utils.Vec2 size) {
        LoadBitMap(context, spriteName, size);
    }

    public void LoadBitMap(Context context, String spriteName, Utils.Vec2 size) {
        Destroy();
        try {
            _bitmap = BitmapUtils.loadScaledBitmap(context, spriteName, (int)size._x, (int)size._y);
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

    private Bitmap scaleToTargetHeight(final Bitmap bitmap, final int targetHeight) {
        float ratio = targetHeight / (float) bitmap.getHeight();
        int newH = (int) (bitmap.getHeight() * ratio);
        int newW = (int) (bitmap.getWidth() * ratio);
        return Bitmap.createScaledBitmap(bitmap, newW, newH, true);
    }
}
