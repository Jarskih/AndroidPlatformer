package com.jarihanski.platformer;

import android.util.SparseArray;

public class TestLevel extends LevelData {
    private final SparseArray<String> _tileIdToSpriteName = new SparseArray<>();

    public TestLevel() {
        createLevel();
        updateLevelDimensions();
    }

    @Override
    public String getSpriteName(final int tileType) {
        final String fileName = _tileIdToSpriteName.get(tileType);
        if(fileName != null) {
            return fileName;
        }
        return NULL_SPRITE;
    }

    @Override
    void createLevel() {
        _tileIdToSpriteName.put(0, "background");
        _tileIdToSpriteName.put(1, PLAYER);
        _tileIdToSpriteName.put(2, "grass_left");
        _tileIdToSpriteName.put(3, "grass_center");
        _tileIdToSpriteName.put(4, "grass_right");

        _tiles = new int[][] {
                {0, 0, 0, 0 ,0 ,0 ,0 ,0},
                {0, 0, 0, 1 ,0 ,0 ,0 ,0},
                {0, 0, 0, 0 ,0 ,0 ,0 ,0},
                {0, 2, 3, 3 ,3 ,3 ,4 ,0},
                {0, 0, 0, 0 ,0 ,0 ,0 ,0},
        };
    }
}
