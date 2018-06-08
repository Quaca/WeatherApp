package com.example.dtuser3.vrijemeapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Forecast implements Parcelable
{
    public long dt;
    public double temp;

    public Forecast(long dt, double temp)
    {
        this.dt = dt;
        this.temp = temp;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeLong(dt);
        parcel.writeDouble(temp);
    }

    public static final Parcelable.Creator<Forecast> CREATOR = new Parcelable.Creator<Forecast>()
    {
        public Forecast createFromParcel(Parcel in)
        {
            return new Forecast(in);
        }

        public Forecast[] newArray(int size)
        {
            return new Forecast[size];
        }
    };

    private Forecast(Parcel parcel)
    {
        dt = parcel.readLong();
        temp = parcel.readDouble();
    }
}