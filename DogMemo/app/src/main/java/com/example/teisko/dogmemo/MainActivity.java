package com.example.teisko.dogmemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

// tutki https://stackoverflow.com/questions/5123407/losing-data-when-rotate-screen

public class MainActivity extends AppCompatActivity {

    private TextView numberOfTouches;
    int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_main);

        numberOfTouches = (TextView) findViewById(R.id.numberOfTouches);
        numberOfTouches.setVisibility(View.INVISIBLE);

        /*TextView ballShape = (TextView) findViewById(R.id.ballShape);
        ballShape.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ballShapeOnClick();
                    //Log.d("Pressed", "Button pressed");
                }
                /*else if (event.getAction() == MotionEvent.ACTION_UP)
                    Log.d("Released", "Button released");
                return false;
            }
        });*/
    }

    public void ballShapeOnClick(View view) {
        numberOfTouches.setVisibility(View.VISIBLE);
        number = Integer.parseInt(numberOfTouches.getText().toString()) + 1;
        numberOfTouches.setText("" + number);
    }
}
