package com.example.milos.perica;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import android.widget.AbsListView.MultiChoiceModeListener;

import models.Konekcija;
import models.Poruka;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    Konekcija konekcija;
    ListView listView;
    ArrayAdapter<Poruka> adapter;
    String cache,pozicija;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cache=pozicija="";
        inicjalizacijaListe();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what) {
                    case 1:
                        ArrayList<Poruka> listaPoruka = (ArrayList<Poruka>) msg.obj;
                        adapter = new ArrayAdapter<Poruka>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            listaPoruka);
                        listView.setAdapter(adapter);
                        break;

                    case 2:
                        konekcija=null;
                        //treba da se doda obavestenje da je prekinuta veza sa serverom
                        break;

                    default:
                    super.handleMessage(msg);
                        break;
                }
            }
        };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
           Intent intent= new Intent(this,Podesavanja.class);
           startActivity(intent);
        }
        if(id==R.id.paljeje) {
            if(konekcija==null) {
                konekcija = new Konekcija(this, handler);
                konekcija.start();
                //treba da se doda obavestenje da je povezano sa serverom
            }
            else{
                konekcija.posaljiPoruku("SHUTDOWN");
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void inicjalizacijaListe(){
        listView=(ListView) findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if(konekcija==null) return;
                konekcija.posaljiPoruku(adapter.getItem((int) id).toString());
                obradaFajla(adapter.getItem(position));
                int a=1;
                int b=1;
                int c=1;
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.pomocni_meni, menu);
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.Obrisi:
                        konekcija.posaljiPoruku("rm ");
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(konekcija==null) return;
                obradaFajla(adapter.getItem(position));
            }
        });
    }


    //Obrada izabranog fajla iz liste
    private void obradaFajla(Poruka poruka){
        if(poruka.getFajl().equals("..")){
            String[] niz=pozicija.split(String.valueOf('\\'));
            pozicija="";
            for(int i=0;i<niz.length-1;i++){
                pozicija+=niz[i]+"\\";
            }
            return;
        }
        switch (poruka.getEkstenzija()){
            case ".txt": konekcija.posaljiPoruku("edit "+pozicija+'\\'+poruka.toString()); return;
            case "":pozicija+='\\'+poruka.toString(); konekcija.posaljiPoruku("dir "+pozicija); return;
            case ".exe": konekcija.posaljiPoruku("run "+pozicija+'\\'+poruka.toString()); return;
            case ".jpg":case ".jpeg":case ".png":case ".bpn": konekcija.posaljiPoruku(pozicija+'\\'+poruka.toString()); return;
            default: konekcija.posaljiPoruku(pozicija+'\\'+poruka.toString()); return;
        }
    }
}
