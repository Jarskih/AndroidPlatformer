package com.jarihanski.platformer;

public abstract class LevelData {
    public static final String NULL_SPRITE = "null_sprite";
    public static final String PLAYER = "player_left";
    public static int NO_TILE = 0;
    int[][] _tiles = null;
    int _height = 0;
    int _width = 0;

    public int getTile(final int x, final int y) {
        return _tiles[x][y];
    }

    public int[] getRow(final int y) {
        return _tiles[y];
    }

    protected void updateLevelDimensions() {
        _height = _tiles.length;
        _width = _tiles[0].length;
    }

    abstract public String getSpriteName(final int tileType);
    abstract void createLevel();
}
