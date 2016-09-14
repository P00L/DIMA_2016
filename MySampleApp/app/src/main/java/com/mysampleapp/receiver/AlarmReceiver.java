package com.mysampleapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mysampleapp.service.AlarmService;
import com.mysampleapp.service.ServiceNotification;
import com.mysampleapp.nosqldb.models.ScheduleDrugDO;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, ServiceNotification.class);
        ScheduleDrugDO scheduleDrugDO = intent.getParcelableExtra(AlarmService.SCHEDULE_EXTRA);
        i.putExtra(AlarmService.SCHEDULE_EXTRA, scheduleDrugDO);
        context.startService(i);

    }

}
