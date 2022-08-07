package com.upv.pm_2022.a04_esfera;

/*
https://github.com/GabeK0/Android-OpenGL-Sphere
Modificaciones Menores, Noviembre 2021
 */

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import android.graphics.Point;
import android.opengl.GLSurfaceView;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
public class MainActivity extends AppCompatActivity {


    private MyGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);


        mGLSurfaceView = new MyGLSurfaceView(this, size.x, size.y);

        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        mGLSurfaceView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mGLSurfaceView.onPause();
        super.onPause();
    }
}