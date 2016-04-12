package com.example.paolo.myfirstapp;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.paolo.myfirstapp.db.TabellaDb;
import com.example.paolo.myfirstapp.db.TabellaService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DisplayListActivity extends ListActivity {


    private AdapterOne adapter;
    private TabellaService tabellaService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        tabellaService = new TabellaService(this);
        adapter = new AdapterOne(this,tabellaService.getAll());
        getListView().setPadding(10, 10, 10, 10);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);
        TabellaDb tabellaDb = tabellaService.getAll().get(position);
        Toast.makeText(getApplicationContext(), "Selezionato "+tabellaDb.getIds(), Toast.LENGTH_SHORT).show();
    }

    //old lista di roba custom adesso la ROBA è presa dal DB :) NON PIU USATA
    @Deprecated
    private List<Roba> generateNews()
    {
        ArrayList<Roba> list=new ArrayList<Roba>();
        Calendar c=Calendar.getInstance();

        Roba tmp=new Roba();
        tmp.setTitolo("WordPress: integrare un pannello opzioni nel tema");
        tmp.setCategory("CMS");
        c.set(2014, 3, 23);
        tmp.setDate(new Date(c.getTimeInMillis()));

        for(int i = 0; i < 20;i++ ){
            list.add(tmp);
    }
        return list;
    }

}
