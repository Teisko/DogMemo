package com.example.teisko.dogmemo;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import static com.example.teisko.dogmemo.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mainActivity;
    private TextView numberOfTouches;
    private TextView ballShape1;
    private TextView ballShape2;
    private TextView ballShape3;
    private TextView ballShape4;
    private TextView ballShape5;
    private TextView ballShape6;
    private TextView ballShape7;
    private TextView ballShape8;
    int number = 0;
    int width;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(activity_main);

        // muuttujia
        mainActivity = (ConstraintLayout) findViewById(R.id.mainactivity);
        numberOfTouches = (TextView) findViewById(R.id.numberOfTouches);
        ballShape1 = (TextView) findViewById(R.id.ballShape);

        // Näytön koko pikseleinä
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        // Asettaa pallon keskelle näyttöä pelin alkaessa näytön piirron jälkeen
        ballShape1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ballShape1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Pallo keskelle
                ballShape1.setX(width/2 - ballShape1.getWidth()/2);
                ballShape1.setY(height/2 - ballShape1.getHeight()/2);
            }
        });

        // Pallon kosketuksen kuuntelija
        ballShape1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ballShapeOnClick();
                }
                return false;
            }
        });
    }

    // Kutsutaan kun palloa kosketetaan
    public void ballShapeOnClick() {
        // Aluksi pallo on keskellä 300dp kokoisena
        // pienennä pallo 200dp kokoiseksi
        if (number == 0) {
            // muuta pallon kokoa
            DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
            int px = Math.round(200 * (dm.densityDpi / 160f));
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ballShape1.getLayoutParams();
            params.width = px;
            params.height = px;
            ballShape1.setLayoutParams(params);

            // Asettaa pallon keskelle näyttöä koon muuttamisen jälkeen
            ballShape1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ballShape1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    // Pallo keskelle
                    ballShape1.setX(width/2 - ballShape1.getWidth()/2);
                    ballShape1.setY(height/2 - ballShape1.getHeight()/2);
                }
            });
        }

        // pienennä pallo 120dp kokoiseksi
        if (number == 1) {
            // muuta pallon kokoa
            DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
            int px = Math.round(120 * (dm.densityDpi / 160f));
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ballShape1.getLayoutParams();
            params.width = px;
            params.height = px;
            ballShape1.setLayoutParams(params);

            // Asettaa pallon keskelle näyttöä koon muuttamisen jälkeen
            ballShape1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ballShape1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    // Pallo keskelle
                    ballShape1.setX(width/2 - ballShape1.getWidth()/2);
                    ballShape1.setY(height/2 - ballShape1.getHeight()/2);
                }
            });
        }

        // alavasen kulma
        if (number == 2) {
            ballShape1.setX(0);
            ballShape1.setY(height - ballShape1.getHeight());
        }

        // ylävasen kulma
        if (number == 3) {
            ballShape1.setX(0);
            ballShape1.setY(0);
        }

        // yläoikea kulma
        if (number == 4) {
            ballShape1.setX(width - ballShape1.getWidth());
            ballShape1.setY(0);
        }

        // alaoikea kulma
        if (number == 5) {
            ballShape1.setX(width - ballShape1.getWidth());
            ballShape1.setY(height - ballShape1.getHeight());
        }

        // peitetään pallo keskellä näyttöä
        if (number == 6) {
            // siirretään pallo keskelle
            ballShape1.setX(width/2 - ballShape1.getWidth()/2);
            ballShape1.setY(height/2 - ballShape1.getHeight()/2);

            // Animoi peitteet
            doTransition(1);
        }

        // 2 peitettä
        if (number >= 7 && number <= 11) { // 7-11
            if (number == 7) {
                // lisätään 2. peite
                ballShape2 = new TextView(this);
                mainActivity.addView(ballShape2);

                // liikutetaan 1. peite
                ballShape1.setX(width/2 - ballShape1.getWidth()/2 - width/4);
                ballShape1.setY(height/2 - ballShape1.getHeight()/2);
                // liikutetaan 2. peite
                ballShape2.setX(width/2 - ballShape1.getWidth()/2 + width/4);
                ballShape2.setY(height/2 - ballShape1.getHeight()/2);
            }
            // Arpoo pallon paikan
            randomSpot(2);
            // Asettaa pallon ja tyhjät
            setBackground(2);
            // Animoi peitteet
            doTransition(2);
        }

        // 3 peitettä
        if (number >= 12 && number <= 16) { // 12-16
            if (number == 12) {
                // lisätään 3. peite
                ballShape3 = new TextView(this);
                mainActivity.addView(ballShape3);

                // liikutetaan 1. peite
                ballShape1.setX(width/4 - ballShape1.getWidth()/2);
                ballShape1.setY(height/2 - ballShape1.getHeight()/2);
                // liikutetaan 2. peite
                ballShape2.setX(width/2 - ballShape1.getWidth()/2);
                ballShape2.setY(height/2 - ballShape1.getHeight()/2);
                // liikutetaan 3. peite
                ballShape3.setX(width*3/4 - ballShape1.getWidth()/2);
                ballShape3.setY(height/2 - ballShape1.getHeight()/2);
            }
            // Arpoo pallon paikan
            randomSpot(3);
            // Asettaa pallon ja tyhjät
            setBackground(3);
            // Animoi peitteet
            doTransition(3);
        }

        // 4 peitettä
        if (number >= 17 && number <= 21) { // 17-21
            if (number == 17) {
                // lisätään 4. peite
                ballShape4 = new TextView(this);
                mainActivity.addView(ballShape4);

                // liikutetaan 1. peite
                ballShape1.setX(width*1/4 - ballShape1.getWidth()/2);
                ballShape1.setY(height*1/4 - ballShape1.getHeight()/2);
                // liikutetaan 2. peite
                ballShape2.setX(width*3/4 - ballShape1.getWidth()/2);
                ballShape2.setY(height*1/4 - ballShape1.getHeight()/2);
                // liikutetaan 3. peite
                ballShape3.setX(width*1/4 - ballShape1.getWidth()/2);
                ballShape3.setY(height*3/4 - ballShape1.getHeight()/2);
                // liikutetaan 4. peite
                ballShape4.setX(width*3/4 - ballShape1.getWidth()/2);
                ballShape4.setY(height*3/4 - ballShape1.getHeight()/2);
            }
            // Arpoo pallon paikan
            randomSpot(4);
            // Asettaa pallon ja tyhjät
            setBackground(4);
            // Animoi peitteet
            doTransition(4);
        }

        // 5 peitettä
        if (number >= 22 && number <= 26) { // 22-26
            if (number == 22) {
                // lisätään 5. peite
                ballShape5 = new TextView(this);
                mainActivity.addView(ballShape5);

                // liikutetaan 1. peite
                ballShape1.setX(width/4 - ballShape1.getWidth()/2);
                ballShape1.setY(height/4 - ballShape1.getHeight()/2);
                // liikutetaan 2. peite
                ballShape2.setX(width*3/4 - ballShape1.getWidth()/2);
                ballShape2.setY(height/4 - ballShape1.getHeight()/2);
                // liikutetaan 3. peite
                ballShape3.setX(width/4 - ballShape1.getWidth()/2);
                ballShape3.setY(height*3/4 - ballShape1.getHeight()/2);
                // liikutetaan 4. peite
                ballShape4.setX(width*3/4 - ballShape1.getWidth()/2);
                ballShape4.setY(height*3/4 - ballShape1.getHeight()/2);
                // liikutetaan 5. peite
                ballShape5.setX(width/2 - ballShape1.getWidth()/2);
                ballShape5.setY(height/2 - ballShape1.getHeight()/2);
            }
            // Arpoo pallon paikan
            randomSpot(5);
            // Asettaa pallon ja tyhjät
            setBackground(5);
            // Animoi peitteet
            doTransition(5);
        }

        // 6 peitettä
        if (number >= 27 /*&& number <= 31*/) { // 27-31
            if (number == 27) {
                // lisätään 6. peite
                ballShape6 = new TextView(this);
                mainActivity.addView(ballShape6);

                // liikutetaan 1. peite
                ballShape1.setX(width/4 - ballShape1.getWidth()/2);
                ballShape1.setY(height/4 - ballShape1.getHeight()/2);
                // liikutetaan 2. peite
                ballShape2.setX(width/2 - ballShape1.getWidth()/2);
                ballShape2.setY(height/4 - ballShape1.getHeight()/2);
                // liikutetaan 3. peite
                ballShape3.setX(width*3/4 - ballShape1.getWidth()/2);
                ballShape3.setY(height/4 - ballShape1.getHeight()/2);
                // liikutetaan 4. peite
                ballShape4.setX(width/4 - ballShape1.getWidth()/2);
                ballShape4.setY(height*3/4 - ballShape1.getHeight()/2);
                // liikutetaan 5. peite
                ballShape5.setX(width/2 - ballShape1.getWidth()/2);
                ballShape5.setY(height*3/4 - ballShape1.getHeight()/2);
                // liikutetaan 6. peite
                ballShape6.setX(width*3/4 - ballShape1.getWidth()/2);
                ballShape6.setY(height*3/4 - ballShape1.getHeight()/2);
            }
            // Arpoo pallon paikan
            randomSpot(6);
            // Asettaa pallon ja tyhjät
            setBackground(6);
            // Animoi peitteet
            doTransition(6);
        }

        // Kosketuslaskuri
        number++;
        numberOfTouches.setText("" + number);
    }

    // Arpoo pallon paikan, n = paikkojen lkm
    public void randomSpot(int n) {
        double random = Math.floor(Math.random()*n);
        float x1 = ballShape1.getX();
        float y1 = ballShape1.getY();
        if (random == 1) {
            ballShape1.setX(ballShape2.getX());
            ballShape1.setY(ballShape2.getY());
            ballShape2.setX(x1);
            ballShape2.setY(y1);
        }
        if (random == 2) {
            ballShape1.setX(ballShape3.getX());
            ballShape1.setY(ballShape3.getY());
            ballShape3.setX(x1);
            ballShape3.setY(y1);
        }
        if (random == 3) {
            ballShape1.setX(ballShape4.getX());
            ballShape1.setY(ballShape4.getY());
            ballShape4.setX(x1);
            ballShape4.setY(y1);
        }
        if (random == 4) {
            ballShape1.setX(ballShape5.getX());
            ballShape1.setY(ballShape5.getY());
            ballShape5.setX(x1);
            ballShape5.setY(y1);
        }
        if (random == 5) {
            ballShape1.setX(ballShape6.getX());
            ballShape1.setY(ballShape6.getY());
            ballShape6.setX(x1);
            ballShape6.setY(y1);
        }
    }

    // Asettaa pallon ja tyhjät, n = paikkojen lkm
    public void setBackground(int n) {
        if (n >= 1) {
            ballShape1.setBackgroundResource(R.drawable.ball_shape);
        }
        if (n >= 2) {
            ballShape2.setBackgroundResource(R.drawable.transparent);
        }
        if (n >= 3) {
            ballShape3.setBackgroundResource(R.drawable.transparent);
        }
        if (n >= 4) {
            ballShape4.setBackgroundResource(R.drawable.transparent);
        }
        if (n >= 5) {
            ballShape5.setBackgroundResource(R.drawable.transparent);
        }
        if (n >= 6) {
            ballShape6.setBackgroundResource(R.drawable.transparent);
        }
    }

    // Animoi peitteen pallon ja tyhjien tilalle, n = paikkojen lkm
    public void doTransition(int n) {
        int length = 1000;
        if (n >= 1) {
            ballShape1.setBackgroundResource(R.drawable.ball_to_square);
            TransitionDrawable transition = (TransitionDrawable) ballShape1.getBackground();
            transition.startTransition(length);
        }
        if (n >= 2) {
            ballShape2.setBackgroundResource(R.drawable.none_to_square);
            TransitionDrawable transition2 = (TransitionDrawable) ballShape2.getBackground();
            transition2.startTransition(length);
        }
        if (n >= 3) {
            ballShape3.setBackgroundResource(R.drawable.none_to_square);
            TransitionDrawable transition3 = (TransitionDrawable) ballShape3.getBackground();
            transition3.startTransition(length);
        }
        if (n >= 4) {
            ballShape4.setBackgroundResource(R.drawable.none_to_square);
            TransitionDrawable transition4 = (TransitionDrawable) ballShape4.getBackground();
            transition4.startTransition(length);
        }
        if (n >= 5) {
            ballShape5.setBackgroundResource(R.drawable.none_to_square);
            TransitionDrawable transition5 = (TransitionDrawable) ballShape5.getBackground();
            transition5.startTransition(length);
        }
        if (n >= 6) {
            ballShape6.setBackgroundResource(R.drawable.none_to_square);
            TransitionDrawable transition6 = (TransitionDrawable) ballShape6.getBackground();
            transition6.startTransition(length);
        }

        // katko kosketuksen tunnistukseen
        ballShape1.setEnabled(false);
        Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ballShape1.setEnabled(true);
            }
        }, length);
    }

    public void gameOver() {

    }
}
