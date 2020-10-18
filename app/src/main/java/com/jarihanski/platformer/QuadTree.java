package com.jarihanski.platformer;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

// https://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374

public class QuadTree {
    private static final int  MAX_OBJECTS = 12;
    private static final int MAX_LEVELS = 2;

    private final int _level;
    private final ArrayList<Entity> _entities;
    private final RectF _bounds;
    private final QuadTree[] _nodes;

    public QuadTree(int level, RectF bounds) {
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
        float subWidth = _bounds.width() / 2f;
        float subHeight = _bounds.height() / 2f;
        float x = _bounds.left;
        float y = _bounds.top;

        RectF rect0 = new RectF(x + subWidth, y, subWidth, subHeight);
        _nodes[0] = new QuadTree(_level+1, rect0);

        RectF rect1 = new RectF(x, y, subWidth, subHeight);
        _nodes[1] = new QuadTree(_level+1, rect1);

        RectF rect2 = new RectF(x , y + subHeight, subWidth, subHeight);
        _nodes[2] = new QuadTree(_level+1, rect2);

        RectF rect3 = new RectF(x + subWidth, y + subHeight, subWidth, subHeight);
        _nodes[3] = new QuadTree(_level+1, rect3);
    }

    private int getIndex(Entity entity) {
        int index = -1;

        double verticalMidpoint = _bounds.left + (_bounds.width() / 2f);
        double horizontalMidpoint = _bounds.top + (_bounds.height() / 2f);

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
}
