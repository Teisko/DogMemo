package com.example.teisko.dogmemo;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaPlayer;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.example.teisko.dogmemo.R.layout.activity_game_end;
import static com.example.teisko.dogmemo.R.layout.activity_main;

public class PracticeActivity extends AppCompatActivity {

    private ConstraintLayout mainActivity;
    private TextView countdown;                     // näyttää aikalaskurin
    private TextView ball;                          // pallo / peite jonka alla pallo

    final String RED = "#ff0000";                   // Vakioita väreille
    final String BLUE = "#0000ff";
    final String BLACK = "#000000";
    final String PREF_FILE_NAME = "PrefFile";       // Tiedosto, josta asetukset haetaan

    String ballColor = BLUE;                         // pallon väri, haetaan asetuksista
    int ballBackground;                             // pallon Drawable kuvio
    // Oikeat painallukset & kaikki painallukset
    int correctTouches = 0;                         // oikeiden painallusten määrä
    int allTouches = 0;                             // kaikkien painallusten määrä
    // Ääni oikealle painallukselle
    int correctSoundFile = R.raw.naksutin2;         // ääniefekti oikean painalluksen jälkeen
    MediaPlayer correctSound;
    // Pelin kesto sekunteina
    int gameTime = 60;                              // pelin kesto sekunteina, haetaan asetuksista
    int ballHiddenTime = 2000;                      // pallon piilossaolon kesto oikean painalluksen jälkeen millisekunteina
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
        countdown = (TextView) findViewById(R.id.numberOfTouches);
        ball = (TextView) findViewById(R.id.ballShape);

        // hakee tallennetut asetukset, jos ne ovat olemassa
        SharedPreferences sharedPref = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        // pallon väri
        if (!sharedPref.getString("ball_color_list", "").equals(""))
            ballColor = sharedPref.getString("ball_color_list", "");
        // peliaika
        if (!sharedPref.getString("gametime_list", "").equals(""))
            gameTime = Integer.parseInt(sharedPref.getString("gametime_list", ""));
        // pallon piilossaoloaika oikean painalluksen jälkeen
        if (!sharedPref.getString("ball_hidden_time_list", "").equals(""))
            ballHiddenTime = Integer.parseInt(sharedPref.getString("ball_hidden_time_list", ""));
        // oikean painalluksen kannustusääni
        if (!sharedPref.getString("correct_sound_list", "").equals("")) {
            if (Integer.parseInt(sharedPref.getString("correct_sound_list", "")) == 0)
                correctSoundFile = -1;
            if (Integer.parseInt(sharedPref.getString("correct_sound_list", "")) == 1)
                correctSoundFile = R.raw.naksutin1;
            if (Integer.parseInt(sharedPref.getString("correct_sound_list", "")) == 2)
                correctSoundFile = R.raw.naksutin2;
            if (Integer.parseInt(sharedPref.getString("correct_sound_list", "")) == 3)
                correctSoundFile = R.raw.rapina;
            if (Integer.parseInt(sharedPref.getString("correct_sound_list", "")) == 4)
                correctSoundFile = R.raw.goodboy;
            if (Integer.parseInt(sharedPref.getString("correct_sound_list", "")) == 5)
                correctSoundFile = R.raw.goodgirl;
        }
        if (correctSoundFile != -1) {
            correctSound = MediaPlayer.create(PracticeActivity.this, correctSoundFile);
        }
        // asettaa haetut värit
        setColors();

