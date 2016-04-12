package com.example.paolo.myfirstapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.paolo.myfirstapp.Recycler.DisplayRecyclerList;
import com.example.paolo.myfirstapp.alarmnotification.NotificationPublisher;

public class MyActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.paolo.myfirstapp.MESSAGE";
    String GROUP_KEY_EMAILS = "group_key_emails";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
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
    }

    //invocato alla creazione del menù(lo visulizza e basta praticamente)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    //chiamato qunado l'utente preme il bottone per far partire l'activity con il messaggio scritto
    public void sendMessage(View view) {
        Intent intent = new Intent(this,DisplayMessageActivity.class);
        //recupera dalla view il messaggio inserito dall'utente
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        //aggiunge all'intent gia creato il messaggio, quindi è come se fosse un passaggio prametri tra due activity
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    //chiamato quando l'utente clicca il bottone per far partire l'activity con la lista
    public void activityListStart(View view) {
        Intent intent = new Intent(this,DisplayListActivity.class);
        startActivity(intent);
    }

    public void activityListRecyclerStart(View view) {
        Intent intent = new Intent(this,DisplayRecyclerList.class);
        startActivity(intent);
    }

    //chiamato quando l'utente clicca il bottone per far partire l'activity con il fragment
    public void activityFragmentStart(View view) {
        Intent intent = new Intent(this,EmptyActivity.class);
        startActivity(intent);
    }

    //chiamato quando l'utente clicca il bottone per far partire la notifica che viene visualizzata e subito modificata grazie all'id
    public void notifyMe(View view) {
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificatioBuilder  = new NotificationCompat.Builder(this)
                .setContentTitle("Arrivata notifica")
                .setContentText("notifica 1")
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setSound(sound);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificatioBuilder.build());

        //modifico la stessa notifica andando a usare lo stesso id
        notificatioBuilder.setContentText("notifica 2");
        // Because the ID(0) remains unchanged, the existing notification is updated
        notificationManager.notify(
                0,
                notificatioBuilder.build());
        //altra notifica ma con stile inbox

        //passo l'id della notifica visualizzata per cancellarla
        Intent intent = new Intent(this, EmptyActivity.class);
        intent.putExtra(NOTIFICATION_SERVICE,3);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent notificationIntent = new Intent(this, MyActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle("Event tracker")
                .setContentText("Events received")
                .setNumber(6)
                .addAction(android.R.drawable.ic_dialog_alert, "MANNAGGIA", pIntent)
                .setOngoing(true)//rende la notifica non rimovibile
                .setAutoCancel(true)//cancella la notifica al click
                .setContentIntent(contentIntent);//azione al click
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        inboxStyle.setBigContentTitle("Event tracker details:");

        for (int i=0; i < events.length; i++) {
            events[i] = "messaggio" + i;
            inboxStyle.addLine(events[i]);
        }
        mBuilder.setStyle(inboxStyle);
        notificationManager.notify(3,mBuilder.build());

    }

    //chiamato quando l'utente clicca il bottone per far partire la notifica con delay tramite alarm manager
    public void notifyMeDelay(View view) {

        scheduleNotification(getNotification("5 second delay"), 5000);
    }


    private void scheduleNotification(Notification notification, int delay) {
        //creo l'intent che quando scatterà il tempo andrà a far partire la classe NotificationPublisher
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    //creo la notifica che verrà visualizzata
    private Notification getNotification(String content) {
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(android.R.drawable.ic_popup_reminder);
        builder.setSound(sound);
        //settare min API 16 in graddle
        return builder.build();
    }


    //azioni relative al menù
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
