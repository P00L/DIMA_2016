package com.example.paolo.myfirstapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class EmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //se arriva dalla notifica cancella la notifica
        Intent intent = getIntent();
        Log.d("NOTI", String.valueOf(intent.getStringExtra(MyActivity.NOTIFICATION_SERVICE)));
        if(intent.getIntExtra(MyActivity.NOTIFICATION_SERVICE, 0) != 0) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(intent.getIntExtra(MyActivity.NOTIFICATION_SERVICE, 0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        CharSequence text = "no press";
        int duration = Toast.LENGTH_LONG;
        switch(id)
        {
            case R.id.menu_settings:
                text = "menu_settings pressed";
                break;
            case R.id.menu_item2:
                text = "item2_settings pressed";
                break;
            case R.id.menu_item3:
                text = "item3_settings pressed";
                break;
        }
        Toast.makeText(this, text, duration).show();
        return super.onOptionsItemSelected(item);
    }
}
