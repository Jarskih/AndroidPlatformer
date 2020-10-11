package com.jarihanski.platformer;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class LevelData {
    public static final String NULL_SPRITE = "null_sprite";
    public static final String PLAYER = "player_left";
    public static final String STATIC_HAZARD = "spears";
    public static final String COLLECTIBLE = "coin";
    private static final String TAG = "Level data";
    private static final String DYNAMIC_HAZARD = "grass_enemy";
    public static int NO_TILE = 0;
    public int lastLevel;
    private int[][] _tiles = null;
    int _height = 0;
    int _width = 0;
    private final SparseArray<String> _tileIdToSpriteName = new SparseArray<>();
    private final Context _context;
    private final HashMap<Integer, String> _levelMap = new HashMap<Integer, String>();

    public LevelData(Context context, int levelName) {
        _context = context;
        addLevels();
        lastLevel = _levelMap.size();
        createLevel(levelName);
        updateLevelDimensions();
    }

    public int getTile(final int x, final int y) {
        return _tiles[x][y];
    }

    public int[] getRow(final int y) {
        return _tiles[y];
    }

    private void updateLevelDimensions() {
        _height = _tiles.length;
        _width = _tiles[0].length;
    }

    public String getSpriteName(final int tileType) {
        final String fileName = _tileIdToSpriteName.get(tileType);
        if(fileName != null) {
            return fileName;
        }
        return NULL_SPRITE;
    }

    public void createLevel(int level) {
        if(level < 1 || level > lastLevel) {
            Log.d("Wrong level number", TAG);
            level = 1;
        }

        _tileIdToSpriteName.put(1, PLAYER);
        _tileIdToSpriteName.put(5, STATIC_HAZARD);
        _tileIdToSpriteName.put(7, COLLECTIBLE);
        _tileIdToSpriteName.put(8, DYNAMIC_HAZARD);

        // Defaults
        _tileIdToSpriteName.put(0, "background");
        _tileIdToSpriteName.put(2, "grass_left");
        _tileIdToSpriteName.put(3, "grass_center");
        _tileIdToSpriteName.put(4, "grass_right");
        _tileIdToSpriteName.put(6, "mud_square");


        if(level == 1) {
            _tileIdToSpriteName.put(0, "background");
            _tileIdToSpriteName.put(2, "grass_left");
            _tileIdToSpriteName.put(3, "grass_center");
            _tileIdToSpriteName.put(4, "grass_right");
            _tileIdToSpriteName.put(6, "mud_square");
        } else if(level == 2) {
            _tileIdToSpriteName.put(0, "background");
            _tileIdToSpriteName.put(2, "snow_left");
            _tileIdToSpriteName.put(3, "snow_center");
            _tileIdToSpriteName.put(4, "snow_right");
            _tileIdToSpriteName.put(6, "snow_square");
        } else if(level == 3) {
            _tileIdToSpriteName.put(0, "background");
            _tileIdToSpriteName.put(2, "desert_left");
            _tileIdToSpriteName.put(3, "desert_center");
            _tileIdToSpriteName.put(4, "desert_right");
            _tileIdToSpriteName.put(6, "desert_square");
        }

        readLevelFile(level);
        updateLevelDimensions();
    }

    // https://stackoverflow.com/questions/9544737/read-file-from-assets
    void readLevelFile(int levelNumber) {

        String levelName = _levelMap.get(levelNumber);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(_context.getAssets().open("levels/"+levelName)));

            String row;
            int sizeY = 0;
            int sizeX = 0;
            ArrayList<Integer> lines = new ArrayList<>();
            while ((row = reader.readLine()) != null) {
                final String[] tokens = row.split(",");
                for (String token : tokens) {
                    lines.add(Integer.parseInt(token));
                }
                sizeX = tokens.length;
                sizeY++;
            }

            _tiles = new int[sizeY][sizeX];
            int counter = 0;
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    int tileId = lines.get(counter);
                    _tiles[y][x] = tileId;
                    counter++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addLevels() {
        try {
            String[] levels = _context.getAssets().list("levels");
            assert levels != null;
            for(int i = 0; i < levels.length; i++){
                _levelMap.put(i+1, levels[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
