package com.jarihanski.platformer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class  Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    public static final String TAG = "Game";
    private static int BACKGROUND_COLOR = Color.argb(255, 135, 206, 235);
    private static final float METERS_TO_SHOW_X = 16f; //set the value you want fixed
    private static final float METERS_TO_SHOW_Y = 0f;  //the other is calculated at runtime!W
    private static final float NANOS_TO_SECONDS = 1/1000000000.0f;

    private volatile boolean _isRunning = false;
    private volatile boolean _gameOver = false;

    private Thread _gameThread = null;
    private Canvas _canvas = null;
    private SurfaceHolder _holder = null;
    private Paint _paint = null;
    private Config _config = null;
    private long _lastTime = 0;
    public BitmapPool _bitmapPool = null;
    private SoundPlayer _soundPlayer = null;
    private QuadTree _quadTree = null;

    private LevelManager _levelManager = null;
    private Matrix _transform = new Matrix();
    private InputManager _controls = new InputManager(); //A valid null-controller.
    private ViewPort _camera = null;
    private Hud _hud = null;
    private MusicPlayer _music = null;
    private int _currentLevel = 1;
    private ArrayList<Entity> _returnEntities = new ArrayList<>();

    public enum GameEvent {
        LevelCompleted,
        Jump,
        CoinPickup,
        PlayerDied,
        PlayerDamaged
    }


    public Game(Context context)
    {
        super(context);
        Init(context);
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public Game(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Init(context);
    }

    public void Init(Context context) {
        _config = new Config(context);
        _holder = getHolder();
        _holder.addCallback(this);
        _holder.setFixedSize(_config.STAGE_WIDTH, _config.STAGE_HEIGHT);
        _paint = new Paint();
        _camera = new ViewPort(1280, 720, METERS_TO_SHOW_X, METERS_TO_SHOW_Y, this);
        Entity._game = this;
        _bitmapPool = new BitmapPool(this);

        loadCurrentLevel();
        _levelManager = new LevelManager(new LevelData(getContext(), _currentLevel), this);
        _hud = new Hud();

        _music = new MusicPlayer(context);
        _music.playMusic(_currentLevel);

        RectF bounds = new RectF();
        bounds.left = 0;
        bounds.right = getLevelWidth();
        bounds.bottom = getLevelHeight();
        bounds.top = 0;
        _camera.setBounds(bounds);
        _soundPlayer = new SoundPlayer(getContext());
        Rectangle rect = new Rectangle();
        rect.setBounds(0, 0, getLevelWidth(), getLevelHeight());
        _quadTree = new QuadTree(0, rect);
        Log.d(TAG, "Game created!");
    }

    public void onGameEvent(GameEvent gameEvent, Entity e) {
        Log.d("GameEvent: " + gameEvent, TAG);
        _soundPlayer.playSoundForGameEvent(gameEvent);
    }

    @Override
    public void run() {
        while (_isRunning) {

            long t = System.nanoTime();
            float dt = ((t - _lastTime) * NANOS_TO_SECONDS);
            _lastTime = t;

            update(dt);
            render(_camera);
        }
    }

    private void update(float dt) {
        if(_gameOver) {
            restart();
            return;
        }

        _camera.lookAt(_levelManager._player);

        for (Entity e: _levelManager.GetEntities()) {
            e.update(dt);
        }
        _levelManager.addAndRemoveEntities();
        checkCollisions();
        checkGameOver();
        checkCoinsCollected();
    }

    private void checkCoinsCollected() {
        if(_levelManager.getCoinsLeft() <= 0) {
            nextLevel();
        }
    }

    private void nextLevel() {
        _currentLevel += 1;
        if(_currentLevel > _levelManager.lastLevel()) {
            _currentLevel = 1;
        }
        _levelManager.destroy();
        clearSave();
        _levelManager = new LevelManager(new LevelData(this.getContext(), _currentLevel), this);
        onGameEvent(GameEvent.LevelCompleted, null);
        _music.playMusic(_currentLevel);
    }

    final static Point _pos = new Point();
    private void render(ViewPort camera) {
        if(_gameOver) {
            return;
        }
        //acquire and lock the canvas
        if (!acquireAndLockCanvas()) {
            return;
        }
        //clear the canvas

        try{
            _canvas.drawColor(BACKGROUND_COLOR); //clear the screen
            for (Entity e : _levelManager.GetEntities()) {
                _transform.reset();
                camera.worldToScreen(e, _pos);
                _transform.postTranslate(_pos.x, _pos.y);
                e.render(_canvas, _transform, _paint);
                _hud.render(_canvas, _paint, _levelManager._player, _levelManager.getCoinsLeft(), this.getContext());
                _quadTree.render(_canvas, _paint, _camera);
            }
        } finally {
            //unlock the canvas and post to the UI thread
            _holder.unlockCanvasAndPost(_canvas); //post to UI thread
        }
    }

    private boolean acquireAndLockCanvas() {
        if(!_holder.getSurface().isValid()){
            return false;
        }
        _canvas = _holder.lockCanvas();
        return (_canvas != null);
    }

    public PointF worldToScreen(final float width, final float height) {
        return new PointF(width * _camera.getPixelsPerMeterX(), height * _camera.getPixelsPerMeterY());
    }

    public Point worldToScreen(final int width, final int height) {
        return new Point((int)width * _camera.getPixelsPerMeterX(), (int)height * _camera.getPixelsPerMeterY());
    }

    private void checkGameOver() {
        if(_levelManager._player.isDead()) {
            _gameOver = true;
            clearSave();
            onGameEvent(GameEvent.PlayerDied, null);
        }
    }

    private void restart() {
        _levelManager.destroy();
        _levelManager = new LevelManager(new LevelData(this.getContext(), _currentLevel), this);
        clearSave();
        _gameOver = false;
    }

    private void clearSave() {
        SharedPreferences prefs = getContext().getSharedPreferences(getContext().getString(R.string.saved_state), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    private void checkCollisions() {
        _quadTree.clear();
        ArrayList<Entity> entities = _levelManager.GetEntities();
        for (int i = 0; i < entities.size(); i++) {
            _quadTree.insert(entities.get(i));
        }

        for(Entity ent : _levelManager.GetEntities()) {
            if(ent._entityType == Entity.EntityType.TERRAIN) {
                continue;
            }
            _returnEntities.clear();
            _quadTree.retrieve(_returnEntities, ent);
            for(Entity e : _returnEntities) {
                if(e == ent) {
                    continue;
                }
                if(ent.isColliding(e)) {
                    ent.onCollision(e);
                    e.onCollision(ent);
                }
            }
        }
    }

    /*All methods below this line are executing on the system UI thread!*/

    protected void onResume() {
        Log.d(TAG, "onResume");
        _controls.onResume();
        _music.start();
    }

    protected void onPause() {
        Log.d(TAG, "onPause");

        _controls.onPause();
        _music.pause();

        _isRunning = false;
        while(true) {
            try {
                _gameThread.join();
                return;
            } catch (InterruptedException e) {
                Log.d(TAG, Log.getStackTraceString(e.getCause()));
            }
        }
    }

    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        _gameThread = null;
        _controls = null;
        if(_music != null) {
            _music.destroy();
            _music = null;
        }

        if(_soundPlayer != null) {
            _soundPlayer.destroy();
            _soundPlayer = null;
        }

        if(_levelManager != null) {
            _levelManager.destroy();
            _levelManager = null;
        }
        if(_bitmapPool != null) {
            _bitmapPool.empty();
        }
        _bitmapPool = null;

        Entity._game = null;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d("SurfaceCreated", TAG);
        _gameThread = new Thread(this);
        _isRunning = true;
    }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {
        Log.d("SurfaceChanged", TAG);
        if(_gameThread != null && _isRunning) {
            Log.d("Game thread started", TAG);
            _gameThread.start();
        }

        for(Entity e : _levelManager.GetEntities()) {
            e.loadGameState(getContext());
        }
        loadCurrentLevel();
    }
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.d("SurfaceDestroyed", TAG);

        ArrayList<Entity> entities = _levelManager.GetEntities();
        for(int i = 0; i < entities.size(); i++) {
            entities.get(i).saveGameState(getContext());
        }
        saveCurrentLevel();
    }

    private void saveCurrentLevel() {
        SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.saved_state), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("currentLevel", _currentLevel);
        editor.apply();
    }

    private void loadCurrentLevel() {
        SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.saved_state), Context.MODE_PRIVATE);
        _currentLevel = sharedPref.getInt("currentLevel", 1);
    }

    public Config getConfig() {
        return _config;
    }

    public void setControls(final InputManager control){
        _controls.onPause(); //give the previous controler
        _controls.onStop(); //a chance to clean up
        _controls = control;
        _controls.onStart();
    }
    public InputManager getControls(){
        return _controls;
    }

    public int getLevelHeight() {
        return _levelManager.GetHeight();
    }

    public int getLevelWidth() {
        return _levelManager.GetWidth();
    }

    public void removeEntity(Entity that) {
        _levelManager.removeEntity(that);

    }
}
