package com.example.milos.perica;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;

import android.widget.Toast;

import models.HandlerPoruka;
import models.Konekcija;
import models.Poruka;
import models.Pomocna;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    Konekcija konekcija;
    ListView listView;
    ArrayAdapter<Poruka> adapter;
    public static String cache,pozicija;
    private Menu mn;
    private MenuItem mi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cache=pozicija="";
        inicjalizacijaListe();
        adapter=new ArrayAdapter<Poruka>(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                new ArrayList<Poruka>());
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what) {
                    case Pomocna.OK:
                        ArrayList<Poruka> listaPoruka = (ArrayList<Poruka>) msg.obj;
                        adapter = new ArrayAdapter<Poruka>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,
                            listaPoruka);
                        listView.setAdapter(adapter);
                        break;
                    case Pomocna.ZATVARANJE_KONEKCIJE:
                        Toast.makeText(getApplicationContext(), "Konekcija je ugasena!", Toast.LENGTH_LONG).show();
                        mn.getItem(0).setIcon(R.drawable.plug42);
                        konekcija=null;
                        break;
                    case Pomocna.GRESKA_NA_SERVERU:
                        konekcija=null;
                        Toast.makeText(getApplicationContext(), "Dogodila se greska na serveru!", Toast.LENGTH_LONG).show();
                        mn.getItem(0).setIcon(R.drawable.plug42);
                        break;
                    case Pomocna.GRESKA_PRI_KONEKTOVANJU:
                        konekcija=null;
                        Toast.makeText(getApplicationContext(), "Dogodila se greska prilikom konekcije!", Toast.LENGTH_LONG).show();
                        mn.getItem(0).setIcon(R.drawable.plug42);
                        break;
                    case Pomocna.NEUSPELO_BRISANJE:
                        Toast.makeText(getApplicationContext(), "Ne mozete da obrisete taj fajl/folder!", Toast.LENGTH_LONG).show();
                        mn.getItem(0).setIcon(R.drawable.plug42);
                        break;
                    case Pomocna.USPESNO_SKIDANJE_FAJLA:
                        Toast.makeText(getApplicationContext(), "Uspesno preuzet fajl!", Toast.LENGTH_LONG).show();
                        break;
                    case Pomocna.NEUSPELO_SKIDANJE_FAJLA:
                        Toast.makeText(getApplicationContext(), "Greska pri preuzimanju fajla", Toast.LENGTH_LONG).show();
                        break;
                    case Pomocna.USPESNO_KONEKTOVANJE_NA_SERVER:
                        Toast.makeText(getApplicationContext(),"Konekcija sa serverom je uspostavljena",Toast.LENGTH_LONG).show();
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
        mn = menu;
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
        if(id==R.id.paljenje) {
            if(konekcija==null) {
                konekcija = new Konekcija(this, handler);
                konekcija.start();
                pozicija="";
                cache="";
                mn.getItem(0).setIcon(R.drawable.plugon);

            }
            else{
                konekcija.posaljiPoruku(new Poruka("SHUTDOWN","",""));
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
                pozicija=Pomocna.OtvaranjeFajla(adapter.getItem(position), konekcija, pozicija);
                }
            });
        //onItemLongClickListener otvara pomocni meni sa dodatnim opcijama
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                kreiranjePomocnogMenija(view, adapter.getItem(position));
                return false;
            }
        });
    }



    //Kreiranje PopUp menija sa dodatnim opcijama
    private void kreiranjePomocnogMenija(View v, final Poruka poruka){
        final PopupMenu popupMenu=new PopupMenu(this,v);
        getMenuInflater().inflate(R.menu.pomocni_meni,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //treba da se dodaju metode za brisanje, kopiranje, isecanje i nalepljivanje
                if(konekcija==null) return false;
                switch (item.getItemId()) {
                    case R.id.Otvori:
                        pozicija=Pomocna.OtvaranjeFajla(poruka,konekcija,pozicija);
                        break;
                    case R.id.Obrisi:
                        Pomocna.ObrisiFajl(poruka, konekcija, pozicija);
                        break;
                    case R.id.Preuzmi:
                        Pomocna.PreuzmiFajl(poruka, konekcija, pozicija);
                        break;
                    default:
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

}

