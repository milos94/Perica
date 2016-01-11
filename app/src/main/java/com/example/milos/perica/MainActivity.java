package com.example.milos.perica;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.Manifest;

import java.security.Permission;
import java.util.ArrayList;
//import java.util.jar.Manifest;

import android.widget.Toast;

import models.Konekcija;
import models.Poruka;
import models.Pomocna;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    Konekcija konekcija;
    ListView listView;
    ArrayAdapter<Poruka> adapter;
    public static String cache,pozicija;
    private Activity mainact;
    private Poruka porukatransver;
    private Menu mn;
    private static final int PRISTUP_SPOLJNOJ_MEMORIJI_KOD = 9;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cache=pozicija="";
        inicjalizacijaListe();
        final Context ctx = getApplication();
        mainact=this;
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
                        Toast.makeText(ctx, ctx.getString(R.string.KonekcijaUgasena), Toast.LENGTH_LONG).show();
                        mn.getItem(0).setIcon(R.drawable.plugoff);
                        konekcija=null;
                        listView.setAdapter(null);
                        break;
                    case Pomocna.GRESKA_NA_SERVERU:
                        konekcija=null;
                        Toast.makeText(ctx, ctx.getString(R.string.GreskaNaServeru), Toast.LENGTH_LONG).show();
                        mn.getItem(0).setIcon(R.drawable.plugoff);
                        listView.setAdapter(null);
                        break;
                    case Pomocna.GRESKA_PRI_KONEKTOVANJU:
                        konekcija=null;
                        Toast.makeText(ctx, ctx.getString(R.string.GreskaPrilikomKonektovanja), Toast.LENGTH_LONG).show();
                        mn.getItem(0).setIcon(R.drawable.plugoff);
                        listView.setAdapter(null);
                        break;
                    case Pomocna.NEUSPELO_BRISANJE:
                        Toast.makeText(ctx, ctx.getString(R.string.NeuspeloBrisanje), Toast.LENGTH_LONG).show();
                        mn.getItem(0).setIcon(R.drawable.plugoff);
                        break;
                    case Pomocna.USPESNO_SKIDANJE_FAJLA:
                        Toast.makeText(ctx,ctx.getString(R.string.UspesnoPreuzimaje), Toast.LENGTH_LONG).show();
                        break;
                    case Pomocna.NEUSPELO_SKIDANJE_FAJLA:
                        Toast.makeText(ctx, ctx.getString(R.string.NeuspesnoPreuzimanje), Toast.LENGTH_LONG).show();
                        break;
                    case Pomocna.USPESNO_KONEKTOVANJE_NA_SERVER:
                        Toast.makeText(ctx,ctx.getString(R.string.UspesnoKonektovanje),Toast.LENGTH_LONG).show();
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
        if(id==R.id.ukljuc) {
            if(konekcija==null) {
                konekcija = new Konekcija(this, handler);
                konekcija.start();
                pozicija="";
                cache="";
                item.setIcon(R.drawable.plugon);
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
                if (konekcija == null) return false;
                switch (item.getItemId()) {
                    case R.id.Otvori:
                        pozicija = Pomocna.OtvaranjeFajla(poruka, konekcija, pozicija);
                        break;
                    case R.id.Obrisi:
                        Pomocna.ObrisiFajl(poruka, konekcija, pozicija);
                        break;
                    case R.id.Preuzmi:
                        porukatransver = poruka;
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(mainact,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},PRISTUP_SPOLJNOJ_MEMORIJI_KOD);
                        }
                        else {
                            Pomocna.PreuzmiFajl(porukatransver, konekcija, pozicija);
                        }
                        break;
                    default:
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public boolean VerzijaMarshmellow()
    {
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case PRISTUP_SPOLJNOJ_MEMORIJI_KOD:
            {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Pomocna.PreuzmiFajl(porukatransver, konekcija, pozicija);
                else
                    Toast.makeText(getApplicationContext(),"Niste dozvolili pristup memoriji!",Toast.LENGTH_SHORT).show();
            }
            default:
                break;
            //return true;

        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

