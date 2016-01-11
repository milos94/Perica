package models;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.example.milos.perica.Razmena;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.List;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by Milos on 21/12/2015.
 */
public class Konekcija extends Thread{

    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;

    volatile boolean radi;
    List<Poruka> lista;
    Context ctx;
    Message msg;
    Handler handler;


    //Mislim da konstruktor nije potrebno objašnjavati
    public Konekcija(Context ct, Handler hand){
        lista=new ArrayList<Poruka>();
        radi=false;
        ctx=ct;
        this.handler=hand;
    }



    //run šta više reći
    public void run(){
        try{
            pokreni();
            while(radi){
                Poruka p= (Poruka) inputStream.readObject();
                if(p.getFajl().equals("START")){
                    lista.clear();continue;}
                else if(p.getFajl().equals("END")){
                    msg=Message.obtain();
                    msg.what= Pomocna.OK;
                    msg.obj=lista;
                    handler.sendMessage(msg);
                    continue;
                }
                else if(p.getFajl().equals("NE")){
                    msg=Message.obtain();
                    msg.what= Pomocna.NEUSPELO_BRISANJE;
                    handler.sendMessage(msg);
                }
                else if(p.getFajl().equals("SHUTDOWN")){
                    kraj();
                    msg=Message.obtain();
                    msg.what= Pomocna.ZATVARANJE_KONEKCIJE;
                    handler.sendMessage(msg);
                    break;}
                else if(p.getKommanda().equals("GIVE")){
                    preuzimanje(Integer.parseInt(p.getFajl()),p.getEkstenzija());
                }
                else{ lista.add(p);}
            }
        }
        catch (Exception e){
            e.printStackTrace();
            msg=Message.obtain();
            msg.what= Pomocna.GRESKA_NA_SERVERU;
            handler.sendMessage(msg);}
    }



    //Metoda za slanje poruke u obliku string
    public void posaljiPoruku(Poruka p){
        try {
            outputStream.writeObject(p);
            outputStream.flush();
        }catch (IOException e){e.printStackTrace();}
        catch (Exception e){e.printStackTrace();}
    }



    //Metoda koja zatvara socket i strimove
    public void kraj() throws IOException{
        radi=false;
        inputStream.close();
        outputStream.close();
        socket.close();
    }



    //Metoda koja pokreće konekciju odnosno vezuje soket i strimove
    public void pokreni() {
        try{
            socket=new Socket(Razmena.getDefaults("IP",ctx),
                    Integer.parseInt(Razmena.getDefaults("PORT", ctx)));
            inputStream=new ObjectInputStream(socket.getInputStream());
            outputStream=new ObjectOutputStream(socket.getOutputStream());
            radi=true;
            msg=Message.obtain();
            msg.what=Pomocna.USPESNO_KONEKTOVANJE_NA_SERVER;
        }catch (Exception e){
            e.printStackTrace();
            msg=Message.obtain();
            msg.what= Pomocna.GRESKA_PRI_KONEKTOVANJU;
            handler.sendMessage(msg);}
    }


    //Metoda za preuzimanje Fajla
    private void preuzimanje(int velicina, String naziv){
        try {
            InputStream io = (InputStream) inputStream;

            File file=null;

            if (pristupSpoljnojMemoriji())
            {
                 file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),naziv);
            }
            else
            {
                file = new File(ctx.getFilesDir(),naziv);
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos=new BufferedOutputStream(fos);
            byte [] mybytearray  = new byte [velicina+10];
            int procitano,trenutno;
            procitano=io.read(mybytearray,0,mybytearray.length);
            trenutno=procitano;
            do{
                procitano=io.read(mybytearray, trenutno, (mybytearray.length-trenutno));
                if(procitano >= 0) trenutno += procitano;
            }while (procitano>-1);
            bos.write(mybytearray, 0, trenutno);
            bos.flush();
            bos.close();
            fos.close();

        }catch (Exception e) {
            e.printStackTrace();
            msg = Message.obtain();
            msg.what = Pomocna.NEUSPELO_SKIDANJE_FAJLA;
            handler.sendMessage(msg);
            return;
        }
    }


    public boolean pristupSpoljnojMemoriji() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }



}
