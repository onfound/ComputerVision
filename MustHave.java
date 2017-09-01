package com.example.ilyad.opencvtest;


import android.view.MotionEvent;
import android.view.View;

public class MustHave implements View.OnTouchListener {
    float x;
    float y;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction()== MotionEvent.ACTION_DOWN){
            x = motionEvent.getX();
            y =motionEvent.getY();
        }
        return false;
    }
}
