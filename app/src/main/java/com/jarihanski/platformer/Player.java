package com.jarihanski.platformer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

public class Player extends Entity {
    protected static final String TAG = "Player";
    private static final float MAX_DELTA = 0.48f;
    private static final float DEFAULT_DIMENTION = 1f;
    private final float MIN_INPUT_TO_TURN = _game.getConfig().MIN_INPUT_TO_TURN;
    private final float _recoveryTime = _game.getConfig().PLAYER_INVULNERABLE_TIME;
    private float _timeSinceDamage = 2f;
    private static int _facing = 0;
    private static final int LEFT = 1;
    private static final int RIGHT = -1;

    private BitmapComponent _bitmapComponent = null;
    private float _velX = 0;
    private float _velY = 0;
    private boolean _isGrounded;
    private int _health = 0;
    private int _collected = 0;

    public Player(final String spriteName, final int x, final int y) {
        _entityType = EntityType.PLAYER;
        _facing = LEFT;
        _x = x;
        _y = y;
        _width = DEFAULT_DIMENTION;
        _height = DEFAULT_DIMENTION;
        final PointF size = new PointF(DEFAULT_DIMENTION, DEFAULT_DIMENTION);
        _bitmapComponent = new BitmapComponent(_game);
        _bitmapComponent.LoadBitMap(spriteName, size);
        _health = _game.getConfig().PLAYER_STARTING_HEALTH;
    }

    @Override
    public void update(float dt) {
        _timeSinceDamage += dt;

        final InputManager controls = _game.getControls();
        final float direction = controls._horizontalFactor;

        if(Math.abs(direction) > MIN_INPUT_TO_TURN) {
            UpdateFacing(direction);
            _velX = direction * _game.getConfig().PLAYER_SPEED;
        } else {
            _velX = 0;
        }

        if(controls._isJumping && _isGrounded){
            _game.onGameEvent(Game.GameEvent.Jump, null);
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
            _health = -1;
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
        if(that._entityType == EntityType.COLLECTIBLE) {
            _collected++;
            _game.removeEntity(that);
            _game.onGameEvent(Game.GameEvent.CoinPickup, this);
            return;
        }

        Entity.getOverlap(this, that, Entity.overlap);
        _x += Entity.overlap.x;
        _y += Entity.overlap.y;
        if(overlap.y != 0) {
            _velY = 0;
            if(Entity.overlap.y < 0.0f) {
                _isGrounded = true;
            }
        }

        if(isInvincible()) {
            return;
        }

        if(that._entityType == EntityType.ENEMY) {
            _health -= that.getDamage();
            _timeSinceDamage = 0;
            _velY += _game.getConfig().PLAYER_JUMP_FORCE;
            _game.onGameEvent(Game.GameEvent.PlayerDamaged, this);
        }
    }

    public boolean isInvincible() {
        return _timeSinceDamage < _recoveryTime;
    }

    @Override
    public void render(Canvas canvas, final Matrix transform, final Paint paint) {
        transform.preScale(_facing, 1.0f);
        if(_facing == RIGHT ){
            final float offset = _game.worldToScreen(_width, _height).x;
            transform.postTranslate(offset, 0);
        }

        if(isInvincible()) {
            blink(canvas, paint, transform);
        } else {
            paint.setAlpha(255);
            canvas.drawBitmap(_bitmapComponent.GetBitmap(), transform, paint);
        }
    }

    private void blink(final Canvas canvas, final Paint paint, final Matrix transform) {
        float _blinkSpeed = 50; // Not moved to config because not needed for balancing the game
        int newAlpha = (int)(Math.sin(_timeSinceDamage*_blinkSpeed) * 255);
        paint.setAlpha(newAlpha);
        canvas.drawBitmap(_bitmapComponent.GetBitmap(),transform, paint);
    }

    @Override
    public void loadGameState(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.saved_state), Context.MODE_PRIVATE);
        if(sharedPref.contains(_entityId+"x")) {
            _x = sharedPref.getFloat(_entityId + "x", 0);
            _y = sharedPref.getFloat(_entityId + "y", 0);
            _velY = sharedPref.getFloat(_entityId + "velY", 0);
            _velX = sharedPref.getFloat(_entityId + "velX", 0);
            _health = sharedPref.getInt(_entityId + "health", 0);
            _collected = sharedPref.getInt(_entityId + "collected", 0);
        }
    }

    @Override
    public void saveGameState(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.saved_state), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(_entityId+"x", _x);
        editor.putFloat(_entityId+"y", _y);
        editor.putFloat(_entityId+"velY", _velY);
        editor.putFloat(_entityId+"velX", _velX);
        editor.putInt(_entityId+"health", _health);
        editor.putInt(_entityId + "collected", _collected);
        editor.apply();
    }

    @Override
    public void destroy() {
        _bitmapComponent = null;
    }

    public boolean isDead() {
        return _health <= 0;
    }
    public int getHealth() {
        return _health;
    }
    public int getCollected() {return _collected;}
}
