package com.mysampleapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.activity.SplashActivity;
import com.mysampleapp.fragment.ScheduleFormFragment;

import java.util.HashSet;
import java.util.Set;

public class ServiceNotification extends IntentService {

    //notification ID always the same to be able to update it later on
    public final static int NOTIFICATION_ID = 666;

    public ServiceNotification() {
        super("ServiceNotification");
    }
    protected void onHandleIntent(Intent intent) {
        Log.w("Service", "service running");
        // 0 invalid number fo an alarm id
        int alarm_id = intent.getIntExtra(ScheduleFormFragment.ALARM_ID_EXTRA,0);
        String drugName = intent.getStringExtra(ScheduleFormFragment.DRUG_EXTRA);

        //get shared pref file
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getApplicationContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);

        //getting data from shared pref and add the new alarm manager as pending
        //returning the pending list of id alarm manager as string or an empty set
        Set<String> s = new HashSet<String>(sharedPref.getStringSet(getApplicationContext().getString(R.string.pending_alarm),
                new HashSet<String>()));
        SharedPreferences.Editor editor = sharedPref.edit();
        s.add(alarm_id+"/"+drugName);
        editor.putStringSet(getApplicationContext().getString(R.string.pending_alarm), s);
        //persist immediatly the data in the shared pref
        editor.apply();

        //rischedula alarm per il prossimo giorno qui si che adesso ci vuole la query da db ma solo
        //per recuperare una specifica entry

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Define our intention of executing AlertReceiver
        Intent alertIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

        alertIntent.putExtra(ScheduleFormFragment.ALARM_ID_EXTRA, alarm_id);
        alertIntent.putExtra(ScheduleFormFragment.DRUG_EXTRA, alarm_id+"drugname");
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm_id, alertIntent, PendingIntent.FLAG_ONE_SHOT);

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() +
                        15 * 1000, alarmIntent);

        createNotification(getApplicationContext(), intent, s);

    }

    public void createNotification(Context context, Intent intent, Set<String> set_result) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Event tracker")
                .setContentText("Events received");
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

// Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Event tracker details:");
// Moves events into the expanded layout
        for (String s : set_result) {
            inboxStyle.addLine(s);
        }
// Moves the expanded layout object into the notification object.
        mBuilder.setStyle(inboxStyle);

        Intent resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.putExtra(SplashActivity.ACTIVITY_HOME_FRAGMENT_EXTRA, "fragment_home");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomeActivity.class);

// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
// added action TODO now it is setted to return to the same place as clicking notification
        mBuilder.addAction(R.mipmap.ic_launcher, "Call", resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mBuilder.setAutoCancel(true);
// notificationID allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
