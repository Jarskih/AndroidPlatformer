package com.jarihanski.platformer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

public class Player extends Entity {
    private BitmapComponent _bitmapComponent = null;
    private static final float DEFAULT_DIMENTION = 1.0f;

    public Player(final String spriteName, final int x, final int y) {
        _x = x;
        _y = y;
        _width = DEFAULT_DIMENTION;
        _height = DEFAULT_DIMENTION;
        final Point size = _game.worldToScreen(DEFAULT_DIMENTION, DEFAULT_DIMENTION);
        _bitmapComponent = new BitmapComponent(_game.getContext(), spriteName, size);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(Canvas canvas, final Matrix transform, final Paint paint) {
        canvas.drawBitmap(_bitmapComponent.GetBitmap(), transform, paint);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void spawn() {

    }
}