        // Näytön koko pikseleinä
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        // Asettaa pallon keskelle näyttöä pelin alkaessa näytön piirron jälkeen
        ball.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ball.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Pallo keskelle
                ball.setX(width/2 - ball.getWidth()/2);
                ball.setY(height/2 - ball.getHeight()/2);
                drawLevel();
            }
        });

        // Pallon kosketuksen kuuntelija
        ball.setOnTouchListener(new View.OnTouchListener() {
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
        countdown.setText(time);
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
                countdown.setText(time);
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
        // Lisää pisteet ja nosta leveliä tarvittaessa
        correctTouches++;
        if (correctSoundFile != -1) {
            correctSound.start();
        }
        drawLevel();
    }

    public void drawLevel() {
        // piilotetaan pallo ja peitteet näkyvistä
        ball.setVisibility(View.INVISIBLE);
        ball.setBackgroundResource(ballBackground);

        // katko näytön kosketuksen tunnistukseen kunnes pallosta tulee taas näkyvä
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Handler touchHandler = new android.os.Handler();
        touchHandler.postDelayed(new Runnable() {
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }, ballHiddenTime);

        // Viiveen ballHiddenTime jälkeen pallosta tulee taas näkyvä
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            // tämä suoritetaan viiveen ballHiddenTime jälkeen
            public void run() {
                ball.setVisibility(View.VISIBLE);

                // Aluksi pallo on keskellä 300dp kokoisena
                if (correctTouches == 0) {
                    // muuta pallon kokoa
                    DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
                    int px = Math.round(300 * (dm.densityDpi / 160f));
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ball.getLayoutParams();
                    params.width = px;
                    params.height = px;
                    ball.setLayoutParams(params);

                    // Asettaa pallon keskelle näyttöä koon muuttamisen jälkeen
                    ball.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            ball.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            // Pallo keskelle
                            ball.setX(width/2 - ball.getWidth()/2);
                            ball.setY(height/2 - ball.getHeight()/2);
                        }
                    });
                }

                // pienennä pallo 150dp kokoiseksi
                if (correctTouches == 1) {
                    // muuta pallon kokoa
                    DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
                    int px = Math.round(150 * (dm.densityDpi / 160f));
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ball.getLayoutParams();
                    params.width = px;
                    params.height = px;
                    ball.setLayoutParams(params);

                    // Asettaa pallon keskelle näyttöä koon muuttamisen jälkeen
                    ball.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            ball.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            // Pallo keskelle
                            ball.setX(width/2 - ball.getWidth()/2);
                            ball.setY(height/2 - ball.getHeight()/2);
                        }
                    });
                }

                // pallo liikkuu 4 eri kohdan välillä
                if (correctTouches >= 2) {
                    double random = Math.floor(Math.random()*4);
                    if (random == 1) {
                        // alavasen kulma
                        ball.setX(width/4 - ball.getWidth()/2);
                        ball.setY(height*3/4 - ball.getHeight()/2);
                    }
                    if (random == 2) {
                        // ylävasen kulma
                        ball.setX(width/4 - ball.getWidth()/2);
                        ball.setY(height/4 - ball.getHeight()/2);
                    }
                    if (random == 3) {
                        // yläoikea kulma
                        ball.setX(width*3/4 - ball.getWidth()/2);
                        ball.setY(height/4 - ball.getHeight()/2);
                    }
                    if (random == 4) {
                        // alaoikea kulma
                        ball.setX(width*3/4 - ball.getWidth()/2);
                        ball.setY(height*3/4 - ball.getHeight()/2);
                    }
                }
            }
        }, ballHiddenTime);
    }

    public void gameOver() {
        // vaihdetaan layout
        setContentView(activity_game_end);
        TextView oikeinTeksti = (TextView) findViewById(R.id.text_oikein);
        final Button uusiPeli = (Button) findViewById(R.id.button_uusipeli);
        final Button palaa = (Button) findViewById(R.id.button_palaa);
        uusiPeli.setEnabled(false);
        palaa.setEnabled(false);

        // näytetään tulokset
        float prosentti = (float) correctTouches/allTouches*100;
        oikeinTeksti.setText("Oikein: " + correctTouches + "/" + allTouches + ", " + (int)prosentti + "%");

        // fade animaatio painikkeelle
        int length = 3000;
        uusiPeli.setAlpha(0);
        palaa.setAlpha(0);
        uusiPeli.animate().alpha(1.0f).setDuration(length).start();
        palaa.animate().alpha(1.0f).setDuration(length).start();

        // animaation pituinen viive painikkeen toimimiselle vahinkopainalluksen välttämiseksi
        Handler restartButtonHandler = new android.os.Handler();
        restartButtonHandler.postDelayed(new Runnable() {
            public void run() {
                uusiPeli.setEnabled(true);
                palaa.setEnabled(true);
            }
        }, length);

        // palaa painikkeen kuuntelija
        palaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // uusipeli painikkeen kuuntelija
        uusiPeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    // Asettaa kuvioiden värit
    public void setColors() {

        // ball eli pallon väri
        if (ballColor.equalsIgnoreCase(RED)) {
            ballBackground = R.drawable.ball_shape_red;
        }
        if (ballColor.equalsIgnoreCase(BLUE)) {
            ballBackground = R.drawable.ball_shape_blue;
        }
        if (ballColor.equalsIgnoreCase(BLACK)) {
            ballBackground = R.drawable.ball_shape_black;
        }
        ball.setBackgroundResource(ballBackground);
    }
}
