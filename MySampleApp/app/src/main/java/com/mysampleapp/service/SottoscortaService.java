package com.mysampleapp.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mysampleapp.R;
import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.activity.SplashActivity;
import com.mysampleapp.nosqldb.models.DrugDO;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class SottoscortaService extends IntentService {

    public static final String DRUG_EXTRA = "drug";
    public static final String ACTION_EXTRA = "action";

    public SottoscortaService() {
        super("SottoscortaService");
    }

    protected void onHandleIntent(Intent intent) {
        Log.w("SottoscortaService", "service running");
        String action = intent.getStringExtra(SottoscortaService.ACTION_EXTRA);
        DrugDO drugDO = intent.getParcelableExtra(SottoscortaService.DRUG_EXTRA);
        switch (action) {
            case "take":
                Log.w("SottoscortaService", "take");
                takeDrug(drugDO);
                break;
            case "refill":
                Log.w("SottoscortaService", "refill");
                refillDrug(drugDO);
                break;
        }

    }

    public void takeDrug(DrugDO drugDO) {
        Log.w("SottoscortaService", "quantity available " + drugDO.getQuantity().toString());
        Log.w("SottoscortaService", "sottoscorta " + drugDO.getMinqty().toString());
        //check if quantity below sottoscorta
        if (drugDO.getQuantity() <= drugDO.getMinqty()) {
            //get shared pref file
            SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                    getApplicationContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);

            //getting data from shared pref and add the new sottoscorta pendig id notification to update always same notification
            Set<String> s = new HashSet<String>(sharedPref.getStringSet(getApplicationContext().getString(R.string.sottoscorta_alarm),
                    new HashSet<String>()));
            SharedPreferences.Editor editor = sharedPref.edit();

            Log.w("SottoscortaService", "pending list " + s.toString());

            //search if the drug was already sottoscortata
            String pendingSottoscorta = "none";
            for (String ss : s) {
                String tmp = ss.split("/")[0];
                Log.w("SottoscortaService", tmp);
                if (tmp.equals(drugDO.getName())) {
                    pendingSottoscorta = ss;
                }
            }

            int notificationID;

            Log.w("SottoscortaService", "pendingSottoscorta " + pendingSottoscorta);

            if (pendingSottoscorta.equals("none")) {
                //first time sottoscortato
                Random r = new Random();
                notificationID = r.nextInt();
                s.add(drugDO.getName() + "/" + notificationID);
                editor.putStringSet(getApplicationContext().getString(R.string.sottoscorta_alarm), s);
                //persist immediatly the data in the shared pref
                editor.apply();

            } else {
                //use the same id to update the notification
                notificationID = Integer.parseInt(pendingSottoscorta.split("/")[1]);
            }

            Log.w("SottoscortaService", "sottoscorta" + notificationID);
            createNotification(getApplicationContext(), drugDO, notificationID);
        }
    }

    private void refillDrug(DrugDO drugDO) {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getApplicationContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);

        //getting data from shared pref and add the new sottoscorta pendig id notification to update always same notification
        Set<String> s = new HashSet<String>(sharedPref.getStringSet(getApplicationContext().getString(R.string.sottoscorta_alarm),
                new HashSet<String>()));
        SharedPreferences.Editor editor = sharedPref.edit();

        Log.w("SottoscortaService", "pending list " + s.toString());

        //search if the drug was already sottoscortata
        String pendingSottoscorta = "none";
        for (String ss : s) {
            String tmp = ss.split("/")[0];
            Log.w("SottoscortaService", tmp);
            if (tmp.equals(drugDO.getName())) {
                pendingSottoscorta = ss;
            }
        }
        if (!pendingSottoscorta.equals("none")) {
            //use the same id to update the notification
            s.remove(pendingSottoscorta);
            editor.putStringSet(getApplicationContext().getString(R.string.sottoscorta_alarm), s);
            //persist immediatly the data in the shared pref
            editor.apply();
            Log.w("SottoscortaService", "pending list " + s.toString());
        }
    }

    //TODO COSA FACCIAMO CON I SOTTOSCORTA?????
    //li notifichiamo una volta e bom
    //li notifichaimo come le pastiglie multiple
    //ci serve una pagina che si apre al click?????
    //tante notifiche tutte diverse
    public void createNotification(Context context, DrugDO drugDO, int notificatoID) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.sottoscorta)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle("Sottoscorta")
                .setContentText(drugDO.getName());

        Intent resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.putExtra(SplashActivity.ACTIVITY_HOME_FRAGMENT_EXTRA, "fragment_drug");
        resultIntent.putExtra(HomeActivity.DRUG_EXTRA, drugDO);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        notificatoID,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(notificatoID, mBuilder.build());
        //TODO AGGIUNGERE UN CONTROLLO SU QUANDO SI SALE SORA AL SOTTOSCORTA(QUALSIASI MODIFICA DI FRUG DO) TOGLIERE DALLE SHARED PREF IL PENDING

    }
}
