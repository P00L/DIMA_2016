package com.example.paolo.myfirstapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by paolo on 31/03/2016.
 * creazione e accesso del db
 */
public class DbContract {

    public static final String DATABASE_NAME = "MyDb.db";
    public static final int DATABASE_VERSION = 1;

    public static final String INT_TYPE = " int";
    public static final String FLOAT_TYPE = " float";
    public static final String TEXT_TYPE = " text";

    public static final String COMMA_SEP = ",";
    //stringa che crea il db
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS "+ Columns.TABLE_NAME+" ("+
                    Columns.COLUMN_NAME_ID +INT_TYPE
                    +" PRIMARY KEY"+COMMA_SEP+
                    Columns.COLUMN_NAME_COLUMN_1 +TEXT_TYPE+COMMA_SEP+
                    Columns.COLUMN_NAME_COLUMN_2 +TEXT_TYPE+" )";


    public static SQLiteDatabase getWritableDatabase(Context context){
        return new DbHelper(context).getWritableDatabase();
    }

    public static SQLiteDatabase getReadableDatabase(Context context){
        return new DbHelper(context).getReadableDatabase();
    }

    //classicina di appoggio per definire tutti i nomi che andranno nel db nome classe nome colonne
    public static abstract class Columns implements BaseColumns {

        public static final String TABLE_NAME = "tabella";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_COLUMN_1 = "column_1";
        public static final String COLUMN_NAME_COLUMN_2 = "column_2";

    }

    //gestione accesso al db la classe da istanziare ma anche no se si fa cosi per modificare il db
    private static class DbHelper extends SQLiteOpenHelper{


        public DbHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
