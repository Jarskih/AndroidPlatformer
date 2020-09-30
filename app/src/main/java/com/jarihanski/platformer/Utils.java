package com.jarihanski.platformer;
public abstract class Utils {

    static float clamp(float val, final float min, final float max) {
        if(val < min) {
            val = min;
        } else if(val > max) {
            val = max;
        }
        return val;
    }

    static float wrap(float val, final float max) {
        if(val < (float) 0) {
            val = max;
        } else if(val > max) {
            val = (float) 0;
        }
        return val;
    }

    static Vec2 worldToScreen(final float width, final float height) {
        return new Vec2(width*50, height*50);
    }

    static class Vec2 {
        public float _x;
        public float _y;
        public Vec2(float x, float y) {
            _x = x;
            _y = y;
        }
    }
}