package com.example.teisko.dogmemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class NewGame extends AppCompatActivity {

    /* Attribuutteja
     *
     */
    Button button_start;
    Button button_addPlayer;

    RadioGroup radioGroup;
    RadioButton radio_normaali;
    RadioButton radio_valinnainen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        // Yhdistetään attribuutit niitä vastaaviin näyttöolioihin
        button_start = (Button)findViewById(R.id.button_start);
        button_addPlayer = (Button)findViewById(R.id.button_addPlayer);

        radio_normaali = (RadioButton)findViewById(R.id.radio_normaali);
        radio_valinnainen = (RadioButton)findViewById(R.id.radio_harjoittelu);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        button_addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avaus = new Intent(v.getContext(), NewPlayer.class);
                startActivity(avaus);
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int peli = -1;
                if(radioGroup.getCheckedRadioButtonId()!=-1){
                    peli = radioGroup.getCheckedRadioButtonId();
                }
                if(peli == 0) {
                    Intent avaus = new Intent(v.getContext(), MainActivity.class);
                }
                if(peli == 1)
                {
                    Intent avaus = new Intent(v.getContext(), PracticeActivity.class);
                }
                startActivity(avaus);
            }
        });

        // Luodaan esimerkkilista                                                   -HUOM: poista sisältö kun pelaajan lisäys toimii
        String[] dogs = {"Koira1", "Koira2", "Koira3", "Koira4", "Koira5", "Koira6", "Koira7", "Koira8", "Koira9"};
        ListAdapter dogsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dogs);
        ListView dogList = (ListView)findViewById(R.id.dogList);
        dogList.setAdapter(dogsAdapter);
    }
}
