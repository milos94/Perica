package com.example.milos.perica;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import models.Konekcija;
import models.Poruka;

public class MainActivity extends AppCompatActivity {

    public static List<Poruka> lista= new ArrayList<Poruka>();
    Konekcija konekcija;
    ListView listView;
    ArrayAdapter<Poruka> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter=new ArrayAdapter<Poruka>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                MainActivity.lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        konekcija=null;
        listView=(ListView) findViewById(R.id.list1);
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public  void StampajListu(Context ctx){


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           Intent intent= new Intent(this,Podesavanja.class);
           startActivity(intent);
        }
        if(id==R.id.paljeje) {

            if(konekcija==null) {
               // MainActivity.this.runOnUiThread(new Konekcija(getApplicationContext(),listView));
                //konekcija= new Konekcija(getApplicationContext(),listView);
                new MojaKlasa().execute();
                //konekcija.start();
                return true;
            }
            else{
                try {
                    konekcija.kraj();
                    konekcija=null;
                }catch (IOException e){e.printStackTrace();}
                catch (Exception e){e.printStackTrace();}
            }
        }

        return super.onOptionsItemSelected(item);
    }
    
    class MojaKlasa extends  AsyncTask<Void, Poruka, Poruka>{
        private Konekcija kon;
        private ArrayAdapter<Poruka> adapter;
        @Override
        protected void onPreExecute(){
            adapter=(ArrayAdapter<Poruka>)listView.getAdapter();
            kon=new Konekcija(getApplicationContext());
            kon.start();
        }
        @Override
        protected Poruka doInBackground(Void... params) {
            while(true){
                kon.uzmi();
                onProgressUpdate(kon.uzmi());
            }
        }

        protected void onProgressUpdate(Poruka params){
            adapter.add(params);
        }
    }
}
