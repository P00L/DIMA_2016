package com.example.paolo.myfirstapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import static com.example.paolo.myfirstapp.db.DbContract.COMMA_SEP;
import static com.example.paolo.myfirstapp.db.DbContract.Columns;
import static com.example.paolo.myfirstapp.db.DbContract.getWritableDatabase;

/**
 * Created by paolo on 31/03/2016.
 * classe di esecuzione delle query
 * qui ogni metodo esegue una query nel SQL nel db ritornando il risulatato
 * classe con cui interagire per ottenere dati dal db
 */
public class DbManager {

    private SQLiteDatabase db;

    //il costruttore getta il db come db writable
    public DbManager(Context context) {
        db = getWritableDatabase(context);
    }

    public void add(TabellaDb tabellaDb) {

        db.execSQL("INSERT OR REPLACE INTO " +
                        Columns.TABLE_NAME + "(" +
                        Columns.COLUMN_NAME_ID + COMMA_SEP +
                        Columns.COLUMN_NAME_COLUMN_1 + COMMA_SEP +
                        Columns.COLUMN_NAME_COLUMN_2 + ") " +
                        "VALUES(?" + COMMA_SEP + "?" + COMMA_SEP + "?)",
                new Object[]{tabellaDb.getIds(), tabellaDb.getColumn_1(),
                        tabellaDb.getColumn_2()});

    }

    public void delete() {
        db.execSQL("DELETE FROM ");
    }


    // metodi che ritornano un TabellaCursor che permette l'accesso riga per riga del db dei dati ritornati
    public TabellaCursor findById(String id) {
        return new TabellaCursor(db.rawQuery("SELECT * FROM " + Columns.TABLE_NAME +
                        " WHERE " + Columns.COLUMN_NAME_ID + " = ?",
                new String[]{id}));
    }

    public TabellaCursor findAll() {
        return new TabellaCursor(
                db.rawQuery("SELECT * FROM " + Columns.TABLE_NAME,
                        null));
    }

}
