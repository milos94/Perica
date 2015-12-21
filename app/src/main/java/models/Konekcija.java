package models;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.milos.perica.MainActivity;
import com.example.milos.perica.Razmena;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milos on 21/12/2015.
 */
public class Konekcija extends Thread {
    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    List<Poruka> lista;
    ListView listView;
    ArrayAdapter<Poruka> adapter;
    Context ctx;
    MainActivity main;
    volatile boolean radi;
    public Konekcija(ListView lv,Context ct,MainActivity mn){
        main=mn;
        listView= (ListView) lv.findViewById(android.R.id.list);
        radi=true;
        ctx=ct;
        lista= new ArrayList<Poruka>();
    }


    public void run(){
        try{
            socket = new Socket(Razmena.getDefaults("IP", ctx),
                                Integer.parseInt(Razmena.getDefaults("PORT", ctx)));
            inputStream= new ObjectInputStream(socket.getInputStream());
            outputStream=new ObjectOutputStream(socket.getOutputStream());
            while(radi){
                Poruka p=(Poruka) inputStream.readObject();
                if(p.getFajl().equals("START")){ lista.clear();}
                else if(p.getFajl().equals("END")){

                    main.odstamapajListu(lista);
                }
                else if(p.getFajl().equals("SHUTDOWN")){
                    kraj();
                    break;}
                else{ lista.add(p);}
            }
        }catch (IOException e){ e.printStackTrace();}
        catch (Exception e){e.printStackTrace();}
    }
    public void posaljiPoruku(Poruka p){
        try {
            outputStream.writeObject(p);
        }catch (IOException e){e.printStackTrace();}
        catch (Exception e){e.printStackTrace();}
    }
    public void kraj() throws IOException{
        radi=false;
        outputStream.writeObject("SHUTDOWN");
        inputStream.close();
        outputStream.close();
        lista.clear();
        socket.close();
    }
}
