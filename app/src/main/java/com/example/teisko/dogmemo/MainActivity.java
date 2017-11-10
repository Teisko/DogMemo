package com.example.teisko.dogmemo;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.example.teisko.dogmemo.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mainActivity;
    private TextView numberOfTouches;               // näyttää aikalaskurin
    private TextView ballShape1;                    // pallo / peite jonka alla pallo
    private TextView ballShape2;                    // ballShape2-6 = tyhjä / peite jonka alla tyhjä
    private TextView ballShape3;
    private TextView ballShape4;
    private TextView ballShape5;
    private TextView ballShape6;

    final String RED = "#ff0000";                   // Vakioita väreille
    final String BLUE = "#0000ff";
    final String GREEN = "#00ff00";
    final String PREF_FILE_NAME = "PrefFile";       // Tiedosto, johon asetukset tallennetaan ja josta ne haetaan

    String ballColor = RED;                         // pallon väri, haetaan asetuksista
    String squareColor = BLUE;                      // peitteiden väri, haetaan asetuksista
    int ballBackground;                             // pallon Drawable kuvio
    int ballToSquare;                               // pallosta peitteeseen animaatio TransitionDrawable
    int transpToSquare;                             // tyhjästä peitteeseen animaatio TransitionDrawable
    // Oikeat painallukset & kaikki painallukset
    int number = 0;                                 // oikeiden painallusten määrä
    int allTouches = 0;                             // kaikkien painallusten määrä
    // Pelin kesto sekunteina
    int gameTime = 60;                              // pelin kesto sekunteina, haetaan asetuksista
    int ballVisibleTime = 2000;                     // pallon näkyvissäolon kesto ennen kuin se peitetään millisekunteina, haetaan asetuksista
    int transitionLength = 2000;                    // peiteanimaation kesto millisekunteina, haetaan asetuksista
    int width;                                      // näytön leveys pikseleinä
    int height;                                     // näytön korkeus pikseleinä

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // lukitse näytön kääntö, piilota otsikko ja tehtäväpalkki
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(activity_main);

        // muuttujia
        mainActivity = (ConstraintLayout) findViewById(R.id.mainactivity);
        numberOfTouches = (TextView) findViewById(R.id.numberOfTouches);
        ballShape1 = (TextView) findViewById(R.id.ballShape);
        ballShape2 = new TextView(this);
        ballShape3 = new TextView(this);
        ballShape4 = new TextView(this);
        ballShape5 = new TextView(this);
        ballShape6 = new TextView(this);

        // hakee tallennetut asetukset, jos ne ovat olemassa
        SharedPreferences sharedPref = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        if (!sharedPref.getString("ball_color_list", "").equals(""))
            ballColor = sharedPref.getString("ball_color_list", "");
        if (!sharedPref.getString("square_color_list", "").equals(""))
            squareColor = sharedPref.getString("square_color_list", "");
        if (!sharedPref.getString("gametime_list", "").equals(""))
            gameTime = Integer.parseInt(sharedPref.getString("gametime_list", ""));
        if (!sharedPref.getString("ball_visible_time_list", "").equals(""))
            ballVisibleTime = Integer.parseInt(sharedPref.getString("ball_visible_time_list", ""));
        if (!sharedPref.getString("cover_animation_time_list", "").equals(""))
            transitionLength = Integer.parseInt(sharedPref.getString("cover_animation_time_list", ""));
        // asettaa haetut värit
        setColors();

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

        // Kaikkien kosketusten kuuntelija
        mainActivity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    allTouches++;
                }
                return false;
            }
        });

        // Peliaikalaskuri, peli loppuu kun aika loppuu
        // muuta peliaika mm:ss formaattiin
        Date d = new Date(gameTime * 1000L);
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String time = df.format(d);
        numberOfTouches.setText(time);
        // laske aikaa yhdellä sekuntin välein
        final Handler timeTickHandler = new Handler();
        // 1 sekunti
        final int delay = 1000;
        timeTickHandler.postDelayed(new Runnable() {
            public void run() {
                gameTime = gameTime-1;
                // muuta peliaika mm:ss formaattiin
                Date d = new Date(gameTime * 1000L);
                SimpleDateFormat df = new SimpleDateFormat("mm:ss");
                df.setTimeZone(TimeZone.getTimeZone("GMT"));
                String time = df.format(d);
                numberOfTouches.setText(time);
                if (gameTime == 0) {
                    timeTickHandler.removeCallbacks(this);
                    gameOver();
                }
                timeTickHandler.postDelayed(this, delay);
            }
        }, delay);
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
            // Asetetaan transparent kuvio
            ballShape2.setBackgroundResource(R.drawable.transparent);
            // Animoi peitteet
            doTransition(2);
        }

        // 3 peitettä
        if (number >= 12 && number <= 16) { // 12-16
            if (number == 12) {
                // lisätään 3. peite
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
            // Asetetaan transparent kuvio
            ballShape3.setBackgroundResource(R.drawable.transparent);
            // Animoi peitteet
            doTransition(3);
        }

        // 4 peitettä
        if (number >= 17 && number <= 21) { // 17-21
            if (number == 17) {
                // lisätään 4. peite
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
            // Asetetaan transparent kuvio
            ballShape4.setBackgroundResource(R.drawable.transparent);
            // Animoi peitteet
            doTransition(4);
        }

        // 5 peitettä
        if (number >= 22 && number <= 26) { // 22-26
            if (number == 22) {
                // lisätään 5. peite
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
            // Asetetaan transparent kuvio
            ballShape5.setBackgroundResource(R.drawable.transparent);
            // Animoi peitteet
            doTransition(5);
        }

        // 6 peitettä
        if (number >= 27 /*&& number <= 31*/) { // 27-31
            if (number == 27) {
                // lisätään 6. peite
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
            // Asetetaan transparent kuvio
            ballShape6.setBackgroundResource(R.drawable.transparent);
            // Animoi peitteet
            doTransition(6);
        }

        // Kosketuslaskuri
        number++;
        //numberOfTouches.setText("" + number);
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

    // Animoi peitteen pallon ja tyhjien tilalle, n = paikkojen lkm
    public void doTransition(int m) {
        final int n = m;

        // asetetaan pallon ja tyhjien kuviot
        if (n >= 1)
            ballShape1.setBackgroundResource(ballBackground);
        if (n >= 2)
            ballShape2.setBackgroundResource(R.drawable.transparent);
        if (n >= 3)
            ballShape3.setBackgroundResource(R.drawable.transparent);
        if (n >= 4)
            ballShape4.setBackgroundResource(R.drawable.transparent);
        if (n >= 5)
            ballShape5.setBackgroundResource(R.drawable.transparent);
        if (n >= 6)
            ballShape6.setBackgroundResource(R.drawable.transparent);

        // katko pallon kosketuksen tunnistukseen kunnes pallo on peitetty
        ballShape1.setEnabled(false);
        Handler touchHandler = new android.os.Handler();
        touchHandler.postDelayed(new Runnable() {
            public void run() {
                ballShape1.setEnabled(true);
            }
        }, ballVisibleTime + transitionLength);

        // Pallo näkyy ballVisibleTime ajan, jonka jälkeen se peitetään
        // peiteanimaatio kestää transitionLength ajan
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            // tämä suoritetaan viiveen ballVisibleTime jälkeen
            public void run() {
                if (n >= 1) {
                    ballShape1.setBackgroundResource(ballToSquare);
                    TransitionDrawable transition = (TransitionDrawable) ballShape1.getBackground();
                    transition.startTransition(transitionLength);
                }
                if (n >= 2) {
                    ballShape2.setBackgroundResource(transpToSquare);
                    TransitionDrawable transition2 = (TransitionDrawable) ballShape2.getBackground();
                    transition2.startTransition(transitionLength);
                }
                if (n >= 3) {
                    ballShape3.setBackgroundResource(transpToSquare);
                    TransitionDrawable transition3 = (TransitionDrawable) ballShape3.getBackground();
                    transition3.startTransition(transitionLength);
                }
                if (n >= 4) {
                    ballShape4.setBackgroundResource(transpToSquare);
                    TransitionDrawable transition4 = (TransitionDrawable) ballShape4.getBackground();
                    transition4.startTransition(transitionLength);
                }
                if (n >= 5) {
                    ballShape5.setBackgroundResource(transpToSquare);
                    TransitionDrawable transition5 = (TransitionDrawable) ballShape5.getBackground();
                    transition5.startTransition(transitionLength);
                }
                if (n >= 6) {
                    ballShape6.setBackgroundResource(transpToSquare);
                    TransitionDrawable transition6 = (TransitionDrawable) ballShape6.getBackground();
                    transition6.startTransition(transitionLength);
                }
            }
        }, ballVisibleTime);
    }

    public void gameOver() {
        // poistetaan kaikki elementit näkyvistä
        if (ballShape1 != null)
            ballShape1.setVisibility(View.GONE);
        if (ballShape2 != null)
            ballShape2.setVisibility(View.GONE);
        if (ballShape3 != null)
            ballShape3.setVisibility(View.GONE);
        if (ballShape4 != null)
            ballShape4.setVisibility(View.GONE);
        if (ballShape5 != null)
            ballShape5.setVisibility(View.GONE);
        if (ballShape6 != null)
            ballShape6.setVisibility(View.GONE);
        if (numberOfTouches != null)
            numberOfTouches.setVisibility(View.GONE);

        // näytetään tulokset
        TextView results = new TextView(this);
        float prosentti = (float)number/allTouches*100;
        results.setText("Oikein: " + number + "/" + allTouches + ", " + (int)prosentti + "%");
        results.setTextSize(30);
        results.measure(0,0);
        results.setX(width/2 - results.getMeasuredWidth()/2);
        results.setY(height/2 - results.getMeasuredHeight()*3/2);
        mainActivity.addView(results);

        // restart painike
        final Button restartButton = new Button(this);
        restartButton.setEnabled(false);
        restartButton.setText("Palaa päävalikkoon");
        restartButton.setTextSize(30);
        restartButton.measure(0,0);
        restartButton.setX(width/2 - restartButton.getMeasuredWidth()/2);
        restartButton.setY(height/2 - restartButton.getMeasuredHeight()*3/2 + 2*restartButton.getMeasuredHeight());

        // fade animaatio painikkeelle
        int length = 3000;
        restartButton.setAlpha(0);
        mainActivity.addView(restartButton);
        restartButton.animate().alpha(1.0f).setDuration(length).start();

        // animaation pituinen viive painikkeen toimimiselle vahinkopainalluksen välttämiseksi
        Handler restartButtonHandler = new android.os.Handler();
        restartButtonHandler.postDelayed(new Runnable() {
            public void run() {
                restartButton.setEnabled(true);
            }
        }, length);

        // painikkeen kuuntelija
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    // Aloittaa uuden pelin
    public void restartGame() {
        this.recreate();
    }

    // Asettaa kuvioiden värit
    public void setColors() {

        // ballShape1 eli pallon väri
        if (ballColor.equalsIgnoreCase(RED)) {
            ballBackground = R.drawable.ball_shape_red;
        }
        if (ballColor.equalsIgnoreCase(BLUE)) {
            ballBackground = R.drawable.ball_shape_blue;
        }
        if (ballColor.equalsIgnoreCase(GREEN)) {
            ballBackground = R.drawable.ball_shape_green;
        }
        ballShape1.setBackgroundResource(ballBackground);

        //Drawable ballDrawable = this.getResources().getDrawable(R.drawable.ball_shape_red);
        //Drawable bg = ballShape1.getBackground();
        //bg.setColorFilter(Color.parseColor(ballColor), PorterDuff.Mode.SRC_ATOP);
        //if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
        //    ballShape1.setBackgroundDrawable(bg);
        //}
        //else {
        //    ballShape1.setBackground(bg);
        //}

        // tyhjä --> neliö animaatioissa olevien neliöiden väri
        if (squareColor.equalsIgnoreCase(RED))
            transpToSquare = R.drawable.transp_to_square_red;
        if (squareColor.equalsIgnoreCase(BLUE))
            transpToSquare = R.drawable.transp_to_square_blue;
        if (squareColor.equalsIgnoreCase(GREEN))
            transpToSquare = R.drawable.transp_to_square_green;

        // pallo --> neliö animaatioissa olevien pallojen ja neliöiden värit
        if (ballColor.equalsIgnoreCase(RED)) {
            if (squareColor.equalsIgnoreCase(RED))
                //ballToSquare = this.getResources().getDrawable(R.drawable.ball_red_to_square_red);
                ballToSquare = R.drawable.ball_red_to_square_red;
            if (squareColor.equalsIgnoreCase(BLUE))
                ballToSquare = R.drawable.ball_red_to_square_blue;
            if (squareColor.equalsIgnoreCase(GREEN))
                ballToSquare = R.drawable.ball_red_to_square_green;
        }
        if (ballColor.equalsIgnoreCase(BLUE)) {
            if (squareColor.equalsIgnoreCase(RED))
                ballToSquare = R.drawable.ball_blue_to_square_red;
            if (squareColor.equalsIgnoreCase(BLUE))
                ballToSquare = R.drawable.ball_blue_to_square_blue;
            if (squareColor.equalsIgnoreCase(GREEN))
                ballToSquare = R.drawable.ball_blue_to_square_green;
        }
        if (ballColor.equalsIgnoreCase(GREEN)) {
            if (squareColor.equalsIgnoreCase(RED))
                ballToSquare = R.drawable.ball_green_to_square_red;
            if (squareColor.equalsIgnoreCase(BLUE))
                ballToSquare = R.drawable.ball_green_to_square_blue;
            if (squareColor.equalsIgnoreCase(GREEN))
                ballToSquare = R.drawable.ball_green_to_square_green;
        }
    }
}
