package models;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import android.os.Handler;

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

    public Konekcija(Context ct, Handler hand){
        lista=new ArrayList<Poruka>();
        radi=false;
        ctx=ct;
        this.handler=hand;
    }



    public void run(){
        try{
            pokreni();
            while(radi){
                Poruka p= (Poruka) inputStream.readObject();
                if(p.getFajl().equals("START")){ lista.clear(); continue;}
                else if(p.getFajl().equals("END")){
                    msg=Message.obtain();
                    msg.what=1;
                    msg.obj=lista;
                    handler.sendMessage(msg);
                    continue;
                }
                else if(p.getFajl().equals("SHUTDOWN")){
                    kraj();
                    msg=Message.obtain();
                    msg.what=2;
                    handler.sendMessage(msg);
                    break;}
                else{ lista.add(p);}
            }
        }catch (IOException e){ e.printStackTrace();}
        catch (Exception e){e.printStackTrace();}
    }



    public void posaljiPoruku(String p){
        try {
            outputStream.writeObject(p);
            outputStream.flush();
        }catch (IOException e){e.printStackTrace();}
        catch (Exception e){e.printStackTrace();}
    }



    public void kraj() throws IOException{
        radi=false;
        inputStream.close();
        outputStream.close();
        socket.close();
    }


    public void pokreni() {
        radi=true;
        try{
            socket=new Socket(Razmena.getDefaults("IP",ctx),
                    Integer.parseInt(Razmena.getDefaults("PORT", ctx)));
            inputStream=new ObjectInputStream(socket.getInputStream());
            outputStream=new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e){e.printStackTrace();}
    }

    public boolean getRadi(){
        return radi;
    }
}
