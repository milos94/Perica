package com.example.milos.perica;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Fajlovi extends ListActivity {

    private Socket client;
    ObjectOutputStream printer;
    ObjectInputStream writer;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fajlovi);

      //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);



        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    ListView lv=(ListView) findViewById(android.R.id.list);
                    List<String> lista=new ArrayList<String>();
                    client = new Socket(Razmena.getDefaults("IP", getApplicationContext()), Integer.parseInt(Razmena.getDefaults("PORT", getApplicationContext())));
                    printer = new ObjectOutputStream(client.getOutputStream());
                    writer = new ObjectInputStream(client.getInputStream());
                    do {
                        try {
                            String poruka = (String) writer.readObject();
                            if(poruka.equals("BEGIN")){
                                lista.clear();
                            }
                            else if(poruka.equals("END")){
                                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getBaseContext(),
                                        android.R.layout.simple_list_item_1,
                                        lista);
                                lv.setAdapter(adapter);

                            }
                            else lista.add(poruka);
                        }catch (Exception e){e.printStackTrace();}
                    }while (true);
                }catch (Exception e){e.printStackTrace();}
            }
        }).start();

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
