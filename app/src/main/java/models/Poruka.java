package models;

import java.io.Serializable;

/**
 * Created by Milos on 16/12/2015.
 */

public class Poruka implements Serializable {
    static final long serialVersionUID=-1191781795339130420L;
    private String kommanda;
    private String fajl;
    private String[] argumenti;
    private String ekstenzija;
    public Poruka(){ }
    public Poruka(String kom, String faj, String ext, String[] arg,String... args){
        kommanda=kom;
        fajl=faj;
        ekstenzija=ext;
        argumenti= new String[arg.length];
        for(int i=0;i<arg.length;i++){
            argumenti[i]=arg[i];
        }
    }
    public  Poruka(String s, String s1){ fajl=s; ekstenzija=s1;}
    public Poruka(String s){ fajl=s;}
    public String getEkstenzija() {
        return ekstenzija;
    }

    public String getKommanda() {
        return kommanda;
    }

    public String getFajl() {
        return fajl;
    }

    public String[] getArgumenti() {
        return argumenti;
    }
    public String toString(){
        return fajl+ekstenzija;
    }
    public void setFajl(String s){
        fajl=s;
    }
}
