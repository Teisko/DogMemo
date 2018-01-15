package com.example.teisko.dogmemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.FileInputStream;
import java.util.Date;

public class Playerlist extends AppCompatActivity {

    // Vakioita
    private static final String TAG = "Playerlist";

    // Attribuutteja
    private String[] titles;
    private Player[] dogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playerlist);

        // Luetaan koiralista tiedostosta
        luePelaajat();
        createList();
        ListAdapter dogsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        ListView dogList = (ListView)findViewById(R.id.dogList);
        dogList.setAdapter(dogsAdapter);
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
        // tai muuten tapahtuu hirvittäviä asioita
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
            Date syntyma = new Date(vuosi, kuukausi, paiva);
            int pisteet = Integer.parseInt(tiedot[4]);
            int korkeinTaso = Integer.parseInt(tiedot[5]);
            int sukupuoli = Integer.parseInt(tiedot[6]);


            dogs[i] = new Player(tiedot[0], tiedot[1], tiedot[2], syntyma, pisteet, korkeinTaso, sukupuoli);
        }
    }

    private void sortList()
    {
        Player apu;
        for(int i = 0;i < dogs.length - 1;i++)
        {
            for(int j = dogs.length - 2;j > i;j--)
            {
                if(dogs[j].korkeinTaso() <= dogs[j+1].korkeinTaso())
                {
                    apu = dogs[j+1];
                    dogs[j+1] = dogs[j];
                    dogs[j] = apu;
                }
            }
        }
    }

    private String createTitle(Player p)
    {
        String title = "";
        title += p.dogName() + "\n\tKorkein saavutettu taso: " + p.korkeinTaso() +
                "\n\tKeskipisteet viimeiseltä 10 peliltä: " + p.keskiarvo();

        return title;
    }

    private String[] createList()
    {
        String[] lista = new String[dogs.length];
        for(int i = 0;i < lista.length;i++)
        {
            lista[i] = createTitle(dogs[i]);
        }
        return lista;
    }
}
