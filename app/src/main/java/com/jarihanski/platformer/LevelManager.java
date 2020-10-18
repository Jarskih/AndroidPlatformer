package com.jarihanski.platformer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

public class LevelManager {
    private final String TAG = "LevelManager";
    private final ArrayList<Entity> _entities = new ArrayList<>();
    private final ArrayList<Entity> _entitiesToAdd = new ArrayList<>();
    private final ArrayList<Entity> _entitiesToRemove = new ArrayList<>();
    private final Game _game;
    Player _player;
    private int _levelHeight = 0;
    private int _levelWidth = 0;
    private int _coinsLeft = 0;
    private int _entityIndex = 0;
    private int _lastLevel = 0;

    public LevelManager(final LevelData levelData, Game game) {
        _game = game;
        _lastLevel = levelData.lastLevel;
        loadMapAssets(levelData);
    }

    private void loadMapAssets(final LevelData levelData) {
        cleanUp();
        _levelHeight = levelData._height;
        _levelWidth = levelData._width;

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

        for(Entity e : _entitiesToAdd) {
            e.loadGameState(_game.getContext());
        }
    }
    private void createEntity (final String spriteName, final int x, final int y) {
        Entity e = null;
        if(spriteName.equalsIgnoreCase(LevelData.PLAYER)) {
            e = new Player(spriteName, x, y);
            _player = (Player)e;
        } else if(spriteName.equalsIgnoreCase(LevelData.STATIC_HAZARD)) {
            e = new StaticHazard(spriteName, x, y);
        } else if(spriteName.equalsIgnoreCase(LevelData.COLLECTIBLE)) {
            e = new Collectible(spriteName, x, y);
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

        if(_entitiesToAdd.contains(e)){
            return;
        }

        if(e._entityType == Entity.EntityType.COLLECTIBLE) {
            _coinsLeft += 1;
        }
        e._entityId = _entityIndex;
        _entityIndex++;
        _entitiesToAdd.add(e);
    }

    void removeEntity(final Entity e) {
        if(e == null) {
            Log.d("Entity null", TAG);
            return;
        }

        if(_entitiesToRemove.contains(e)){
            return;
        }

        if(e._entityType == Entity.EntityType.COLLECTIBLE) {
            _coinsLeft -= 1;
        }

        _entitiesToRemove.add(e);
    }

    private void cleanUp() {
        addAndRemoveEntities();
        for (Entity e : _entitiesToAdd) {
            e.destroy();
        }
    }

    public int getCoinsLeft() {
        return _coinsLeft;
    }

    public void destroy() {
        cleanUp();
    }

    public ArrayList<Entity> GetEntities() {
        return _entities;
    }
    public int GetHeight() { return _levelHeight; }
    public int GetWidth() { return _levelWidth; }

    public int lastLevel() {
        return _lastLevel;
    }

    public void LoadState() {
        SharedPreferences sharedPref = _game.getContext().getSharedPreferences(_game.getContext().getString(R.string.saved_state), Context.MODE_PRIVATE);
        if(sharedPref.contains("colleted")) {
            _coinsLeft = sharedPref.getInt("coinsLeft" , 0);
        }
    }

    public void SaveState() {
        SharedPreferences sharedPref = _game.getContext().getSharedPreferences(_game.getContext().getString(R.string.saved_state), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("coinsLeft", _coinsLeft);
        editor.apply();
    }
}
