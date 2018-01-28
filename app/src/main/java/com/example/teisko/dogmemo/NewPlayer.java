package com.example.teisko.dogmemo;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewPlayer extends AppCompatActivity {

    /* Vakiot */
    final static String DATE_FORMAT = "dd-MM-yyyy";

    /* Attribuutit */
    Button button_save;
    EditText edit_koira;
    EditText edit_omistaja;
    EditText edit_rotu;
    EditText edit_syntyma;
    RadioGroup rg_sukupuoli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_player);

        // äänipainikkeet muuttavat mediaääniä
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        button_save = (Button)findViewById(R.id.button_save);
        edit_koira = (EditText)findViewById(R.id.edit_koira);
        edit_omistaja = (EditText)findViewById(R.id.edit_omistaja);
        edit_rotu = (EditText)findViewById(R.id.edit_rotu);
        edit_syntyma = (EditText)findViewById(R.id.edit_syntyma);

        edit_syntyma.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus){
                if(hasFocus){
                    KalenteriDialogi dialog = new KalenteriDialogi();
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    dialog.show(ft, "DatePicker");

                }
            }

        });
        rg_sukupuoli = (RadioGroup)findViewById(R.id.rg_sukupuoli);

        button_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){

                if(checkFields()) {
                    String kNimi = edit_koira.getText().toString();
                    String oNimi = edit_omistaja.getText().toString();
                    String rotu = edit_rotu.getText().toString();
                    String[] ajat = edit_syntyma.getText().toString().split("-");
                    int vuosi = Integer.parseInt(ajat[2]);
                    int kuukausi = Integer.parseInt(ajat[1]);
                    int paiva = Integer.parseInt(ajat[0]);
                    Date syntyma = new Date(vuosi-1900, kuukausi-1, paiva);
                    int sukupuoli = -1;

                    int rGId = rg_sukupuoli.getCheckedRadioButtonId();
                    if (R.id.rb_narttu == rGId) {
                        sukupuoli = 0;
                    } else
                        sukupuoli = 1;

                    // Lisätään profiilitiedostoon tiedot uudesta pelaajasta
                    File file = new File(getApplicationContext().getFilesDir(), Player.TIEDNIMI);

                    Player uusi = new Player(kNimi, oNimi, rotu, syntyma, sukupuoli);

                    // Luodaan pelaajan pisteitä varten tiedosto
                    File pistetiedosto = new File(getApplicationContext().getFilesDir(), uusi.fileName());

                    try {
                        FileWriter fileWriter = new FileWriter(file, true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(uusi.toString() + "\n");
                        bufferedWriter.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent output = new Intent();
                    setResult(NewGame.PELAAJALUOTU, output);
                    finish();
                }
            }
        });
    }

    // Metodi, joka tarkistaa uuden pelaajan lomakkeen tiedot, ja palauttaa tiedon oikeellisuudesta.
    private boolean checkFields() {
        Context context = getApplicationContext();
        CharSequence text = "";
        int duration;
        duration = Toast.LENGTH_SHORT;
        Toast toast;
        boolean isOk = true;



        String kNimi = edit_koira.getText().toString();
        if(kNimi.length() < 1)
        {
            text = "Syötä koiran nimi.";
        }
        else
        {
            // Tarkistetaan löytyykö samannimistä pelaajaa
            if(!(new File(getApplicationContext().getFilesDir(), kNimi + ".txt").isFile()))
            {
                boolean hasNonAlpha = false;
                String osat[] = kNimi.split(" ");
                for (int i = 0; i < osat.length; i++) {
                    hasNonAlpha = osat[i].matches("^.*[^a-zA-Z0-9 ].*$");
                    if (hasNonAlpha) {
                        text = "Koiran nimi sisältää ei-aakkosanumeerisiä merkkejä";
                    }
                }
            }
            else
                text = "Samanniminen koira löytyy jo.";
        }

        String oNimi = edit_omistaja.getText().toString();
        if(oNimi.length() < 1)
        {
            text = "Syötä omistajan nimi.";
        }
        else
        {
            boolean hasNonAlpha = false;
            String osat[] = oNimi.split(" ");
            for(int i = 0; i < osat.length; i++) {
                hasNonAlpha = osat[i].matches("^.*[^a-zA-Z0-9 ].*$");
                if(hasNonAlpha)
                {
                    text = "Omistajan nimi sisältää ei-aakkosanumeerisiä merkkejä";
                }
            }
        }

        String rotu = edit_rotu.getText().toString();
        if(rotu.length() < 1)
        {
            text = "Syötä koiran rotu.";
        }
        else
        {
            boolean hasNonAlpha = false;
            String osat[] = rotu.split(" ");
            for(int i = 0; i < osat.length; i++) {
                hasNonAlpha = osat[i].matches("^.*[^a-zA-Z0-9 ].*$");
                if(hasNonAlpha)
                {
                    text = "Koiran rotu sisältää ei-aakkosanumeerisiä merkkejä";
                }
            }
        }

        String[] ajat = edit_syntyma.getText().toString().split("-");
        if(ajat.length != 3)
        {
            text = "Syötä koiran syntymäpäivä muodossa: dd-mm-yyyy tai käytä päivämäärän valitsinta";
        }
        else
        {
            int vuosi = Integer.parseInt(ajat[2]);

            if(!isDateValid(edit_syntyma.getText().toString()))
            {
                text = "Syötetty päivämäärä on virheellinen. Vaaditaan muotoa dd-MM-yyyy.";
            }
        }

        int rGId = rg_sukupuoli.getCheckedRadioButtonId();
        if (rGId == -1)
            text = "Valitse koiran sukupuoli.";

        // Jos jonkinlainen virhe on löydetty, tekstillä on pituutta > 0
        if(text.length() > 0) {
            toast = Toast.makeText(context, text, duration);
            toast.show();
            isOk = false;
        }

        return isOk;
    }

    // Funktio, joka tarkistaa "dd-mm-yyyy"-muotoisen päivämäärän oikeellisuuden
    private boolean isDateValid(String date)
    {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public void onOk(String value)
    {
        edit_syntyma.setText(value);
    }
}
