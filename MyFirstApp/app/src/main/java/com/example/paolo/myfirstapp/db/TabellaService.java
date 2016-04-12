package com.example.paolo.myfirstapp.db;

import android.content.Context;
import android.util.Log;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paolo on 01/04/2016.
 * classe che incorpora tutti i metodi per la gestione dei dati relativi ai movie che si trovano nel db
 *
 */
public class TabellaService {

    private DbManager dbManager;

    public TabellaService(Context context) {
        dbManager = new DbManager(context);
        Fill(context);
    }

    private void Fill (Context context) {
        Log.d("numero elementi db",String.valueOf(dbManager.findAll().getCount()));
        if(dbManager.findAll().getCount() == 0){
            dbManager.add(new TabellaDb("stringa11","stringa12",1));
            dbManager.add(new TabellaDb("stringa21","stringa22",2));
            dbManager.add(new TabellaDb("stringa31","stringa32",3));
            dbManager.add(new TabellaDb("stringa41","stringa42",4));
            dbManager.add(new TabellaDb("stringa51","stringa52",5));

        }
    }

    public List<TabellaDb> getAll (){
        TabellaCursor tabellaCursor = dbManager.findAll();
        List<TabellaDb> tabellaReturn = new ArrayList<TabellaDb>();

        while (tabellaCursor.moveToNext()) {
           // Log.d("tabella cursor scan",String.valueOf(tabellaCursor.getColumn1()));
            TabellaDb tabellaDb = new TabellaDb(
                    tabellaCursor.getColumn1(),
                    tabellaCursor.getColumn2(),
                    tabellaCursor.getIds());
            tabellaReturn.add(tabellaDb);
        }
        Log.d("ritorno get all service",String.valueOf(tabellaReturn));
        tabellaCursor.close();
        return tabellaReturn;
    }
}
