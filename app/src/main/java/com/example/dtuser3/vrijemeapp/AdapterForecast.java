package com.example.dtuser3.vrijemeapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdapterForecast extends BaseAdapter
{
    private Activity activity;
    private static LayoutInflater inflater;
    private ArrayList<Forecast> prognoze;

    public AdapterForecast(Activity a, ArrayList<Forecast> data)
    {
        activity = a;
        prognoze = data;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return prognoze.size();
    }

    @Override
    public Object getItem(int i)
    {
        return prognoze.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }


    public static class ViewHolder
    {
        TextView tekstVrijeme;
        TextView tekstTemperatura;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        View vi = view;
        AdapterForecast.ViewHolder holder;

        if(view == null)
        {
            vi = inflater.inflate(R.layout.element_liste_prognoza, null);

            holder = new AdapterForecast.ViewHolder();
            holder.tekstVrijeme = vi.findViewById(R.id.tekstVrijeme);
            holder.tekstTemperatura = vi.findViewById(R.id.tekstTemperatura);

            vi.setTag( holder );
        }
        else
        {
            holder = (AdapterForecast.ViewHolder) vi.getTag();
        }

        if(prognoze.size() > 0)
        {
            Long dt = prognoze.get(i).dt;

            double temp = prognoze.get(i).temp;

            DateTime datum = new DateTime(dt * 1000L);

            String dan = "";

            switch (datum.getDayOfWeek())
            {
                case 1 :
                    dan = "Mon";
                    break;
                case 2:
                    dan = "Tue";
                    break;
                case 3:
                    dan = "Wed";
                    break;
                case 4:
                    dan = "Thu";
                    break;
                case 5:
                    dan = "Fri";
                    break;
                case 6:
                    dan = "Sat";
                    break;
                case 7:
                    dan = "Sun";
                    break;
            }

            String vrijeme = dan + " " + datum.getHourOfDay() + ":" + datum.getMinuteOfHour();

            holder.tekstTemperatura.setText( Double.toString(temp) + " C" );
            holder.tekstVrijeme.setText( vrijeme );
        }
        return vi;
    }
}