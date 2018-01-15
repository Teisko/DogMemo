package com.example.teisko.dogmemo;


import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private int pisteet;
    private LinkedList<Integer> pisteHistoria; // Viimeisen kymmenen pelin pisteet
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
        pisteet(0);
        korkeinTaso(0);
        sukupuoli(sp);
    }

    /** Constructor for creating new Objects from saved file */
    public Player(String d, String o, String r, Date syntyma, int p, int kt, int sp) throws
            IllegalArgumentException
    {
        dogName(d);
        ownerName(o);
        rotu(r);
        syntymapaiva(syntyma);
        pisteet(p);
        korkeinTaso(kt);
        sukupuoli(sp);
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

    private void pisteet(int p)
    {
        pisteet = p;
    }

    private void korkeinTaso(int k)
    {
        korkeinTaso = k;
    }

    private void sukupuoli(int sp)
    {
        sukupuoli = sp;
    }

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

    public int pisteet()
    {
        return pisteet;
    }

    public int korkeinTaso()
    {
        return korkeinTaso;
    }

    public int sukupuoli()
    {
        return sukupuoli;
    }

    private String syntymaTeksti()
    {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(syntymapaiva);
    }

    /** Pistehistorian päivitysmetodi, poistaa kymmenennen tuloksen ja lisää uusimman */
    public void paivitaHistoria(int uusinTulos)
    {
        if(pisteHistoria.size() > 9)
            pisteHistoria.removeLast();
        pisteHistoria.addFirst(new Integer(uusinTulos));
    }

    /** Palauttaa pistehistorian pisteiden keskiarvon muodossa double */
    public double keskiarvo()
    {
        double keskiarvo = 0;
        if(pisteHistoria.size() > 0) {
            ListIterator<Integer> iteraattori = pisteHistoria.listIterator(0);
            while (iteraattori.hasNext()) {
                keskiarvo += (double)iteraattori.next();
            }
        }
        keskiarvo = keskiarvo / (double)pisteHistoria.size();
        return keskiarvo;
    }

    public String fileName()
    {
        return dogName + ".txt";
    }

    /** Perityt metodit */
    @Override
    public String toString()
    {
        return dogName + "/" + ownerName + "/" + rotu + "/" + syntymaTeksti() + "/" + pisteet + "/" + korkeinTaso + "/" + sukupuoli;
    }
}