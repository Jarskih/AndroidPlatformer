package com.jarihanski.platformer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import static com.jarihanski.platformer.Utils.worldToScreen;

public class StaticEntity extends Entity {
    private BitmapComponent _bitmapComponent = null;
    private static final float DEFAULT_DIMENTION = 1.0f;

    public StaticEntity(final String spriteName, final int x, final int y) {
        _x = x;
        _y = y;
        final Utils.Vec2 size = worldToScreen(DEFAULT_DIMENTION, DEFAULT_DIMENTION);
        _bitmapComponent = new BitmapComponent(_game.getContext(), spriteName, size);
    }

    public void LoadBitmap(final String spriteName, final int x, final int y) {
        destroy();
        final Utils.Vec2 size = worldToScreen(DEFAULT_DIMENTION, DEFAULT_DIMENTION);
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
