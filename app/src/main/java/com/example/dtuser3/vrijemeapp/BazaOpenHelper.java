package com.example.dtuser3.vrijemeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class BazaOpenHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "WeatherBaza.db";

    public static final String DATABASE_TABLE_GRAD = "Grad";
    public static final String GRAD_ID = "_id";
    public static final String GRAD_NAZIV = "naziv";

    public static final String DATABASE_TABLE_MJERENJE = "Mjerenje";
    public static final String MJERENJE_ID = "_id";
    public static final String MJERENJE_GRAD_ID = "grad_id";
    public static final String MJERENJE_VRIJEME = "vrijeme";
    public static final String MJERENJE_TEMPERATURA = "temperatura";


    private static final String DATABASE_CREATE_TABLE_GRAD = "CREATE TABLE " + DATABASE_TABLE_GRAD + "("
            + GRAD_ID + " integer primary key autoincrement, " + GRAD_NAZIV + " text not null unique);";

    private static final String DATABASE_CREATE_TABLE_MJERENJE = "CREATE TABLE " + DATABASE_TABLE_MJERENJE + "("
            + MJERENJE_ID + " integer primary key autoincrement, " + MJERENJE_VRIJEME + " integer not null, " +
            MJERENJE_TEMPERATURA + " real not null, " + MJERENJE_GRAD_ID + " integer REFERENCES " + DATABASE_TABLE_GRAD + "(" + GRAD_ID + "));";


    private BazaOpenHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    private static BazaOpenHelper mInstance = null;

    public static BazaOpenHelper getInstance(Context ctx)
    {
        if(mInstance == null)
        {
            mInstance = new BazaOpenHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE_GRAD);
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE_MJERENJE);

        ubaciGrad(sqLiteDatabase, "London");
        ubaciGrad(sqLiteDatabase, "New York");
        ubaciGrad(sqLiteDatabase, "Paris");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_MJERENJE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_GRAD);

        onCreate(sqLiteDatabase);
    }

    public void dodajMjerenje(String grad, Forecast forecast)
    {
        try
        {
            Long idGrada = dajIDGrada(grad);

            if(!vecImaMjerenje(idGrada, forecast.dt))
            {
                SQLiteDatabase db = this.getWritableDatabase();

                ContentValues contentValues = new ContentValues();

                contentValues.put(MJERENJE_VRIJEME, forecast.dt);
                contentValues.put(MJERENJE_TEMPERATURA, forecast.temp);
                contentValues.put(MJERENJE_GRAD_ID, idGrada);

                db.insert(DATABASE_TABLE_MJERENJE, null, contentValues);

                db.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public ArrayList<Forecast> dajMjerenjaGrada(String grad)
    {
        ArrayList<Forecast> lista = new ArrayList<>();

        if(grad.equals("London") || grad.equals("New York") || grad.equals("Paris"))
        {
            Cursor cursor = null;

            try
            {
                Long idGrada = dajIDGrada(grad);

                SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

                cursor = sqLiteDatabase.query(DATABASE_TABLE_MJERENJE, new String[]{MJERENJE_VRIJEME, MJERENJE_TEMPERATURA}, MJERENJE_GRAD_ID + "=" + Long.toString(idGrada), null, null, null, null);

                int indeksKoloneVrijeme = cursor.getColumnIndexOrThrow(MJERENJE_VRIJEME);
                int indeksKoloneTemperatura = cursor.getColumnIndexOrThrow(MJERENJE_TEMPERATURA);


                while(cursor.moveToNext())
                {
                    Long vrijeme = cursor.getLong(indeksKoloneVrijeme);
                    Double temperatura = cursor.getDouble(indeksKoloneTemperatura);

                    lista.add(new Forecast(vrijeme, temperatura));
                }

                cursor.close();

                sqLiteDatabase.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();

                if(cursor != null && !cursor.isClosed())
                {
                    cursor.close();
                }
            }
        }

        return lista;
    }

    private boolean vecImaMjerenje(Long idGrada, Long vrijeme)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = null;

        try
        {
            cursor = sqLiteDatabase.query(DATABASE_TABLE_MJERENJE, new String[]{MJERENJE_VRIJEME, MJERENJE_GRAD_ID}, MJERENJE_VRIJEME + "=" + Long.toString(vrijeme) + " AND " + MJERENJE_GRAD_ID + "=" + Long.toString(idGrada), null, null, null, null);

            boolean ret = cursor.getCount() > 0;

            cursor.close();

            sqLiteDatabase.close();

            return ret;

        }
        catch (Exception e)
        {
            e.printStackTrace();

            throw e;
        }
    }

    private void ubaciGrad(SQLiteDatabase sqLiteDatabase, String grad)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(GRAD_NAZIV, grad);

        sqLiteDatabase.insert(DATABASE_TABLE_GRAD, null, contentValues);
    }

    private Long dajIDGrada(String grad)
    {
        Cursor cursor = null;
        try
        {
            SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

            cursor = sqLiteDatabase.query(DATABASE_TABLE_GRAD, new String[]{GRAD_ID}, GRAD_NAZIV + "='" + grad + "'", null, null, null, null);

            cursor.moveToNext();

            sqLiteDatabase.close();

            return cursor.getLong(cursor.getColumnIndexOrThrow(GRAD_ID));
        }
        catch (Exception e)
        {
            e.printStackTrace();

            if(cursor != null && !cursor.isClosed())
            {
                cursor.close();
            }

            throw e;
        }
    }

    public static String escapeAllChars(String s)
    {
        String novi = "";

        for(int i = 0; i < s.length(); i++)
        {
            if(!(s.charAt(i) == '"' || Character.toString(s.charAt(i)).equals("'")))
            {
                novi = novi.concat(Character.toString(s.charAt(i)));
            }
        }

        return novi;
    }
}
