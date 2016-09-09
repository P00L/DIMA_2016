package com.mysampleapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.activity.SplashActivity;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;

import java.util.HashSet;
import java.util.Set;

public class ServiceNotification extends IntentService {

    //notification ID always the same to be able to update it later on
    public final static int NOTIFICATION_ID = 666;

    public ServiceNotification() {
        super("ServiceNotification");
    }

    protected void onHandleIntent(Intent intent) {
        Log.w("ServiceNotification", "service running");
        // 0 invalid number fo an alarm id
        ScheduleDrugDO scheduleDrugDO = intent.getParcelableExtra(AlarmService.SCHEDULE_EXTRA);

        //get shared pref file
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getApplicationContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);

        //getting data from shared pref and add the new alarm manager as pending
        //returning the pending list of id alarm manager as string or an empty set
        Set<String> s = new HashSet<String>(sharedPref.getStringSet(getApplicationContext().getString(R.string.pending_alarm),
                new HashSet<String>()));
        SharedPreferences.Editor editor = sharedPref.edit();
        s.add(scheduleDrugDO.getAlarmId().intValue() + "/" + scheduleDrugDO.getDrug());
        editor.putStringSet(getApplicationContext().getString(R.string.pending_alarm), s);
        //persist immediatly the data in the shared pref
        editor.apply();

        //start service to keep update or set alarm
        Intent i = new Intent(getApplicationContext(), AlarmService.class);
        i.putExtra(AlarmService.SCHEDULE_EXTRA, scheduleDrugDO);
        i.putExtra(AlarmService.ACTION_EXTRA, "set");
        getApplicationContext().startService(i);

        createNotification(getApplicationContext(), intent, s);

    }

    public void createNotification(Context context, Intent intent, Set<String> set_result) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.pending_schedule)
                .setColor(getResources().getColor(R.color.colorPrimary));


        if (set_result.size() == 1) {
            mBuilder.setContentTitle("Drug reminder");
            for (String s : set_result) {
                mBuilder.setContentText(s.split("/")[1]);
            }

        //single line
        } else {
            //multiline
            mBuilder.setContentTitle("Drug reminder")
                    .setContentText("...");

            NotificationCompat.InboxStyle inboxStyle =
                    new NotificationCompat.InboxStyle();
            // Sets a title for the Inbox in expanded layout
            inboxStyle.setBigContentTitle("Drug reminder");
            // Moves events into the expanded layout
            for (String s : set_result) {
                inboxStyle.addLine(s.split("/")[1]);
            }
            // Moves the expanded layout object into the notification object.
            mBuilder.setStyle(inboxStyle);
        }

        Intent resultIntent = new Intent(context, HomeActivity.class);
        resultIntent.putExtra(SplashActivity.ACTIVITY_HOME_FRAGMENT_EXTRA, "fragment_pending");
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        -2,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_VIBRATE);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        mBuilder.setAutoCancel(true);
// notificationID allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
