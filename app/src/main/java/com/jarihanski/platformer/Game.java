package com.jarihanski.platformer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;


public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    public static final String TAG = "Game";
    private static int BACKGROUND_COLOR = Color.argb(255, 135, 206, 235);
    private final MainActivity _activity;

    private volatile boolean _isRunning = false;
    private volatile boolean _gameOver = false;

    private Thread _gameThread = null;
    private Canvas _canvas = null;
    private SurfaceHolder _holder = null;
    private Paint _paint = null;
    private Config _config = null;
    private long _lastTime = 0;

    private LevelManager _levelManager = null;
    private Matrix _transform = new Matrix();

    ViewPort _camera = null;
    private static final float METERS_TO_SHOW_X = 0f; //set the value you want fixed
    private static final float METERS_TO_SHOW_Y = 9f;  //the other is calculated at runtime!

    public Game(Context context) {
        super(context);
        _config = new Config(context);
        _activity = (MainActivity) context;
        _holder = getHolder();
        _holder.addCallback(this);
        _holder.setFixedSize(_config.STAGE_WIDTH, _config.STAGE_HEIGHT);
        _paint = new Paint();
        Entity._game = this;

        _levelManager = new LevelManager(new TestLevel());
        _camera = new ViewPort(1280, 720, METERS_TO_SHOW_X, METERS_TO_SHOW_Y);

        Log.d(TAG, "Game created!");
    }

    @Override
    public void run() {
        while (_isRunning) {

            long t = System.nanoTime();
            float dt = (int) ((t - _lastTime) / 1000000);
            dt /= 1000f;
            _lastTime = t;

            update(dt);
            render();
        }
    }

    private void update(float dt) {
        if(_gameOver) {
            return;
        }

        for (Entity e: _levelManager.GetEntities()) {
            e.update(dt);
        }
        _levelManager.addAndRemoveEntities();
        checkCollisions();
        checkGameOver();
    }

    private void render() {
        //acquire and lock the canvas
        if (!acquireAndLockCanvas()) {
            return;
        }
        //clear the canvas

        try{
            _canvas.drawColor(BACKGROUND_COLOR); //clear the screen
            for (Entity e : _levelManager.GetEntities()) {
                _transform.reset();
                Utils.Vec2 _pos = Utils.worldToScreen(e._x, e._y);
                _transform.postTranslate(_pos._x, _pos._y);
                e.render(_canvas, _transform, _paint);
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

    private void checkGameOver() {

    }

    private void checkCollisions() {
    }

    /*All methods below this line are executing on the system UI thread!*/

    protected void onResume() {
        Log.d(TAG, "onResume");
    }

    protected void onPause() {
        Log.d(TAG, "onPause");

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

        if(_levelManager != null) {
            _levelManager.destroy();
            _levelManager = null;
        }

        Entity._game = null;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.d("SurfaceCreated", TAG);
        _gameThread = new Thread(this);
        _gameThread.start();
        _isRunning = true;
    }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {
        Log.d("SurfaceChanged", TAG);
        if(_gameThread != null && _isRunning) {
            Log.d("Game thread started", TAG);
           // _gameThread.start();
        }
    }
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.d("SurfaceDestroyed", TAG);
    }
}
