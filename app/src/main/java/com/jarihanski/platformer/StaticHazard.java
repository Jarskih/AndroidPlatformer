package com.jarihanski.platformer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

public class StaticHazard extends Entity {
    private BitmapComponent _bitmapComponent = null;
    private static final float DEFAULT_DIMENTION = 1f;
    private final int _damage = 1;

    public StaticHazard(final String spriteName, final float x, final float y) {
        _entityType = EntityType.ENEMY;
        _x = x;
        _y = y;
        _width = DEFAULT_DIMENTION;
        _height = DEFAULT_DIMENTION;
        final PointF size = new PointF(DEFAULT_DIMENTION, DEFAULT_DIMENTION);
        _bitmapComponent = new BitmapComponent(_game);
        _bitmapComponent.LoadBitMap(spriteName, size);
    }

    public void LoadBitmap(final String spriteName, final float x, final float y) {
        destroy();
        final PointF size = new PointF(DEFAULT_DIMENTION, DEFAULT_DIMENTION);
        _bitmapComponent = new BitmapComponent(_game);
        _bitmapComponent.LoadBitMap(spriteName, size);
    }

    @Override
    public int getDamage() {
        return _damage;
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
        _bitmapComponent = null;
    }
}