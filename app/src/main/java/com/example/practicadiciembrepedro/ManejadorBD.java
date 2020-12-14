package com.example.practicadiciembrepedro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ManejadorBD  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "moviles.db";

    private static final String COL_ID="ID";
    private static final String COL_FECHA="FECHA";
    private static final String COL_HORA="HORA";
    private static final String COL_BATERIA="BATERIA";
    private static final String COL_LATITUD_GPS="LATITUD";
    private static final String COL_LONGITUD_GPS="LONGITUD";

    private static final String TABLE_NAME="SEGUIMIENTO";


    public ManejadorBD(Context context) {

        super(context, DATABASE_NAME, null, 1);
    }

 

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COL_FECHA + " TEXT, " +COL_HORA+" TEXT, "+ COL_BATERIA + " TEXT, " + COL_LATITUD_GPS + " TEXT, " +COL_LONGITUD_GPS+" TEXT)");

    }

    public boolean insertar(String fecha,String hora,String bateria,String latitud,String longitud){
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_FECHA,fecha);
        contentValues.put(COL_HORA,hora);
        contentValues.put(COL_BATERIA,bateria);
        contentValues.put(COL_LATITUD_GPS,latitud);
        contentValues.put(COL_LONGITUD_GPS,longitud);

        long resultador=sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        sqLiteDatabase.close();

        if (resultador ==-1) {
            return false;
        }else{
            return true;
        }


    }

    Cursor listar(){
        SQLiteDatabase sqLiteDatabase=this.getReadableDatabase();

        Cursor cursor=sqLiteDatabase.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        return cursor;
    }

     boolean borrar(String id){
        SQLiteDatabase sqLiteDatabase=getWritableDatabase();

        int borradas=sqLiteDatabase.delete(TABLE_NAME,COL_ID+ "=?",new String[]{id});
        //sqLiteDatabase.execSQL("delete from " + TABLE_NAME + " Where id = " + id);

        sqLiteDatabase.close();

        return (borradas>0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
