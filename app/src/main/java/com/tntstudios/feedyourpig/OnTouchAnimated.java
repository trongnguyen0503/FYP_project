package com.tntstudios.feedyourpig;

import android.view.MotionEvent;
import android.view.View;

import com.tntstudios.feedyourpig.gameplay.OnSwipeTouchListener;

public class OnTouchAnimated implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
            view.setAlpha(0.5f);
        }
        if(motionEvent.getAction()==MotionEvent.ACTION_UP)
            view.setAlpha(1f);
        return false;
    }
}
