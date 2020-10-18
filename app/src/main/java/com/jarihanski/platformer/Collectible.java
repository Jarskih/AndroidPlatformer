package com.jarihanski.platformer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

public class Collectible extends Entity {
    private BitmapComponent _bitmapComponent = null;
    private static final float DEFAULT_DIMENTION = 1f;
    private static final float MAX_DELTA = 0.48f;
    private float _velY = 0;
    private boolean _isGrounded = false;

    public Collectible(final String spriteName, final float x, final float y) {
        _entityType = EntityType.COLLECTIBLE;
        _x = x;
        _y = y;
        _width = DEFAULT_DIMENTION;
        _height = DEFAULT_DIMENTION;
        final PointF size = new PointF(DEFAULT_DIMENTION, DEFAULT_DIMENTION);
        _bitmapComponent = new BitmapComponent(_game);
        _bitmapComponent.LoadBitMap(spriteName, size);
    }

    @Override
    public void update(float dt) {
        if(!_isGrounded) {
            _velY += _game.getConfig().GRAVITY * dt;
        }
        _y += Utils.clamp(_velY * dt, -MAX_DELTA, MAX_DELTA);
        _isGrounded = false;
    }

    @Override
    protected void onCollision(final Entity that) {
        Entity.getOverlap(this, that, Entity.overlap);
        _x += Entity.overlap.x;
        _y += Entity.overlap.y;
        if(overlap.y != 0) {
            _velY = 0;
            if(Entity.overlap.y < 0.0f) {
                _isGrounded = true;
            }
        }
    }

    @Override
    public void loadGameState(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.saved_state), Context.MODE_PRIVATE);
        if(sharedPref.contains(_entityId+"x")) {
            _x = sharedPref.getFloat(_entityId + "x", 0);
            _y = sharedPref.getFloat(_entityId + "y", 0);
            _velY = sharedPref.getFloat(_entityId + "velY", 0);
        }
    }

    @Override
    public void saveGameState(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.saved_state), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(_entityId+"x", _x);
        editor.putFloat(_entityId+"y", _y);
        editor.putFloat(_entityId+"velY", _velY);
        editor.apply();
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
