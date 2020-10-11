package com.jarihanski.platformer;

import androidx.constraintlayout.solver.widgets.Rectangle;

import java.util.ArrayList;
import java.util.List;

// https://gamedevelopment.tutsplus.com/tutorials/quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374

public class QuadTree {
    private int MAX_OBJECTS = 10;
    private int MAX_LEVELS = 5;

    private int level;
    private List<Entity> _entities;
    private Rectangle _bounds;
    private QuadTree[] _nodes;

    public QuadTree(int pLevel, Rectangle pBounds) {
        level = pLevel;
        _entities = new ArrayList();
        _bounds = pBounds;
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
        _nodes[0] = new QuadTree(level+1, rect0);
        Rectangle rect1 = new Rectangle();
        rect1.setBounds(x + subWidth, y, subWidth, subHeight);
        _nodes[1] = new QuadTree(level+1, rect1);
        Rectangle rect2 = new Rectangle();
        rect2.setBounds(x + subWidth, y, subWidth, subHeight);
        _nodes[2] = new QuadTree(level+1, rect2);
        Rectangle rect3 = new Rectangle();
        rect3.setBounds(x + subWidth, y, subWidth, subHeight);
        _nodes[3] = new QuadTree(level+1, rect3);
    }

    private int getIndex(Entity entity) {
        int index = -1;

        boolean topQuadrant = (entity._x < _bounds.getCenterX() && entity._y + entity._height < _bounds.getCenterY());
        boolean bottomQuadrant = (entity._y > _bounds.getCenterX());

        if (entity._x < _bounds.getCenterY() && entity._x + entity._width < _bounds.getCenterY()) {
            if (topQuadrant) {
                index = 1;
            }
            else if (bottomQuadrant) {
                index = 2;
            }
        }
        else if (entity._x > _bounds.getCenterY()) {
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

        if (_entities.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (_nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < _entities.size()) {
                int index = getIndex(_entities.get(i));
                if (index != -1) {
                    _nodes[index].insert(_entities.remove(i));
                }
                else {
                    i++;
                }
            }
        }
    }

    public List<Entity> retrieve(List<Entity> entities, Entity entity) {
        int index = getIndex(entity);
        if (index != -1 && _nodes[0] != null) {
            _nodes[index].retrieve(entities, entity);
        }

        entities.addAll(_entities);

        return entities;
    }
}
