package com.example.teisko.dogmemo;


import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by Sampo on 2017-10-13.
 * Edited by Tuukka on 2017-11-06.
 */

public class Player implements Serializable{

    // Vakioita
    public static final String TIEDNIMI = "profiles.txt";
    public static final String TAG = "Player";

    // Attribuutteja
    private String dogName;
    private String ownerName;
    private String rotu;
    private Date syntymapaiva;
    private int pelatutPelit;
    private LinkedList<Integer> pisteHistoria; // Viimeisen viiden pelin pisteet
    private double keskiarvo;
    private int korkeinTaso;
    private int sukupuoli; // 0 = narttu, 1 = uros

    /* Constructors */

    /** Constructor for class Player */
    public Player(String d, String o, String r, Date syntyma, int sp) throws
            IllegalArgumentException
    {
        dogName(d);
        ownerName(o);
        rotu(r);
        syntymapaiva(syntyma);
        pelatutPelit(0);
        korkeinTaso(0);
        sukupuoli(sp);
        keskiarvo = 0;
        pisteHistoria = new LinkedList<Integer>();
    }

    /** Constructor for creating new Objects from saved file */
    public Player(String d, String o, String r, Date syntyma, int p, int kt, int sp, double ka) throws
            IllegalArgumentException
    {
        dogName(d);
        ownerName(o);
        rotu(r);
        syntymapaiva(syntyma);
        pelatutPelit(p);
        korkeinTaso(kt);
        sukupuoli(sp);
        keskiarvo(ka);
        pisteHistoria = new LinkedList<Integer>();
    }

    /** Set */
    private void dogName(String d)
    {
        dogName = d;
    }

    private void ownerName(String o)
    {
        ownerName = o;
    }

    private void rotu(String r)
    {
        rotu = r;
    }

    private void syntymapaiva(Date syntyma)
    {
        syntymapaiva = syntyma;
    }

    public void pelatutPelit(int p)
    {
        pelatutPelit = p;
    }

    public void korkeinTaso(int k)
    {
        korkeinTaso = k;
    }

    private void sukupuoli(int sp)
    {
        sukupuoli = sp;
    }

    public void keskiarvo(double ka) { keskiarvo = ka; }

    /** Get */
    public String dogName()
    {
        return dogName;
    }

    public String ownerName()
    {
        return ownerName;
    }

    public String rotu()
    {
        return rotu;
    }

    public Date syntymapaiva()
    {
        return syntymapaiva;
    }

    public int pelatutPelit()
    {
        return pelatutPelit;
    }

    public int korkeinTaso()
    {
        return korkeinTaso;
    }

    public int sukupuoli()
    {
        return sukupuoli;
    }

    public double keskiarvo() {return keskiarvo; }

    public String syntymaTeksti()
    {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(syntymapaiva);
    }

    /** Pistehistorian päivitysmetodi, poistaa viidennen tuloksen ja lisää uusimman */
    public void paivitaHistoria(int uusinTulos)
    {
        if(pisteHistoria.size() > 4)
            pisteHistoria.removeLast();
        pisteHistoria.addFirst(new Integer(uusinTulos));

        laskeKeskiarvo();
    }

    public void tallennaHistoria(File tiedosto)
    {
        try {
            FileWriter fileWriter = new FileWriter(tiedosto);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            Iterator<Integer> iteraattori = pisteHistoria.listIterator(0);
            while(iteraattori.hasNext())
            {
                bufferedWriter.write("" + iteraattori.next().toString() + "\n");
            }
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void lueHistoria(Context context)
    {
        FileInputStream inputStream;

        String sisalto = "";

        try {
            inputStream = context.openFileInput(fileName());
            StringBuffer fileContent = new StringBuffer("");
            int n = -1;

            byte[] buffer = new byte[1024];

            while ((n = inputStream.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }

            sisalto = fileContent.toString();
        } catch (Exception e) {
            Log.d(TAG, "Exception 1.");
        }
        String rivit[] = sisalto.split("\n");
        for(int i = 0;i < rivit.length;i++) {
            if(rivit[i].length() > 0)
                pisteHistoria.add(Integer.parseInt(rivit[i]));
        }
    }

    /** Laskee keskiarvon */
    private void laskeKeskiarvo()
    {
        keskiarvo = 0;
        if(pisteHistoria.size() > 0) {
            ListIterator<Integer> iteraattori = pisteHistoria.listIterator(0);
            while (iteraattori.hasNext()) {
                keskiarvo += (double)iteraattori.next();
            }
        }
        keskiarvo = keskiarvo / (double)pisteHistoria.size();
    }

    public String fileName()
    {
        return dogName + ".txt";
    }

    /** Perityt metodit */
    @Override
    public String toString()
    {
        return dogName + "/" + ownerName + "/" + rotu + "/" + syntymaTeksti() + "/" + pelatutPelit + "/" + korkeinTaso + "/" + sukupuoli + "/" + keskiarvo;
    }
}