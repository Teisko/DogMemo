package com.example.teisko.dogmemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.TouchDelegate;
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

import static com.example.teisko.dogmemo.R.layout.activity_game_end;
import static com.example.teisko.dogmemo.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mainActivity;
    private TextView countdown;                     // näyttää aikalaskurin
    private TextView ball;                          // pallo / peite jonka alla pallo
    private TextView ballTouchArea;                 // kosketusalue pallon ympärillä
    private TextView empty1;                        // empty1-3 tyhjä / peite jonka alla tyhjä
    private TextView empty2;
    private TextView empty3;
    private TextView movingCover1;                  // movingCover1-4 liikkuvat peitteet
    private TextView movingCover2;
    private TextView movingCover3;
    private TextView movingCover4;

    final String RED = "#ff0000";                   // Vakioita väreille
    final String BLUE = "#0000ff";
    final String BLACK = "#000000";
    final static String PREF_FILE_NAME = "PrefFile";       // Tiedosto, josta asetukset haetaan

    String ballColor = BLUE;                        // pallon väri, haetaan asetuksista
    String squareColor = BLACK;                     // peitteiden väri, haetaan asetuksista
    int ballBackground;                             // pallon Drawable kuvio
    int squareBackground;                           // peitteen Drawable kuvio
    // Oikeat painallukset & kaikki painallukset
    int correctTouches = 0;                         // oikeiden painallusten määrä
    int allTouches = 0;                             // kaikkien painallusten määrä
    int level = 0;                                  // nykyinen taso pelissä
    int levelCorrectTouches = 0;                    // pisteet nykyisellä levelillä
    int levelAllTouches = 0;                        // kaikki kosketukset kyseisen levelin aikana, lähtöarvo 1 koska jostain syystä 1. leveli loppuu muuten liian aikaisin
    int pointsForLevel = 5;                         // vaadittavat pisteet etenemiseen, haetaan asetuksista
    boolean newLevel = false;                       // true kun päästään uudelle levelille
    int highestLevel = 0;                           // korkein saavutettu taso
    // Ääni oikealle painallukselle
    int correctSoundFile = R.raw.naksutin1;         // ääniefekti oikean painalluksen jälkeen
    MediaPlayer correctSound;
    // Ääni pallon ilmestymiselle
    int ballSoundFile = R.raw.ping;
    MediaPlayer ballSound;
    // Pelin kesto sekunteina
    int gameTime = 60;                              // pelin kesto sekunteina, haetaan asetuksista
    int ballHiddenTime = 2000;                      // pallon piilossaolon kesto oikean painalluksen jälkeen millisekunteina
    int ballVisibleTime = 2000;                     // pallon näkyvissäolon kesto ennen kuin se peitetään millisekunteina, haetaan asetuksista
    int animationLength = 2000;                     // peiteanimaation kesto millisekunteina, haetaan asetuksista
    int width;                                      // näytön leveys pikseleinä
    int height;                                     // näytön korkeus pikseleinä
    int ballDiameter = 300;                         // asetuksissa säädetty pallon koko
    int currentDiameter = 300;                      // pallon tämänhetkinen koko
    double touchAreaCoef = 1.5;                     // kosketusalueen koko verrattuna pallon kokoon
    boolean reduceSize;                             // pienennetäänkö pallon kokoa 1. tason jälkeen vai ei

    private int currentApiVersion;                  // android versio

    @Override
    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // lukitse näytön kääntö, piilota otsikko ja tehtäväpalkki
        super.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(activity_main);

        // piilota navigaatiopalkki, se tulee takaisin näkyviin vetämällä ruudun reunasta
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        // This work only for android 4.4+
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

        // äänipainikkeet muuttavat mediaääniä
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // muuttujia
        mainActivity = (ConstraintLayout) findViewById(R.id.mainactivity);
        countdown = (TextView) findViewById(R.id.numberOfTouches);
        ball = (TextView) findViewById(R.id.ballShape);
        ballTouchArea = new TextView(this);
        ballTouchArea.setVisibility(View.INVISIBLE);
        ballTouchArea.setBackgroundResource(R.drawable.ball_shape_red);
        mainActivity.addView(ballTouchArea);
        empty1 = new TextView(this);
        empty2 = new TextView(this);
        empty3 = new TextView(this);
        empty1.setVisibility(View.INVISIBLE);
        empty2.setVisibility(View.INVISIBLE);
        empty3.setVisibility(View.INVISIBLE);
        mainActivity.addView(empty1);
        mainActivity.addView(empty2);
        mainActivity.addView(empty3);
        movingCover1 = new TextView(this);
        movingCover2 = new TextView(this);
        movingCover3 = new TextView(this);
        movingCover4 = new TextView(this);
        movingCover1.setVisibility(View.INVISIBLE);
        movingCover2.setVisibility(View.INVISIBLE);
        movingCover3.setVisibility(View.INVISIBLE);
        movingCover4.setVisibility(View.INVISIBLE);
        mainActivity.addView(movingCover1);
        mainActivity.addView(movingCover2);
        mainActivity.addView(movingCover3);
        mainActivity.addView(movingCover4);

        // hakee tallennetut asetukset, jos ne ovat olemassa
        SharedPreferences sharedPref = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        // pallon väri
        if (!sharedPref.getString("ball_color_list", "").equals(""))
            ballColor = sharedPref.getString("ball_color_list", "");
        // peitteiden väri
        if (!sharedPref.getString("square_color_list", "").equals(""))
            squareColor = sharedPref.getString("square_color_list", "");
        // peliaika
        if (!sharedPref.getString("gametime_list", "").equals(""))
            gameTime = Integer.parseInt(sharedPref.getString("gametime_list", ""));
        // pallon piilossaoloaika oikean painalluksen jälkeen
        if (!sharedPref.getString("ball_hidden_time_list", "").equals(""))
            ballHiddenTime = Integer.parseInt(sharedPref.getString("ball_hidden_time_list", ""));
        // pallon näkyvissäoloaika ennen peiteanimaation alkua
        if (!sharedPref.getString("ball_visible_time_list", "").equals(""))
            ballVisibleTime = Integer.parseInt(sharedPref.getString("ball_visible_time_list", ""));
        // peiteanimaation kesto
        if (!sharedPref.getString("cover_animation_time_list", "").equals(""))
            animationLength = Integer.parseInt(sharedPref.getString("cover_animation_time_list", ""));
        // tason muuttumiseen vaadittu pistemäärä +-
        if (!sharedPref.getString("level_points_list", "").equals(""))
            pointsForLevel = Integer.parseInt(sharedPref.getString("level_points_list", ""));
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
        if (correctSoundFile != -1)
            correctSound = MediaPlayer.create(MainActivity.this, correctSoundFile);
        // kosketusalueen koko verrattuna pallon kokoon
        if (!sharedPref.getString("touch_area_list", "").equals("")) {
            if (Double.parseDouble(sharedPref.getString("touch_area_list", "")) == 0)
                touchAreaCoef = 1;
            if (Double.parseDouble(sharedPref.getString("touch_area_list", "")) == 1)
                touchAreaCoef = 1.5;
            if (Double.parseDouble(sharedPref.getString("touch_area_list", "")) == 2)
                touchAreaCoef = 2;
        }
        // Pallon koko
        if (!sharedPref.getString("ball_size_list", "").equals("")) {
            ballDiameter = Integer.parseInt(sharedPref.getString("ball_size_list", ""));
        }
        // Pienennä pallon kokoa?
        if (!sharedPref.getString("reduce_size_list", "").equals("")) {
            if (Integer.parseInt(sharedPref.getString("reduce_size_list", "")) == 1)
                reduceSize = true;
            if (Integer.parseInt(sharedPref.getString("reduce_size_list", "")) == 0)
                reduceSize = false;
        }
        // Pallon ilmestymisääni
        if (!sharedPref.getString("ball_sound_list", "").equals("")) {
            if (Integer.parseInt(sharedPref.getString("ball_sound_list", "")) == 0)
                ballSoundFile = -1;
            if (Integer.parseInt(sharedPref.getString("ball_sound_list", "")) == 1)
                ballSoundFile = R.raw.ping;
            if (Integer.parseInt(sharedPref.getString("ball_sound_list", "")) == 2)
                ballSoundFile = R.raw.piip1;
            if (Integer.parseInt(sharedPref.getString("ball_sound_list", "")) == 3)
                ballSoundFile = R.raw.piip2;
            if (Integer.parseInt(sharedPref.getString("ball_sound_list", "")) == 4)
                ballSoundFile = R.raw.piip3;
        }
        if (ballSoundFile != -1)
            ballSound = MediaPlayer.create(MainActivity.this, ballSoundFile);

        // asettaa haetut värit
        setColors();

        // Näytön koko pikseleinä
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        // Aloita 1. taso
        drawLevel();

        // Kaikkien kosketusten kuuntelija
        mainActivity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    allTouches++;
                    levelAllTouches++;

                    // tutkitaan osuiko kosketus oikeaan kohteeseen
                    ballTouchArea.setX(ball.getX() + ball.getWidth()/2 - ballTouchArea.getWidth()/2);
                    ballTouchArea.setY(ball.getY() + ball.getHeight()/2 - ballTouchArea.getHeight()/2);
                    Rect ballRect = new Rect();
                    ballTouchArea.getHitRect(ballRect);

                    // jos osui
                    if (ballRect.contains((int)event.getX(), (int)event.getY())) {
                        correctTouches++;
                        levelCorrectTouches++;
                        if (correctSoundFile != -1) {
                            correctSound.start();
                        }
                        // levelin nousu
                        if (levelCorrectTouches == (levelAllTouches - levelCorrectTouches) + pointsForLevel) {
                            level++;
                            if (level > highestLevel) {
                                highestLevel = level;
                            }
                            newLevel = true;
                            levelCorrectTouches = 0;
                            levelAllTouches = 0;
                        }
                        drawLevel();
                    }

                    // levelin tippuminen
                    else if (levelAllTouches - levelCorrectTouches == levelCorrectTouches + pointsForLevel) {
                        if (level >= 6) {
                            level = 4;
                        }
                        else if (level >= 2) {
                            level = level-2;
                        }
                        else {
                            level = 0;
                        }
                        newLevel = true;
                        levelAllTouches = 0;
                        levelCorrectTouches = 0;
                        drawLevel();
                    }
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

    // auttaa navigaatiopalkin piilottamisessa
    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    // piirtää näytölle oikean levelin mukaiset elementit
    public void drawLevel() {
        // piilotetaan pallo ja peitteet näkyvistä
        ball.setVisibility(View.INVISIBLE);
        ball.setBackgroundResource(ballBackground);
        empty1.setVisibility(View.INVISIBLE);
        empty2.setVisibility(View.INVISIBLE);
        empty3.setVisibility(View.INVISIBLE);

        // katko näytön kosketuksen tunnistukseen kunnes pallosta tulee taas näkyvä
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        // Viiveen ballHiddenTime jälkeen pallosta tulee taas näkyvä
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            // tämä suoritetaan viiveen ballHiddenTime jälkeen
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                ball.setVisibility(View.VISIBLE);

                // Pallon ilmestymisääni
                if (ballSoundFile != -1) {
                    ballSound.start();
                }

                // Aluksi pallo on keskellä ballDiameter kokoisena
                if (level == 0) {
                    // muuta pallon, peitteiden ja tyhjien pallon paikkojen kokoa ballDiameter kokoiseksi
                    DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
                    currentDiameter = ballDiameter;
                    int px = Math.round(currentDiameter * (dm.densityDpi / 160f));
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ball.getLayoutParams();
                    params.width = px;
                    params.height = px;
                    ball.setLayoutParams(params);
                    movingCover1.setLayoutParams(params);
                    movingCover2.setLayoutParams(params);
                    movingCover3.setLayoutParams(params);
                    movingCover4.setLayoutParams(params);
                    empty1.setLayoutParams(params);
                    empty2.setLayoutParams(params);
                    empty3.setLayoutParams(params);

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

                    // muuta kosketusalueen kokoa
                    DisplayMetrics dm2 = Resources.getSystem().getDisplayMetrics();
                    int px2 = Math.round((int)(currentDiameter*touchAreaCoef) * (dm2.densityDpi / 160f));
                    ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) ballTouchArea.getLayoutParams();
                    params2.width = px2;
                    params2.height = px2;
                    ballTouchArea.setLayoutParams(params2);
                    // aseta kosketusalue pallon kohdalle
                    ball.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            ball.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            ballTouchArea.setX(ball.getX() + ball.getWidth()/2 - ballTouchArea.getWidth()/2);
                            ballTouchArea.setY(ball.getY() + ball.getHeight()/2 - ballTouchArea.getHeight()/2);
                        }
                    });
                }

                // pienennä pallon kokoa puolella jos asetus on päällä
                if (level == 1 && reduceSize) {
                    // muuta pallon kokoa
                    DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
                    currentDiameter = ballDiameter/2;
                    int px = Math.round(currentDiameter * (dm.densityDpi / 160f));
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ball.getLayoutParams();
                    params.width = px;
                    params.height = px;
                    ball.setLayoutParams(params);
                    movingCover1.setLayoutParams(params);
                    movingCover2.setLayoutParams(params);
                    movingCover3.setLayoutParams(params);
                    movingCover4.setLayoutParams(params);
                    empty1.setLayoutParams(params);
                    empty2.setLayoutParams(params);
                    empty3.setLayoutParams(params);

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

                    // muuta kosketusalueen kokoa
                    DisplayMetrics dm2 = Resources.getSystem().getDisplayMetrics();
                    int px2 = Math.round((int)(currentDiameter*touchAreaCoef) * (dm2.densityDpi / 160f));
                    ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) ballTouchArea.getLayoutParams();
                    params2.width = px2;
                    params2.height = px2;
                    ballTouchArea.setLayoutParams(params2);

                    ball.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            ball.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            ballTouchArea.setX(ball.getX() + ball.getWidth()/2 - ballTouchArea.getWidth()/2);
                            ballTouchArea.setY(ball.getY() + ball.getHeight()/2 - ballTouchArea.getHeight()/2);
                        }
                    });
                }

                // jos kokoa ei haluta pienentää
                else if (level == 1) {
                    // Pallo keskelle
                    ball.setX(width/2 - ball.getWidth()/2);
                    ball.setY(height/2 - ball.getHeight()/2);
                }

                // pallo liikkuu 4 eri kohdan välillä
                if (level == 2) {
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

                // peitetään pallo keskellä näyttöä
                if (level == 3) {
                    // siirretään pallo keskelle
                    ball.setX(width/2 - ball.getWidth()/2);
                    ball.setY(height/2 - ball.getHeight()/2);

                    // Animoi peitteet
                    doAnimation(1);
                }

                // 2 peitettä
                if (level == 4) {
                    if (newLevel) {
                        // liikutetaan 1. peite
                        ball.setX(width/2 - ball.getWidth()/2 - width/4);
                        ball.setY(height/2 - ball.getHeight()/2);
                        // liikutetaan 2. peite
                        empty1.setX(width/2 - ball.getWidth()/2 + width/4);
                        empty1.setY(height/2 - ball.getHeight()/2);
                    }
                    // Arpoo pallon paikan
                    randomSpot(2);
                    // Asetetaan transparent kuvio
                    empty1.setVisibility(View.INVISIBLE);
                    // Animoi peitteet
                    doAnimation(2);
                }

                // 3 peitettä
                if (level == 5) {
                    if (newLevel) {
                        // liikutetaan 1. peite
                        ball.setX(width/5 - ball.getWidth()/2);
                        ball.setY(height/2 - ball.getHeight()/2);
                        // liikutetaan 2. peite
                        empty1.setX(width/2 - ball.getWidth()/2);
                        empty1.setY(height/2 - ball.getHeight()/2);
                        // liikutetaan 3. peite
                        empty2.setX(width*4/5 - ball.getWidth()/2);
                        empty2.setY(height/2 - ball.getHeight()/2);
                    }
                    // Arpoo pallon paikan
                    randomSpot(3);
                    // Asetetaan transparent kuvio
                    empty2.setVisibility(View.INVISIBLE);
                    // Animoi peitteet
                    doAnimation(3);
                }

                // 4 peitettä
                if (level >= 6) {
                    if (newLevel) {
                        // liikutetaan 1. peite
                        ball.setX(width/4 - ball.getWidth()/2);
                        ball.setY(height/4 - ball.getHeight()/2);
                        // liikutetaan 2. peite
                        empty1.setX(width*3/4 - ball.getWidth()/2);
                        empty1.setY(height/4 - ball.getHeight()/2);
                        // liikutetaan 3. peite
                        empty2.setX(width/4 - ball.getWidth()/2);
                        empty2.setY(height*3/4 - ball.getHeight()/2);
                        // liikutetaan 4. peite
                        empty3.setX(width*3/4 - ball.getWidth()/2);
                        empty3.setY(height*3/4 - ball.getHeight()/2);
                    }
                    // Arpoo pallon paikan
                    randomSpot(4);
                    // Asetetaan transparent kuvio
                    empty3.setVisibility(View.INVISIBLE);
                    // Animoi peitteet
                    doAnimation(4);
                }

                // siirrä kosketusalue pallon kohdalle
                ballTouchArea.setX(ball.getX() + ball.getWidth()/2 - ballTouchArea.getWidth()/2);
                ballTouchArea.setY(ball.getY() + ball.getHeight()/2 - ballTouchArea.getHeight()/2);

                newLevel = false;
                //Toast.makeText(MainActivity.this, "level = " + level + "\n" + levelCorrectTouches + " / " + levelAllTouches + "\n" + correctTouches + " / " + allTouches, Toast.LENGTH_SHORT).show();
            }
        }, ballHiddenTime);
    }

    // Arpoo pallon paikan, n = paikkojen lkm
    public void randomSpot(int n) {
        double random = Math.floor(Math.random()*n);
        float x1 = ball.getX();
        float y1 = ball.getY();
        if (random == 1) {
            ball.setX(empty1.getX());
            ball.setY(empty1.getY());
            empty1.setX(x1);
            empty1.setY(y1);
        }
        if (random == 2) {
            ball.setX(empty2.getX());
            ball.setY(empty2.getY());
            empty2.setX(x1);
            empty2.setY(y1);
        }
        if (random == 3) {
            ball.setX(empty3.getX());
            ball.setY(empty3.getY());
            empty3.setX(x1);
            empty3.setY(y1);
        }
    }

    // Animoi peitteen pallon ja tyhjien tilalle, n = paikkojen lkm
    public void doAnimation(final int n) {
        // katko näytön kosketuksen tunnistukseen kunnes pallo on peitetty
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Handler touchHandler = new android.os.Handler();
        touchHandler.postDelayed(new Runnable() {
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        }, ballVisibleTime + animationLength);

        // Pallo näkyy ballVisibleTime ajan, jonka jälkeen se peitetään
        // peiteanimaatio kestää animationLength ajan
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            // tämä suoritetaan viiveen ballVisibleTime jälkeen
            public void run() {
                // peiteanimaatiot
                // pallon peittäminen
                if (n >= 1) {
                    movingCover1.setX(ball.getX());
                    movingCover1.setY(-ball.getHeight());
                    movingCover1.setVisibility(View.VISIBLE);
                    movingCover1.animate().x(ball.getX()).y(ball.getY()).setDuration(animationLength).start();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        // tämä suoritetaan viiveen animationLength jälkeen
                        public void run() {
                            ball.setBackgroundResource(squareBackground);
                            movingCover1.setVisibility(View.INVISIBLE);
                        }
                    }, animationLength);
                }
                // 2. paikan peittäminen
                if (n >= 2) {
                    movingCover2.setX(empty1.getX());
                    movingCover2.setY(-ball.getHeight());
                    movingCover2.setVisibility(View.VISIBLE);
                    movingCover2.animate().x(empty1.getX()).y(empty1.getY()).setDuration(animationLength).start();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        // tämä suoritetaan viiveen animationLength jälkeen
                        public void run() {
                            empty1.setVisibility(View.VISIBLE);
                            movingCover2.setVisibility(View.INVISIBLE);
                        }
                    }, animationLength);
                }
                // 3. paikan peittäminen
                if (n >= 3) {
                    movingCover3.setX(empty2.getX());
                    movingCover3.setY(-ball.getHeight());
                    movingCover3.setVisibility(View.VISIBLE);
                    movingCover3.animate().x(empty2.getX()).y(empty2.getY()).setDuration(animationLength).start();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        // tämä suoritetaan viiveen animationLength jälkeen
                        public void run() {
                            empty2.setVisibility(View.VISIBLE);
                            movingCover3.setVisibility(View.INVISIBLE);
                        }
                    }, animationLength);
                }
                // 4. paikan peittäminen
                if (n >= 4) {
                    movingCover4.setX(empty3.getX());
                    movingCover4.setY(-ball.getHeight());
                    movingCover4.setVisibility(View.VISIBLE);
                    movingCover4.animate().x(empty3.getX()).y(empty3.getY()).setDuration(animationLength).start();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        // tämä suoritetaan viiveen animationLength jälkeen
                        public void run() {
                            empty3.setVisibility(View.VISIBLE);
                            movingCover4.setVisibility(View.INVISIBLE);
                        }
                    }, animationLength);
                }
            }
        }, ballVisibleTime);
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
                Intent avaus = new Intent(v.getContext(), MainMenu.class);
                startActivity(avaus);
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

    // androidin takaisin-näppäin palaa päävalikkoon
    public void onBackPressed() {
        Intent avaus = new Intent(MainActivity.this, MainMenu.class);
        startActivity(avaus);
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

        // empty1-3 eli peitteen väri
        if (squareColor.equalsIgnoreCase(RED)) {
            squareBackground = R.drawable.square_shape_red;
        }
        if (squareColor.equalsIgnoreCase(BLUE)) {
            squareBackground = R.drawable.square_shape_blue;
        }
        if (squareColor.equalsIgnoreCase(BLACK)) {
            squareBackground = R.drawable.square_shape_black;
        }
        empty1.setBackgroundResource(squareBackground);
        empty2.setBackgroundResource(squareBackground);
        empty3.setBackgroundResource(squareBackground);
        movingCover1.setBackgroundResource(squareBackground);
        movingCover2.setBackgroundResource(squareBackground);
        movingCover3.setBackgroundResource(squareBackground);
        movingCover4.setBackgroundResource(squareBackground);
    }
}
