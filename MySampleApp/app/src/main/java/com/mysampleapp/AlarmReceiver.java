package com.mysampleapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.activity.SplashActivity;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;
import com.mysampleapp.fragment.ScheduleFormFragment;

import java.util.HashSet;
import java.util.Set;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, ServiceNotification.class);
        ScheduleDrugDO scheduleDrugDO = intent.getParcelableExtra(AlarmService.SCHEDULE_EXTRA);
        i.putExtra(AlarmService.SCHEDULE_EXTRA, scheduleDrugDO);
        context.startService(i);

    }

}
