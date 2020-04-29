package com.example.android.pets.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import Clases.Pet;

public class PetDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shelter.db";
    private static final int DATABASE_VERSION = 1;

    public PetDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + PetContract.PetEntry.TABLE_NAME +" ("
                + PetContract.PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + PetContract.PetEntry.COLUMN_PET_BREED + " TEXT, "
                + PetContract.PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + PetContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public ArrayList llenar_lv(){
        ArrayList<String> lista = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        String q = "SELECT * FROM pets";
        Cursor registro = database.rawQuery(q, null);
        if(registro.moveToFirst()){
            do {
                lista.add(registro.getString(1) + " - " + registro.getString(2));
            }while (registro.moveToNext());
        }

        return lista;
    }

    public ArrayList listaID(){
        ArrayList<String> lista = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        String q = "SELECT * FROM pets";
        Cursor registro = database.rawQuery(q, null);
        if(registro.moveToFirst()){
            do {
                lista.add(registro.getString(0));
            }while (registro.moveToNext());
        }

        return lista;
    }

    public Pet buscar(String codigo){

        Pet mascota = new Pet();

        SQLiteDatabase database = this.getReadableDatabase();
        String q = "SELECT * FROM pets WHERE _ID='" +codigo+"'";
        Cursor registro = database.rawQuery(q, null);
        if(registro.moveToFirst()){
            mascota.id= registro.getString(0);
            mascota.nombre= registro.getString(1);
            mascota.raza= registro.getString(2);
            mascota.genero= registro.getString(3);
            mascota.peso= registro.getString(4);
        }

        return mascota;
    }
}
