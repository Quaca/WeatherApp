package com.example.dtuser3.vrijemeapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DohvatiForecast extends IntentService
{
    public static final int STATUS_FINISH = 1;
    public static final String KVALETOV_KEY = "65dfa63ae7f3c6b1602067ad2a92341e";

    public DohvatiForecast()
    {
        super("DohvatiForecast");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (intent != null)
        {
            final ResultReceiver receiver = intent.getParcelableExtra("receiver");

            String imeGrada = intent.getStringExtra("grad");

            try
            {
                imeGrada = URLEncoder.encode(imeGrada, "utf-8");
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }

            String url1 = "http://api.openweathermap.org/data/2.5/forecast?q=" + imeGrada + "&units=metric&appid=" + KVALETOV_KEY;

            ArrayList<Forecast> prognoze = new ArrayList<>();

            try
            {
                URL url = new URL(url1);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String rezultat = convertStreamToString(in);

                JSONObject jo = new JSONObject(rezultat);

                JSONArray list = jo.getJSONArray("list");

                for(int i = 0; i < list.length(); i++)
                {
                    JSONObject forecast = list.getJSONObject(i);

                    Long dt = forecast.getLong("dt");

                    JSONObject main = forecast.getJSONObject("main");

                    double temp = main.getDouble("temp");

                    prognoze.add(new Forecast(dt, temp));
                }

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("listaForecast", prognoze);
                receiver.send(STATUS_FINISH, bundle);
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        StringBuilder sb = new StringBuilder();
        String line;

        try
        {
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                sb.append("\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return  sb.toString();
    }
}
