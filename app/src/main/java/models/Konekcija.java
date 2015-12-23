package models;

import android.content.Context;
import android.os.AsyncTask;
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
public class Konekcija extends Thread{
    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    volatile boolean radi;
    List<Poruka> lista;
    Context ctx;
    ArrayAdapter<Poruka> adapter;
    ListView listView;


    public Konekcija(Context ct){
        lista=new ArrayList<Poruka>();
        radi=true;
        ctx=ct;
        this.listView=listView;
    }



    public void run(){
        try{
            socket = new Socket(Razmena.getDefaults("IP", ctx),
                    Integer.parseInt(Razmena.getDefaults("PORT", ctx)));
            inputStream= new ObjectInputStream(socket.getInputStream());
            outputStream=new ObjectOutputStream(socket.getOutputStream());
           /* while(radi){
                Poruka p= new Poruka();
                p=(Poruka) inputStream.readObject();
                if(p.getFajl().equals("START")){ lista.clear();}
                else if(p.getFajl().equals("END")){
                    ArrayAdapter<Poruka> adapter=new ArrayAdapter<Poruka>(ctx,
                            android.R.layout.simple_list_item_1,
                            lista);
                    listView.setAdapter(adapter);
                    //Treba da se odstampa lista...
                }
                else if(p.getFajl().equals("SHUTDOWN")){
                    kraj();
                    break;}
                else{ lista.add(p);}
            }*/
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
        inputStream.close();
        outputStream.close();
        socket.close();
    }


    public Poruka uzmi() {
        try {
            return (Poruka) inputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
