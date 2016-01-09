package models;

import com.example.milos.perica.MainActivity;

/**
 * Created by Milos on 06/01/2016.
 */
public class Pomocna {
    //staticke promenjive koje se korisete za poruke
    public static final int OK=0;
    public static final int GRESKA_PRI_KONEKTOVANJU=1;
    public static final int GRESKA_NA_SERVERU=2;
    public static final int ZATVARANJE_KONEKCIJE=3;
    public static final int NEUSPELO_BRISANJE=4;
    public static final int NEUSPELO_SKIDANJE_FAJLA=5;
    public static final int USPESNO_SKIDANJE_FAJLA=6;
    public static final int USPESNO_KONEKTOVANJE_NA_SERVER=7;



    //Obrada izabranog fajla iz liste
    public static String OtvaranjeFajla(Poruka poruka, Konekcija konekcija,String pozicija){
        if(poruka.getFajl().equals("..")){
            String[] niz= pozicija.split("\\\\");
            pozicija="";
            for(int i=0;i<niz.length-1;i++){
                pozicija+=niz[i]+"\\";
            }
            konekcija.posaljiPoruku(new Poruka("dir",pozicija,"nema"));
            return pozicija;
        }
        switch (poruka.getEkstenzija()){
            case "":case "DIR":pozicija+=(pozicija.equals(""))?poruka.toString():poruka.toString()+"\\";
                konekcija.posaljiPoruku(new Poruka("dir",pozicija,"nema"));
                return pozicija;
            default: konekcija.posaljiPoruku(new Poruka("run",
                    pozicija+((pozicija.equals(""))?poruka.toString():'\\'+poruka.toString()))); return pozicija;
        }
    }

    public static void ObrisiFajl(Poruka poruka,Konekcija konekcija,String pozicija){
        konekcija.posaljiPoruku(new Poruka("delete",
                pozicija+((pozicija.equals(""))?poruka.toString():'\\'+poruka.toString())));
    }

    public static void PreuzmiFajl(Poruka poruka,Konekcija konekcija,String pozicija){
        konekcija.posaljiPoruku(new Poruka("take",
                pozicija+((pozicija.equals(""))?poruka.toString():'\\'+poruka.toString())));
    }
}



