package com.jarihanski.platformer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

public abstract class Entity {
    protected static final String TAG = "Entity";
    protected static Game _game = null;
    protected float _x = 0;
    protected float _y = 0;
    protected float _width = 0;
    protected float _height = 0;
    protected int _entityId;
    public EntityType _entityType = null;

    public enum EntityType {
        ENEMY,
        PLAYER,
        TERRAIN,
        COLLECTIBLE
    }

    public abstract void update(float dt);
    public abstract void render(final Canvas canvas, final Matrix transform, final Paint paint);

    protected void onCollision(final Entity that) {}

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

    public int getDamage() {return 0;}

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

    //AABB intersection test.
    //returns true on intersection, and sets the least intersecting axis in overlap
    static final PointF overlap = new PointF( 0 , 0 ); //Q&D PointF pool for collision detection. Assumes single threading.
    @SuppressWarnings ( "UnusedReturnValue" )
    static boolean getOverlap( final Entity a, final Entity b, final PointF overlap) {
        overlap.x = 0.0f;
        overlap.y = 0.0f;
        final float centerDeltaX = a.centerX() - b.centerX();
        final float halfWidths = (a._width + b._width) * 0.5f;
        float dx = Math.abs(centerDeltaX); //cache the abs, we need it twice

        if (dx > halfWidths) return false ; //no overlap on x == no collision

        final float centerDeltaY = a.centerY() - b.centerY();
        final float halfHeights = (a._height + b._height) * 0.5f;
        float dy = Math.abs(centerDeltaY);

        if (dy > halfHeights) return false ; //no overlap on y == no collision

        dx = halfWidths - dx; //overlap on x
        dy = halfHeights - dy; //overlap on y
        if (dy < dx) {
            overlap.y = (centerDeltaY < 0 ) ? -dy : dy;
        } else if (dy > dx) {
            overlap.x = (centerDeltaX < 0 ) ? -dx : dx;
        } else {
            overlap.x = (centerDeltaX < 0 ) ? -dx : dx;
            overlap.y = (centerDeltaY < 0 ) ? -dy : dy;
        }
        return true ;
    }

    public abstract void destroy();

    public void loadGameState(Context context) {
    }

    public void saveGameState(Context context) {
    }
}