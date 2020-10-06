package com.jarihanski.platformer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

public class Player extends Entity {
    protected static final String TAG = "Player";
    private static final float MAX_DELTA = 0.48f;
    private static final float DEFAULT_DIMENTION = 1f;
    private float MIN_INPUT_TO_TURN = 0.05f;
    private static int _facing = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = -1;
    private static final int _respawnPosY = -5;

    private BitmapComponent _bitmapComponent = null;
    private float _velX = 0;
    private float _velY = 0;
    private boolean _isGrounded;

    public Player(final String spriteName, final int x, final int y) {
        _facing = LEFT;
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
        final InputManager controls = _game.getControls();
        final float direction = controls._horizontalFactor;

        if(Math.abs(direction) > MIN_INPUT_TO_TURN) {
            UpdateFacing(direction);
            _velX = direction * _game.getConfig().PLAYER_SPEED;
        } else {
            _velX = 0;
        }

        if(controls._isJumping && _isGrounded){
            _velY = _game.getConfig().PLAYER_JUMP_FORCE;
            _isGrounded = false;
        }

        if(!_isGrounded) {
            _velY += _game.getConfig().GRAVITY * dt;
        }

        Clamp(dt);
        _isGrounded = false;
    }

    private void UpdateFacing(float direction) {
        if(direction < 0) {
            _facing = LEFT;
        } else if(direction > 0) {
            _facing = RIGHT;
        }
    }

    private void Clamp(float dt) {
        _y += Utils.clamp(_velY * dt, -MAX_DELTA, MAX_DELTA);
        _x += Utils.clamp(_velX * dt, -MAX_DELTA, MAX_DELTA);

        if(_y > _game.getLevelHeight()) {
            _y = _respawnPosY;
        }

        if(_x < 0) {
            _x = 0;
        }
        if(_x > _game.getLevelWidth()) {
            _x = _game.getLevelWidth();
        }
    }

    @Override
    public void onCollision(final Entity that) {
        Entity.getOverlap(this, that, Entity.overlap);
        _x += Entity.overlap.x;
        _y += Entity.overlap.y;
        if(overlap.y != 0) {
            _velY = 0;
            if(Entity.overlap.y < 0.0f) {
                _isGrounded = true;
            }
        }
    };

    @Override
    public void render(Canvas canvas, final Matrix transform, final Paint paint) {
        transform.preScale(_facing, 1.0f);
        if(_facing == RIGHT ){
            final float offset = _game.worldToScreen(_width, _height).x;
            transform.postTranslate(offset, 0);
        }
        canvas.drawBitmap(_bitmapComponent.GetBitmap(), transform, paint);
    }

    @Override
    public void destroy() {
        _bitmapComponent = null;
    }

    public boolean isDead() {
        return false;
    }
}
