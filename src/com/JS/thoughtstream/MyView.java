package com.JS.thoughtstream;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MyView extends TextView {

    GestureDetector gestureDetector;
    private EditText aEditText;

    public MyView(Context context, AttributeSet attrs, EditText pEditText) {
        super(context, attrs);
        aEditText = pEditText;
        // creating new gesture detector
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    // skipping measure calculation and drawing

    // delegate the event to the gesture detector
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }
}