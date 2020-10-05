package com.jarihanski.platformer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends AppCompatActivity {
    private Game _game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _game = new Game(this); //this == GameActivity == a Context'
        setContentView(_game);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _game.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _game.onPause();
        isFinishing();
    }

    @Override
    protected void onDestroy() {
        _game.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            return;
        }
        Window window = getWindow();
        window.setDecorFitsSystemWindows(false);
    }
}