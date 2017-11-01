package com.example.teisko.dogmemo;

/**
 * Created by Sampo on 13.10.2017.
 */

public class Player {
    private String dogName;
    private String ownerName;
    private String rotu;
    private int syntymapaiva;                 // MUUTETTAVA
    private int pisteet;
    private int korkeinTaso;
    private int sukupuoli;

    /* Constructors */

    /** Constructor for class Player */
    public Player(String d, String o, String r, int syntyma, int p, int k, int sp) throws
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

    public void dogName(String d)
    {

    }

    public String dogName()
    {
        return dogName;
    }

    public void ownerName(String o)
    {

    }

    public String ownerName()
    {
        return ownerName;
    }

    public void rotu(String r)
    {

    }

    public String rotu()
    {
        return rotu;
    }

    public void syntymapaiva(int syntyma)
    {

    }

    public int syntymapaiva()
    {
        return syntymapaiva;
    }

    public void pisteet(int p)
    {

    }

    public int pisteet()
    {
        return pisteet;
    }

    public void korkeinTaso(int k)
    {

    }

    public int korkeinTaso()
    {
        return korkeinTaso;
    }

    public void sukupuoli(int sp)
    {

    }

    public int sukupuoli()
    {
        return sukupuoli;
    }
}
