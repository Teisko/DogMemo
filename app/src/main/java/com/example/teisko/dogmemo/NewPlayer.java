package com.example.teisko.dogmemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Date;

public class NewPlayer extends AppCompatActivity {

    /* Attribuutit */
    Button button_save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_player);

        button_save = (Button)findViewById(R.id.button_save);
        /* button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Player uusi = new Player(String d, String o, String r, Date syntyma, int p, int k, boolean sp);*/
    }
}
