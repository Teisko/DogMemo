package com.example.teisko.dogmemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class PlayerInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);

        final Player tutkittava = (Player)getIntent().getSerializableExtra("Pelaaja");
        TextView kNimi = (TextView)findViewById(R.id.kNimi);
        TextView oNimi = (TextView)findViewById(R.id.oNimi);
        TextView rotu = (TextView)findViewById(R.id.rotu);
        TextView ika = (TextView)findViewById(R.id.ika);
        TextView sukupuoli = (TextView)findViewById(R.id.sukupuoli);
        TextView pisteet = (TextView)findViewById(R.id.pisteet);
        TextView pelatut = (TextView)findViewById(R.id.pelatut);
        TextView korkeinTaso = (TextView)findViewById(R.id.korkeinTaso);
        Button button = (Button)findViewById(R.id.button);
        TextView textView13 = (TextView)findViewById(R.id.textView13);

        textView13.setText("Pisteet on laskettu ottamalla keskiarvo viimeisen viiden\npelikerran onnistumisprosenteista.");

        kNimi.setText("Koiran nimi: " + tutkittava.dogName());
        oNimi.setText("Omistajan nimi: " + tutkittava.ownerName());
        rotu.setText("Rotu: " + tutkittava.rotu());
        ika.setText("Syntymäpäivä: " + tutkittava.syntymaTeksti());
        System.out.println("Synttäri: " + tutkittava.syntymaTeksti());
        System.out.println("ika: " + ika);
        if(tutkittava.sukupuoli() == 0)
            sukupuoli.setText("Sukupuoli: Narttu");
        else
            sukupuoli.setText("Sukupuoli: Uros");
        pisteet.setText("Pisteet: " + tutkittava.keskiarvo());
        pelatut.setText("Pelatut pelit: " + tutkittava.pelatutPelit());
        korkeinTaso.setText("Korkein saavutettu taso: " + tutkittava.korkeinTaso());

        setResult(-1, new Intent());

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try {
                    // Luodaan File-olio sitä vastaavan tiedoston poistamiseksi
                    File pistetiedosto = new File(getApplicationContext().getFilesDir(), tutkittava.fileName());

                    pistetiedosto.delete();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                // Luodaan viesti profiilin poistamisesta käyttäjän nähtäväksi
                Context context = getApplicationContext();
                CharSequence text = "Pelaajaprofiili poistettu.";
                int duration;
                duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                Intent output = new Intent();
                output.putExtra("Poistettu", tutkittava);
                setResult(1, output);
                finish();
            }
        });
    }
}
