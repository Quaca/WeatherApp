package com.example.dtuser3.vrijemeapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.net.InetAddress;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ResultReceiverForecast.Receiver
{
    String grad = "";
    ListView listaRezultat;

    private AdapterForecast adapterForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BazaOpenHelper.getInstance(getApplicationContext());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final Button bPretraga = findViewById(R.id.bPretraga);
        final EditText uneseniTekst = findViewById(R.id.tekst);
        listaRezultat = findViewById(R.id.listaRezultat);

        bPretraga.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dajPrognozu(uneseniTekst.getText().toString());
            }
        });

        final Button bLondon = findViewById(R.id.bLondon);
        final Button bNewYork = findViewById(R.id.bNewYork);
        final Button bParis = findViewById(R.id.bParis);

        bLondon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("Msg", "london");

                dajPrognozu("London");
            }
        });

        bNewYork.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i("Msg", "new york");

                dajPrognozu("New York");
            }
        });

        bParis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dajPrognozu("Paris");
            }
        });
    }

    private void dajPrognozu(String grad)
    {
        this.grad = grad;

        if(imaInterneta())
        {
            Intent intent = new Intent(getApplicationContext(), DohvatiForecast.class);

            ResultReceiverForecast mReceiver = new ResultReceiverForecast(new Handler());
            mReceiver.setReceiver(MainActivity.this);

            intent.putExtra("grad", grad);
            intent.putExtra("receiver", mReceiver);

            startService(intent);
        }
        else
        {
            BazaOpenHelper db = BazaOpenHelper.getInstance(getApplicationContext());

            ArrayList<Forecast> prognoze = new ArrayList<>();

            if(grad.equals("London") || grad.equals("New York") || grad.equals("Paris"))
            {
                prognoze = db.dajMjerenjaGrada(grad);
            }

            adapterForecast = new AdapterForecast(this, prognoze);

            listaRezultat.setAdapter(adapterForecast);
            adapterForecast.notifyDataSetChanged();
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData)
    {
        if(resultCode == DohvatiForecast.STATUS_FINISH)
        {
            ArrayList<Forecast> prognoze = resultData.<Forecast>getParcelableArrayList("listaForecast");

            BazaOpenHelper db = BazaOpenHelper.getInstance(getApplicationContext());

            if(grad.equals("London") || grad.equals("New York") || grad.equals("Paris"))
            {
                if(prognoze != null)
                {
                    for (Forecast forecast : prognoze)
                    {
                        db.dodajMjerenje(grad, forecast);
                    }
                }

                prognoze = db.dajMjerenjaGrada(grad);
            }

            adapterForecast = new AdapterForecast(this, prognoze);

            listaRezultat.setAdapter(adapterForecast);
            adapterForecast.notifyDataSetChanged();
        }
    }

    private boolean imaInterneta()
    {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }

    }
}

