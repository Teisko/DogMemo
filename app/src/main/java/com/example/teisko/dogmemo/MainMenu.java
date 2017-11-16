package com.example.teisko.dogmemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    Button button_uusi;
    Button button_asetukset;
    Button button_tilastot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        button_uusi = (Button)findViewById(R.id.button_uusi);
        button_uusi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avaus = new Intent(v.getContext(), NewGame.class);
                startActivity(avaus);
            }
        });

    /*
        button_uusi = (Button)findViewById(R.id.button_asetukset);
        button_uusi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avaus = new Intent(v.getContext(), Settings.class);
                startActivity(avaus);
            }
        });

        button_uusi = (Button)findViewById(R.id.button_tilastot);
        button_uusi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avaus = new Intent(v.getContext(), MainActivity.class);
                startActivity(avaus);
            }
        });
     */


    }
}