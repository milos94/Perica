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
import android.widget.PopupMenu;
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
import models.Poruke;

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
                    case Poruke.OK:
                        ArrayList<Poruka> listaPoruka = (ArrayList<Poruka>) msg.obj;
                        adapter = new ArrayAdapter<Poruka>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            listaPoruka);
                        listView.setAdapter(adapter);
                        break;

                    case Poruke.ZATVARANJE_KONEKCIJE:
                        konekcija=null;
                        //treba da se doda obavestenje da je prekinuta veza sa serverom
                        break;
                    case Poruke.GRESKA_NA_SERVERU:
                        konekcija=null;
                        //treba da se doda obavestenje da je doslo do greske
                        break;
                    case Poruke.GRESKA_PRI_KONEKTOVANJU:
                        konekcija=null;
                        //treba da se doda obavestenje da je doslo do greske pri konektovanju
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


    //Kreiranje ListViewa i postavaljanje Listenera
    private void inicjalizacijaListe(){
        listView=(ListView) findViewById(R.id.listView);
        //onItemClickListenere podrazumevano otvara odabrani fajl ili folder
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (konekcija == null) return;
                OtvaranjeFajla(adapter.getItem(position));
                }
            });
        //onItemLongClickListener otvara pomocni meni sa dodatnim opcijama
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                kreiranjePomocnogMenija(view,adapter.getItem(position));
                return false;
            }
        });
    }



    //Kreiranje PopUp menija sa dodatnim opcijama
    private void kreiranjePomocnogMenija(View v, final Poruka poruka){
        PopupMenu popupMenu=new PopupMenu(this,v);
        getMenuInflater().inflate(R.menu.pomocni_meni,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //treba da se dodaju metode za brisanje, kopiranje, isecanje i nalepljivanje
                switch (item.getItemId()){
                    case R.id.Otvori: OtvaranjeFajla(poruka); break;
                    case R.id.Obrisi:  break;
                    case R.id.Kopiraj: break;
                    case R.id.Iseci:  break;
                    case R.id.Nalepi:  break;
                    default: return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }




    //Obrada izabranog fajla iz liste
    private void OtvaranjeFajla(Poruka poruka){
        if(poruka.getFajl().equals("..")){
            String[] niz=pozicija.split(String.valueOf('\\'));
            pozicija="";
            for(int i=0;i<niz.length-1;i++){
                pozicija+=niz[i]+"\\";
            }
            return;
        }
        switch (poruka.getEkstenzija()){
            case "":pozicija+=(pozicija.equals(""))?poruka.toString():'\\'+poruka.toString();
                konekcija.posaljiPoruku("dir "+pozicija); return;
            default: konekcija.posaljiPoruku(pozicija+((pozicija.equals(""))?poruka.toString():'\\'+poruka.toString())); return;
        }
    }





}
