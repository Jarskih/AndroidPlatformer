package com.jarihanski.platformer;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public abstract class Entity {
    protected static final String TAG = "Entity";
    protected static Game _game = null; //shared ref, managed by the Game-class!
    protected float _x = 0;
    protected float _y = 0;
    protected float _width = 0;
    protected float _height = 0;
    protected float _velX = 0;
    protected float _velY = 0;

    public abstract void update(float dt);
    public abstract void render(final Canvas canvas, final Matrix transform, final Paint paint);
    public boolean isDead() {
        return false;
    }

    protected void onCollision(final Entity that, final int damage) {}

    float left() {
        return _x;
    }
    float right() {
        return _x + _width;
    }
    float top() {
        return _y;
    }
    float bottom() {
        return _y + _height;
    }

    float centerY() {
        return _y + (_height * 0.5f);
    }
    float centerX() { return _x + (_width * 0.5f); }

    boolean isColliding(final Entity other) {
        if (this == other) {
            throw new AssertionError("isColliding: You shouldn't test Entities against themselves!");
        }
        return Entity.isAABBOverlapping(this, other);
    }

    static boolean isAABBOverlapping(final Entity a, final Entity b) {
        return !(a.right() <= b.left()
                || b.right() <= a.left()
                || a.bottom() <= b.top()
                || b.bottom() <= a.top());
    }

    public abstract void destroy();

    public abstract void spawn();
}