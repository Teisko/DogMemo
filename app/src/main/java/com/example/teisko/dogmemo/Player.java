package com.example.teisko.dogmemo;

import java.util.Date;

/**
 * Created by Sampo on 2017-10-13.
 * Edited by Tuukka on 2017-11-06.
 */

public class Player {
    private String dogName;
    private String ownerName;
    private String rotu;
    private Date syntymapaiva;
    private int pisteet;
    private int korkeinTaso;
    private boolean sukupuoli;

    /* Constructors */

    /** Constructor for class Player */
    public Player(String d, String o, String r, Date syntyma, int p, int k, boolean sp) throws
            IllegalArgumentException
    {
        dogName(d);
        ownerName(o);
        rotu(r);
        syntymapaiva(syntyma);
        pisteet(p);
        korkeinTaso(k);
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

    private void sukupuoli(boolean sp)
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

    public boolean sukupuoli()
    {
        return sukupuoli;
    }
}
