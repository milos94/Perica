package models;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.milos.perica.R;

import java.util.ArrayList;

/**
 * Created by Milos on 09/01/2016.
 */
public class HandlerPoruka extends Handler {
    private ListView listView;
    private Context ctx;
    private Konekcija konekcija;
    private Menu mn;
    private ArrayAdapter adapter;
    public HandlerPoruka(ListView listView,Context context, Konekcija konekcija, Menu menu,ArrayAdapter adapter){
        this.listView=listView;
        this.ctx=context;
        this.konekcija=konekcija;
        this.mn=menu;
        this.adapter=adapter;
    }
    @Override
    public void handleMessage(Message msg){
        switch (msg.what) {
            case Pomocna.OK:
                ArrayList<Poruka> listaPoruka = (ArrayList<Poruka>) msg.obj;

                listView.setAdapter(adapter);
                break;
            case Pomocna.ZATVARANJE_KONEKCIJE:
                Toast.makeText(ctx, "Konekcija je ugasena!", Toast.LENGTH_LONG).show();
                mn.getItem(0).setIcon(R.drawable.plugoff);
                konekcija=null;
                break;
            case Pomocna.GRESKA_NA_SERVERU:
                konekcija=null;
                Toast.makeText(ctx, "Dogodila se greska na serveru!", Toast.LENGTH_LONG).show();
                mn.getItem(0).setIcon(R.drawable.plugoff);
                break;
            case Pomocna.GRESKA_PRI_KONEKTOVANJU:
                konekcija=null;
                Toast.makeText(ctx, "Dogodila se greska prilikom konekcije!", Toast.LENGTH_LONG).show();
                mn.getItem(0).setIcon(R.drawable.plugoff);
                break;
            case Pomocna.NEUSPELO_BRISANJE:
                Toast.makeText(ctx, "Ne mozete da obrisete taj fajl/folder!", Toast.LENGTH_LONG).show();
                mn.getItem(0).setIcon(R.drawable.plugoff);
                break;
            case Pomocna.USPESNO_SKIDANJE_FAJLA:
                Toast.makeText(ctx, "Uspesno preuzet fajl!", Toast.LENGTH_LONG).show();
                break;
            case Pomocna.NEUSPELO_SKIDANJE_FAJLA:
                Toast.makeText(ctx, "Greska pri preuzimanju fajla", Toast.LENGTH_LONG).show();
                break;
            case Pomocna.USPESNO_KONEKTOVANJE_NA_SERVER:
                Toast.makeText(ctx,"Konekcija sa serverom je uspostavljena",Toast.LENGTH_LONG).show();
                break;
            default:
                super.handleMessage(msg);
                break;
        }
    }
}
