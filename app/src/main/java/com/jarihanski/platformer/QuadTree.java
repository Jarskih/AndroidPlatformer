package com.jarihanski.platformer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.constraintlayout.solver.widgets.Rectangle;

import java.util.ArrayList;
import java.util.List;

// https://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374

public class QuadTree {
    private int MAX_OBJECTS = 12;
    private int MAX_LEVELS = 2;

    private int _level;
    private List<Entity> _entities;
    private Rectangle _bounds;
    private QuadTree[] _nodes;

    public QuadTree(int level, Rectangle bounds) {
        _level = level;
        _entities = new ArrayList();
        _bounds = bounds;
        _nodes = new QuadTree[4];
    }

    public void clear() {
        _entities.clear();

        for (int i = 0; i < _nodes.length; i++) {
            if (_nodes[i] != null) {
                _nodes[i].clear();
                _nodes[i] = null;
            }
        }
    }

    private void split() {
        int subWidth = _bounds.width / 2;
        int subHeight = _bounds.height / 2;
        int x = _bounds.x;
        int y = _bounds.y;

        Rectangle rect0 = new Rectangle();
        rect0.setBounds(x + subWidth, y, subWidth, subHeight);
        _nodes[0] = new QuadTree(_level+1, rect0);

        Rectangle rect1 = new Rectangle();
        rect1.setBounds(x, y, subWidth, subHeight);
        _nodes[1] = new QuadTree(_level+1, rect1);

        Rectangle rect2 = new Rectangle();
        rect2.setBounds(x , y + subHeight, subWidth, subHeight);
        _nodes[2] = new QuadTree(_level+1, rect2);

        Rectangle rect3 = new Rectangle();
        rect3.setBounds(x + subWidth, y + subHeight, subWidth, subHeight);
        _nodes[3] = new QuadTree(_level+1, rect3);
    }

    private int getIndex(Entity entity) {
        int index = -1;

        double verticalMidpoint = _bounds.x + (_bounds.width / 2);
        double horizontalMidpoint = _bounds.y + (_bounds.height / 2);

        boolean topQuadrant = (entity._y < horizontalMidpoint && entity._y + entity._height < horizontalMidpoint);
        boolean bottomQuadrant = (entity._y > horizontalMidpoint);

        if (entity._x < verticalMidpoint  && entity._x + entity._width < verticalMidpoint ) {
            if (topQuadrant) {
                index = 1;
            }
            else if (bottomQuadrant) {
                index = 2;
            }
        }
        else if (entity._x > verticalMidpoint ) {
            if (topQuadrant) {
                index = 0;
            }
            else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    public void insert(Entity entity) {
        if (_nodes[0] != null) {
            int index = getIndex(entity);

            if (index != -1) {
                _nodes[index].insert(entity);

                return;
            }
        }

        _entities.add(entity);

        if (_entities.size() > MAX_OBJECTS && _level < MAX_LEVELS) {
            if (_nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < _entities.size()) {
                int index = getIndex(_entities.get(i));
                if (index != -1) {
                    _nodes[index].insert(_entities.get(i));
                    _entities.remove(i);
                }
                else {
                    i++;
                }
            }
        }
    }

    public void retrieve(List<Entity> entities, Entity entity) {
        if (_nodes[0] != null) {
            int index = getIndex(entity);
            if (index != -1) {
                _nodes[index].retrieve(entities, entity);
            }
        }
        entities.addAll(_entities);
    }

    public void render(Canvas canvas, Paint paint, ViewPort camera) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        for (QuadTree t : _nodes) {
            if(t._nodes[0] != null) {
                Rect r = new Rect(t._nodes[0]._bounds.x, t._nodes[0]._bounds.y,  t._nodes[0]._bounds.width, t._nodes[0]._bounds.height);
                canvas.drawRect(r, paint);
            }
            if(t._nodes[1] != null) {
                Rect r = new Rect(t._nodes[1]._bounds.x, t._nodes[1]._bounds.y, t._nodes[1]._bounds.width, t._nodes[1]._bounds.height);
                canvas.drawRect(r, paint);
            }
            if(t._nodes[2] != null) {
                Rect r = new Rect(t._nodes[2]._bounds.x, t._nodes[2]._bounds.y, t._nodes[2]._bounds.width, t._nodes[2]._bounds.height);
                canvas.drawRect(r, paint);
            }
            if(t._nodes[3] != null) {
                Rect r = new Rect(t._nodes[3]._bounds.x, t._nodes[3]._bounds.y, t._nodes[3]._bounds.width, t._nodes[3]._bounds.height);
                canvas.drawRect(r, paint);
            }
        }
    }
}
