package models;

import java.io.Serializable;

/**
 * Created by Milos on 16/12/2015.
 */

public class Poruka implements Serializable {
    static final long serialVersionUID=-189968662588969806L;
    private String komanda;
    private String fajl;
    private String[] argumenti;
    private String ekstenzija;
    public Poruka(){ }
    public Poruka(String kom, String faj, String ext, String... arg){
        komanda=kom;
        fajl=faj;
        ekstenzija=ext;
        argumenti= new String[arg.length];
        System.arraycopy(arg, 0, argumenti, 0, arg.length);
    }
    public Poruka(String s, String s1){ komanda=s; fajl=s1;}
    public Poruka(String s){ fajl=s;}

    public String getEkstenzija() {
        return ekstenzija;
    }

    public String getKommanda() {
        return komanda;
    }

    public String getFajl() {
        return fajl;
    }

    public String[] getArgumenti() {
        return argumenti;
    }
    public String toString(){
        return fajl;
    }
    public void setFajl(String s){
        fajl=s;
    }
    public void setKommanda(String s){komanda=s;}
}
