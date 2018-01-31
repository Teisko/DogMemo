package com.example.teisko.dogmemo;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class NewGame extends AppCompatActivity {

    // Vakioita
    private static final String TAG = "NewGame";

    // Request codes
    public static final int PELAAJANLUONTI = 25;
    public static final int PELI = 3758;

    // Resultcodes
    public static final int PELIOHI = 1;
    public static final int PELAAJALUOTU = 2;

    /* Attribuutteja
     *
     */
    Button button_start;
    Button button_addPlayer;

    RadioGroup radioGroup;
    RadioButton radio_normaali;
    RadioButton radio_valinnainen;
    public String[] names;
    public Player[] dogs;
    public Player valittu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        File file = new File(getApplicationContext().getFilesDir(), Player.TIEDNIMI);

        try {
            if(!file.exists()) {
                file.createNewFile();
            }
        }
        catch(IOException e)
        {
            Log.e(TAG, "IOEXCEPTION!!!!!!!!!");
        }
      
        // äänipainikkeet muuttavat mediaääniä
        setVolumeControlStream(AudioManager.STREAM_MUSIC);



        // Yhdistetään attribuutit niitä vastaaviin näyttöolioihin
        button_start = (Button)findViewById(R.id.button_start);

        // Aloituspainike ei toimi ennenkuin ollaan valittu pelaajaprofiili
        button_start.setEnabled(false);

        button_addPlayer = (Button)findViewById(R.id.button_addPlayer);

        radio_normaali = (RadioButton)findViewById(R.id.radio_normaali);
        radio_valinnainen = (RadioButton)findViewById(R.id.radio_harjoittelu);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        button_addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent avaus = new Intent(v.getContext(), NewPlayer.class);
                startActivityForResult(avaus, PELAAJALUOTU);
            }
        });

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int peli = radioGroup.getCheckedRadioButtonId();
                Intent avaus = null;
                if(peli == R.id.radio_normaali) {
                    avaus = new Intent(v.getContext(), MainActivity.class);
                }
                else
                    avaus = new Intent(v.getContext(), PracticeActivity.class);
                // pysäytetään valikkomusiikki
                if (MainMenu.musicPlayer != null && MainMenu.musicPlayer.isPlaying()) {
                    MainMenu.musicPlayer.pause();
                }
                startActivityForResult(avaus, PELIOHI);
            }
        });

        // Luetaan koiralista tiedostosta
        luePelaajat();
        ListAdapter dogsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        ListView dogList = (ListView)findViewById(R.id.dogList);
        dogList.setAdapter(dogsAdapter);
        dogList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                valittu = dogs[position];
                button_start.setEnabled(true);
            }
        });
        dogList.setAdapter(dogsAdapter);
    }

    private String[] lueNimet()
    {
        names = new String[dogs.length];
        for(int i = 0;i < dogs.length;i++)
        {
            names[i] = dogs[i].dogName();
        }
        return names;
    }

    /** Lukee tiedostosta pelaajat ja päivittää tämän olion listat */
    public void luePelaajat() {

        FileInputStream inputStream;

        String sisalto = "";

        try {
            inputStream = openFileInput(Player.TIEDNIMI);
            StringBuffer fileContent = new StringBuffer("");
            int n = -1;

            byte[] buffer = new byte[1024];

            while ((n = inputStream.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }

            sisalto = fileContent.toString();
        } catch (Exception e) {
            Log.d(TAG, "Exception 1.");
            finish();
        }

        String rivit[];
        // Jaetaan sisältö taulukoihin, mutta tarkistetaan ensin että sisältö ei ole tyhjä
        if(sisalto.length() > 0)
            rivit = sisalto.split("\n");
        else
            rivit = new String[0];

        dogs = new Player[rivit.length];

        for(int i = 0;i < rivit.length;i++)
        {
            String tiedot[] = rivit[i].split("/");

            String[] ajat = tiedot[3].split("-");
            int vuosi = Integer.parseInt(ajat[2]);
            int kuukausi = Integer.parseInt(ajat[1]);
            int paiva = Integer.parseInt(ajat[0]);
            Date syntyma = new Date(vuosi-1900, kuukausi-1, paiva);
            int pelatutPelit = Integer.parseInt(tiedot[4]);
            int korkeinTaso = Integer.parseInt(tiedot[5]);
            int sukupuoli = Integer.parseInt(tiedot[6]);
            double keskiarvo = Double.parseDouble(tiedot[7]);

            dogs[i] = new Player(tiedot[0], tiedot[1], tiedot[2], syntyma, pelatutPelit, korkeinTaso, sukupuoli, keskiarvo);
            dogs[i].lueHistoria(getApplicationContext());
        }
        lueNimet();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Kun pelaajanluonti-ikkunasta poistutaan, luetaan pelaajalista uudelleen
        if(resultCode == PELAAJALUOTU) {
            luePelaajat();
            ListView list = (ListView) findViewById(R.id.dogList);
            ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
            list.setAdapter(adapter);
        }
        if(resultCode == PELIOHI)
        {
            final int level = (int)data.getIntExtra("korkeinTaso", 0);
            final double pisteet = (double)data.getFloatExtra("pisteet", 0);
            if (level > valittu.korkeinTaso())
                valittu.korkeinTaso(level);

            valittu.paivitaHistoria((int) pisteet);
            valittu.pelatutPelit(valittu.pelatutPelit() + 1);

            // Luodaan File-olio pistehistorian tallentamista varten
            File pistetiedosto = new File(getApplicationContext().getFilesDir(), valittu.fileName());
            valittu.tallennaHistoria(pistetiedosto);

            // Tallennetaan profiilitiedostoon päivitetyt pisteet
            tallennaPelaajat();
        }
    }

    public void tallennaPelaajat()
    {
        File file = new File(getApplicationContext().getFilesDir(), Player.TIEDNIMI);

        try {
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for(int i = 0;i < dogs.length;i++) {
                bufferedWriter.write(dogs[i].toString() + "\n");
            }
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
