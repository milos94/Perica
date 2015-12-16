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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Fajlovi extends ListActivity {

    private Socket client;
    ObjectOutputStream printer;
    ObjectInputStream writer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fajlovi);

        //final ListView llvv= (ListView) findViewById(R.id.list);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    List<String> lista=new ArrayList<String>();
                    Context ctx = getBaseContext();

                    client=new Socket(Razmena.getDefaults("IP",getApplicationContext()),Integer.parseInt(Razmena.getDefaults("PORT",getApplicationContext())));
                    printer = new ObjectOutputStream(client.getOutputStream());
                    writer= new ObjectInputStream(client.getInputStream());
                    do {
                        try {
                            String poruka = (String) writer.readObject();
                            lista.add(poruka);
                        }catch (Exception e){}
                    }while (!(lista.contains("END")));
                    String[] ss= new String[lista.size()];
                    for(int i=0;i<lista.size();i++)
                    {
                        ss[i]=lista.get(i).toString();
                    }
                    setListAdapter(new ArrayAdapter<String>(ctx, android.R.layout.simple_list_item_1, ss));

                }catch (Exception e){e.printStackTrace();}

            }
        }).start();

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
