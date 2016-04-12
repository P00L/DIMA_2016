package com.example.paolo.myfirstapp.db;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by paolo on 31/03/2016.
 *
 * cursor wrapper del db
 * è un normale adapter per visualizzare roba con accesso riga per riga del db
 * ci sono i getter di tutte le colonne della tabella
 * ci sarà un cursor per ogni tabella???
 */
public class TabellaCursor extends CursorWrapper {

    public TabellaCursor(Cursor cursor) {
        super(cursor);
    }

    public int getIds(){
        return getInt(
                getColumnIndex(DbContract.
                        Columns.COLUMN_NAME_ID));
    }

    public String getColumn1(){
        return getString(
                getColumnIndex(DbContract.
                        Columns.COLUMN_NAME_COLUMN_1));
    }

    public String getColumn2(){
        return getString(
                getColumnIndex(DbContract.
                        Columns.COLUMN_NAME_COLUMN_2));
    }
}
