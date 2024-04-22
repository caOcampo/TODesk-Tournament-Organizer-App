package com.example.todeskapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class BracketLayout extends LinearLayout {

    public BracketLayout(Context context) {
        super(context);
        init();
    }

    public BracketLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BracketLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.bracket_layout, this);
    }
}
