package com.example.teisko.dogmemo;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnTouchListener;

import static android.R.attr.padding;
import static com.example.teisko.dogmemo.R.layout.activity_main;

// tutki https://stackoverflow.com/questions/5123407/losing-data-when-rotate-screen

public class MainActivity extends AppCompatActivity {

    private TextView numberOfTouches;

    private TextView ballShape1;
    private TextView ballShape2;
    private TextView ballShape3;
    private TextView ballShape4;
    private TextView ballShape5;
    private TextView ballShape6;
    private TextView touchNumber;

    LinearLayout layout;
    LinearLayout.LayoutParams params;

    int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(activity_main);*/

        /*numberOfTouches = (TextView) findViewById(R.id.numberOfTouches);
        numberOfTouches.setVisibility(View.VISIBLE);*/

        layout = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.0F);
        setContentView(layout);

        System.out.println("asd1");
        drawMoreSpots(2);
    }

    public void ballShapeOnClick(View view) {
        //numberOfTouches.setVisibility(View.VISIBLE);
        //number = Integer.parseInt(numberOfTouches.getText().toString()) + 1;
        //numberOfTouches.setText("" + number);
        System.out.println("asd123");
        number = Integer.parseInt(touchNumber.getText().toString()) + 1;
        touchNumber.setText("" + number);
        System.out.println("asd321");
    }

    public void drawMoreSpots(int amount) {
        touchNumber = new TextView(this);
        touchNumber.setText("0");
        layout.addView(touchNumber);

        // draw 1. ballspot
        if (amount >= 1) {
            System.out.println("drawing 1. ball");
            ballShape1 = new TextView(this);
            ballShape1.setId(1);
            ballShape1.setBackgroundResource(R.drawable.ball_shape);
            layout.addView(ballShape1);
            addBallShapeListener(1);
            System.out.println("drawing 1. ball DONE");
        }
        if (amount >= 2) {
            System.out.println("drawing 2. ball");
            ballShape2 = new TextView(this);
            ballShape2.setId(2);
            ballShape2.setBackgroundResource(R.drawable.ball_shape);
            ballShape2.getBackground().setColorFilter(Color.parseColor("#00ff00"), PorterDuff.Mode.DARKEN);
            layout.addView(ballShape2);
            addBallShapeListener(2);
            System.out.println("drawing 2. ball DONE");
        }
    }

    public void addBallShapeListener(int index) {
        if (index >= 1) {
            ballShape1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ballShapeOnClick(findViewById(ballShape1.getId()));
                    }
                    /*else if (event.getAction() == MotionEvent.ACTION_UP) {
                    }*/
                    return false;
                }
            });
        }
        if (index >= 2) {
            ballShape2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        ballShapeOnClick(findViewById(ballShape2.getId()));
                    }
                    /*else if (event.getAction() == MotionEvent.ACTION_UP) {
                    }*/
                    return false;
                }
            });
        }
    }
}
