package models;

import java.io.Serializable;

/**
 * Created by Milos on 16/12/2015.
 */
public class Poruka implements Serializable {
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
}
