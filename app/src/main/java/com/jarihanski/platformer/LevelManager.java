package com.jarihanski.platformer;

import android.util.Log;

import java.util.ArrayList;

public class LevelManager {
    private final String TAG = "LevelManager";
    private final ArrayList<Entity> _entities = new ArrayList<>();
    private final ArrayList<Entity> _entitiesToAdd = new ArrayList<>();
    private final ArrayList<Entity> _entitiesToRemove = new ArrayList<>();
    Player _player;

    public LevelManager(final LevelData levelData) {
        loadMapAssets(levelData);
    }

    private void loadMapAssets(final LevelData levelData) {
        cleanUp();
        int _levelHeight = levelData._height;
        int _levelWidth = levelData._width;

        for (int y = 0; y < _levelHeight; y++) {
            final int[] row = levelData.getRow(y);
            for (int x = 0; x < row.length; x++) {
                final int tileID = row[x];
                if (tileID == LevelData.NO_TILE) {
                    continue;
                }
                final String spriteName = levelData.getSpriteName(tileID);
                createEntity(spriteName, x, y);
            }
        }
    }
    private void createEntity (final String spriteName, final int x, final int y) {
        Entity e = null;
        if(spriteName.equalsIgnoreCase(LevelData.PLAYER)) {
            e = new Player(spriteName, x, y);
            _player = (Player)e;
        } else {
            e = new StaticEntity(spriteName, x, y);
        }
        addEntity(e);
    }

    void addAndRemoveEntities() {
        for (Entity e : _entitiesToRemove) {
            _entities.remove(e);
        }
        _entities.addAll(_entitiesToAdd);
        _entitiesToRemove.clear();
        _entitiesToAdd.clear();
    }

    private void addEntity(final Entity e) {
        if(e == null) {
            Log.d("Entity null", TAG);
            return;
        }
        _entitiesToAdd.add(e);
    }

    private void removeEntity(final Entity e) {
        if(e == null) {
            Log.d("Entity null", TAG);
            return;
        }
        _entitiesToRemove.add(e);
    }

    private void cleanUp() {
        addAndRemoveEntities();
        for (Entity e : _entitiesToAdd) {
            e.destroy();
        }
    }

    public void destroy() {
        cleanUp();
    }

    public ArrayList<Entity> GetEntities() {
        return _entities;
    }
}
