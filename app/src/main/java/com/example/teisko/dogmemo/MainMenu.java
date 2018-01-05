package com.example.teisko.dogmemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends AppCompatActivity {

    private final String TAG = "MainMenu";

    Button button_uusi;
    Button button_asetukset;
    Button button_tilastot;
    TextView menuText;
    static MediaPlayer musicPlayer;
    boolean playMusic = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // äänipainikkeet muuttavat mediaääniä
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // soita musiikki
        playMusicFile();

        // aseta logo
        menuText = (TextView)findViewById(R.id.text_menu);
        menuText.setText("");
        menuText.setBackgroundResource(R.mipmap.logo1);

        button_uusi = (Button)findViewById(R.id.button_uusi);
        button_uusi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avaus = new Intent(v.getContext(), NewGame.class);
                startActivity(avaus);
            }
        });

        button_asetukset = (Button)findViewById(R.id.button_asetukset);
        button_asetukset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avaus = new Intent(MainMenu.this, SettingsActivity.class);
                avaus.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                avaus.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
                startActivity(avaus);
            }
        });

        button_tilastot = (Button)findViewById(R.id.button_tilastot);
        button_tilastot.setEnabled(false);
    }

    public void playMusicFile() {
        // soita musiikki
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.PREF_FILE_NAME, MODE_PRIVATE);
        if (!sharedPref.getString("music_list", "").equals("")) {
            if (Integer.parseInt(sharedPref.getString("music_list", "")) == 1)
                playMusic = true;
            if (Integer.parseInt(sharedPref.getString("music_list", "")) == 0)
                playMusic = false;
        }
        if (playMusic) {
            if (musicPlayer == null) {
                musicPlayer = MediaPlayer.create(MainMenu.this, R.raw.koirapeli);
                musicPlayer.setLooping(true);
            }
            if (!musicPlayer.isPlaying())
                musicPlayer.start();
        }
    }

    public void onResume() {
        super.onResume();  // Always call the superclass method first
        playMusicFile();
    }
}