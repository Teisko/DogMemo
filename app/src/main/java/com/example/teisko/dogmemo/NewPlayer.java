package com.example.teisko.dogmemo;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;

public class NewPlayer extends AppCompatActivity {

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

                String kNimi = edit_koira.getText().toString();
                String oNimi = edit_omistaja.getText().toString();
                String rotu = edit_rotu.getText().toString();
                String[] ajat = edit_syntyma.getText().toString().split("-");
                int vuosi = Integer.parseInt(ajat[2]);
                int kuukausi = Integer.parseInt(ajat[1]);
                int paiva = Integer.parseInt(ajat[0]);
                Date syntyma = new Date(vuosi, kuukausi, paiva);
                int sukupuoli = -1;

                int rGId = rg_sukupuoli.getCheckedRadioButtonId();
                if(rGId != -1) {
                    if (R.id.rb_narttu == rGId) {
                        sukupuoli = 0;
                    } else
                        sukupuoli = 1;
                }

                // Lisätään tiedostoon tiedot uudesta pelaajasta
                FileOutputStream outputStream;

                Player uusi = new Player(kNimi, oNimi, rotu, syntyma, sukupuoli);

                try {
                    String rivi = uusi.toString() + "\n";
                    outputStream = openFileOutput(NewGame.TIEDNIMI, Context.MODE_PRIVATE);
                    outputStream.write(rivi.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent output = new Intent();
                output.putExtra("Avain", 1);
                setResult(RESULT_OK, output);
                finish();
            }
        });
    }

    public void onOk(String value)
    {
        edit_syntyma.setText(value);
    }
}
