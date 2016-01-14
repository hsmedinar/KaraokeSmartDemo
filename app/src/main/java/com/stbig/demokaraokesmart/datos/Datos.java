package com.stbig.demokaraokesmart.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.stbig.demokaraokesmart.R;
import com.stbig.demokaraokesmart.objects.Cancion;
import com.stbig.demokaraokesmart.helper.DBitem;

import java.util.ArrayList;


/**
 * Created by root on 23/11/15.
 */
public class Datos extends SQLiteOpenHelper {


    private SQLiteDatabase db;
    private Context context;

    private static final String CREATE_CANCION = "CREATE TABLE " + DBitem.TABLE_CANCIONES + " (" + DBitem.CANCION_ID + " INTEGER," + DBitem.CANCION_NAME + " TEXT,"  + DBitem.CANCION_DURATION + " TEXT," + DBitem.CANCION_INITIAL + " INTEGER," + DBitem.CANCION_MP4 +  " TEXT," + DBitem.CANCION_VIDEO  + " TEXT," + DBitem.CANCION_SOUND  + " TEXT,"  + DBitem.CANCION_ARTIST  + " TEXT);";

    public Datos(Context context){
        super(context, context.getString(R.string.database), null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();

        try {
            db.execSQL(CREATE_CANCION);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void registerCancion(int id, String name, String duration, int initial_second, String mp4_source, String video, String sound,
                                String artist) throws SQLException {

        try {
            db = getReadableDatabase();

            if(checkRegister(DBitem.TABLE_CANCIONES,DBitem.CANCION_ID,String.valueOf(id))==0){
                ContentValues values = new ContentValues();
                values.put(DBitem.CANCION_ID, id);
                values.put(DBitem.CANCION_NAME, name);
                values.put(DBitem.CANCION_DURATION, duration);
                values.put(DBitem.CANCION_INITIAL, initial_second);
                values.put(DBitem.CANCION_MP4, mp4_source);
                values.put(DBitem.CANCION_VIDEO, video);
                values.put(DBitem.CANCION_SOUND, sound);
                values.put(DBitem.CANCION_ARTIST, artist);
                db.insert(DBitem.TABLE_CANCIONES, null, values);
                Log.i("regitsrar en db", "registro");
            }else{
                String[] args = {String.valueOf(id)};
                ContentValues values = new ContentValues();
                values.put(DBitem.CANCION_NAME, name);
                values.put(DBitem.CANCION_DURATION, duration);
                values.put(DBitem.CANCION_INITIAL, initial_second);
                values.put(DBitem.CANCION_MP4, mp4_source);
                values.put(DBitem.CANCION_VIDEO, video);
                values.put(DBitem.CANCION_SOUND, sound);
                values.put(DBitem.CANCION_ARTIST, artist);
                db.update(DBitem.TABLE_CANCIONES, values, DBitem.CANCION_ID + "=?", args);
            }

            db.close();

        } catch (SQLException e) {
            Log.i("error en registro db", e.getMessage());
        }
    }

    public ArrayList<Cancion> listarCanciones(){
        ArrayList<Cancion> values = new ArrayList<Cancion>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from " + DBitem.TABLE_CANCIONES , null);
        if (c.getCount() != 0) {
            c.moveToFirst();
            do {
                Cancion data = new Cancion(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7));
                values.add(data);
            } while (c.moveToNext());
        }
        db.close();
        c.close();
        return values;
    }


    public int checkRegister(String table,String field,String data) {
        int total=0;
        String[] param = {data};
        Cursor c = db.rawQuery("select * from " + table + "  where " + field + "=?", param);
        total = c.getCount();
        c.close();
        return total;
    }
}
