package com.example.paolo.myfirstapp.Recycler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.paolo.myfirstapp.R;
import com.example.paolo.myfirstapp.Recycler.AdapterRecycler;
import com.example.paolo.myfirstapp.db.TabellaDb;
import com.example.paolo.myfirstapp.db.TabellaService;

public class DisplayRecyclerList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recycler_list);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        final TabellaService tabellaService = new TabellaService(this);
        // specify an adapter for the recycler view in the xml passando la lista di cose da visualizzare
        adapter = new AdapterRecycler(this, tabellaService.getAll());

        //metto il listener al click del recycle

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //TabellaDb tabellaDb = adapter.getTabella(position);
                TabellaDb tabellaDb = tabellaService.getAll().get(position);
                Toast.makeText(getApplicationContext(), "Selezionato " + tabellaDb.getIds(), Toast.LENGTH_SHORT).show();
            }
        }));

        recyclerView.setAdapter(adapter);

    }


}
