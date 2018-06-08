package com.example.dtuser3.vrijemeapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class ResultReceiverForecast extends ResultReceiver
{
    private Receiver mReceiver;

    public ResultReceiverForecast(Handler handler)
    {
        super(handler);
    }

    public void setReceiver(Receiver receiver)
    {
        mReceiver = receiver;
    }

    public interface Receiver
    {
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData)
    {
        if(mReceiver != null)
        {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}