package com.example.teisko.dogmemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

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

        // Luodaan otsikkolista
        createList();
        ListAdapter dogsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        ListView dogList = (ListView)findViewById(R.id.dogList);
        dogList.setAdapter(dogsAdapter);
        dogList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent avaus = new Intent(view.getContext(), PlayerInfo.class);
                avaus.putExtra("Pelaaja", dogs[position]);
                startActivityForResult(avaus, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Jos profiili on poistettu
        if(resultCode == 1) {
            Player[] uusiLista = new Player[dogs.length-1];
            LinkedList<Player> apulista = new LinkedList();
            final Player poistettu = (Player)data.getSerializableExtra("Poistettu");

            //Lisätään apulistaan kaikki paitsi poistettu pelaaja
            for(int i = 0;i < dogs.length;i++) {
                if(!dogs[i].dogName().equals(poistettu))
                {
                    apulista.push(dogs[i]);
                }
            }

            // Lisätään apulistalta pelaajat uuteen taulukkoon
            Iterator<Player> iteraattori = apulista.listIterator(0);
            for(int i = 0;i < uusiLista.length;i++)
            {
                uusiLista[i] = iteraattori.next();
            }

            // Vaihdetaan uusi pelaajataulukko
            dogs = uusiLista;
            createList();
            sortList();
            tallennaPelaajat();
            ListView dogList = (ListView) findViewById(R.id.dogList);
            ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
            dogList.setAdapter(adapter);
            dogList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Intent avaus = new Intent(view.getContext(), PlayerInfo.class);
                    avaus.putExtra("Pelaaja", dogs[position]);
                    startActivityForResult(avaus, 1);
                }
            });
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
        }
        sortList();
        createList();
    }

    private void sortList()
    {
        Player apu;
        for(int i = 0;i < dogs.length - 1;i++)
        {
            for(int j = dogs.length - 2;j > i;j--)
            {
                if(dogs[j].korkeinTaso() < dogs[j+1].korkeinTaso())
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
                "\n\tKeskipisteet viimeiseltä 5 peliltä: " + p.keskiarvo();

        return title;
    }

    private void createList()
    {
        titles = new String[dogs.length];
        for(int i = 0;i < dogs.length;i++)
        {
            titles[i] = createTitle(dogs[i]);
        }
    }
}
