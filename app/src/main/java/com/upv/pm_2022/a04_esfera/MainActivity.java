package com.upv.pm_2022.a04_esfera;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Point;
import android.view.Display;

/**
 * https://github.com/GabeK0/Android-OpenGL-Sphere
 * Modificaciones Menores, Noviembre 2021
 */
public class MainActivity extends AppCompatActivity {
    private MyGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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