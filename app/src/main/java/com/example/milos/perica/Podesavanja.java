package com.example.milos.perica;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Podesavanja extends AppCompatActivity {
    Button sacuvaj;
    EditText tip, tport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences shpf= getSharedPreferences("Gotovo",MODE_PRIVATE);
        setContentView(R.layout.activity_podesavanja);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sacuvaj=(Button) findViewById(R.id.sacuvaj);
        tip=(EditText) findViewById(R.id.teip);
        tport=(EditText) findViewById(R.id.teport);
        tip.setText(Razmena.getDefaults("IP", getApplicationContext()));
        tport.setText(Razmena.getDefaults("PORT",getApplicationContext()));
        sacuvaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences shpf= getSharedPreferences("Gotovo",MODE_PRIVATE);
                SharedPreferences.Editor edit =  shpf.edit();
                Razmena.setDefaults("IP", tip.getText().toString(),getApplicationContext());
                Razmena.setDefaults("PORT", tport.getText().toString(), getApplicationContext());
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
