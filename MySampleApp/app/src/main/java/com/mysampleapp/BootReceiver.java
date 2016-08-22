package com.mysampleapp;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.amazonaws.mobile.AWSMobileClient;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableDrug;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DemoNoSQLTableScheduleDrug;
import com.mysampleapp.demo.nosql.DrugDO;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;
import com.mysampleapp.fragment.ScheduleFormFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    DemoNoSQLOperation operation;
    ScheduleDrugDO[] items;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            new MyAsyncTask(context).execute();
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context context;

        public MyAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //ahahah a while true till the connection is available may we can think another solution
            while (!isNetworkConnected(context)){
            }

            // Set the alarm here.
            DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(context)
                    .getNoSQLTableByTableName("ScheduleDrug");
            operation = (DemoNoSQLOperation) demoTable.getOperationByName(context, "all");
            operation.executeOperation();
            items = ((DemoNoSQLTableScheduleDrug.DemoQueryWithPartitionKeyOnly) operation).getResultArray();

            for(ScheduleDrugDO scheduleDrugDO : items){
                setAlarm(context,scheduleDrugDO);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

        }
    }

    public void setAlarm(Context context,ScheduleDrugDO scheduleDrugDO) {

        String days = scheduleDrugDO.getDay();
        Log.w("days",days);
        String[] splitday = days.split("/");
        List<String> list = new ArrayList<String>();
        //convert string format of day to int to match Calendar day
        for (String s : splitday)
            switch (s) {
                case "L":
                    list.add("2");
                    break;
                case "MA":
                    list.add("3");
                    break;
                case "ME":
                    list.add("4");
                    break;
                case "G":
                    list.add("5");
                    break;
                case "V":
                    list.add("6");
                    break;
                case "S":
                    list.add("7");
                    break;
                case "D":
                    list.add("1");
                    break;
                default:
                    list.add("x");
                    break;
            }
        Collections.sort(list);
        //get today
        Calendar calNow = Calendar.getInstance();
        //get next day inizialized to today
        Calendar nextDay = Calendar.getInstance();
        long timeDiff;
        long alarmDelay;
        String[] hourmin = scheduleDrugDO.getHour().split(":");
        //get today index inside scheduled day alarm
        int today_index = list.indexOf(calNow.get(Calendar.DAY_OF_WEEK) + "");

        if (today_index != -1) {
            // schedule alarm today if in the future time or next day
            nextDay.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourmin[0]));
            nextDay.set(Calendar.MINUTE, Integer.parseInt(hourmin[1]));

            if (Calendar.getInstance().getTimeInMillis() < nextDay.getTimeInMillis()) {
                //schedule alarm today
                //set up time difference from now to next alarm
                timeDiff = nextDay.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                alarmDelay = Calendar.getInstance().getTimeInMillis() + timeDiff;

            } else {
                //schedule alarm next day starting searching from today ---> easy
                //getting next index to set alarm
                int next_day_index;
                if (today_index == list.size() - 1) {
                    next_day_index = 0;
                } else {
                    next_day_index = today_index + 1;
                }

                //get nex day Calendar format
                while (nextDay.get(Calendar.DAY_OF_WEEK) != Integer.parseInt(list.get(next_day_index))) {
                    nextDay.add(Calendar.DATE, 1);
                }
                //setting hour and minute
                nextDay.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourmin[0]));
                nextDay.set(Calendar.MINUTE, Integer.parseInt(hourmin[1]));

                timeDiff = nextDay.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                alarmDelay = Calendar.getInstance().getTimeInMillis() + timeDiff;
            }
        } else {
            //schedule alarm next day searching next value from today ---> harder
            //getting next index to set alarm
            int next_day_index = 8;
            if (calNow.get(Calendar.DAY_OF_WEEK) == 7) {
                //find smallest value in list
                next_day_index =  0;
            } else {
                //find first value greater than today
                for (String s : list){
                    if (Integer.parseInt(s) > calNow.get(Calendar.DAY_OF_WEEK)){
                        next_day_index = list.indexOf(s);
                        break;
                    }
                }
            }

            //get nex day Calendar format
            while (nextDay.get(Calendar.DAY_OF_WEEK) != Integer.parseInt(list.get(next_day_index))) {
                nextDay.add(Calendar.DATE, 1);
            }
            //setting hour and minute
            nextDay.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourmin[0]));
            nextDay.set(Calendar.MINUTE, Integer.parseInt(hourmin[1]));

            timeDiff = nextDay.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
            alarmDelay = Calendar.getInstance().getTimeInMillis() + timeDiff;

        }


        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
        Log.w("TODAY", format.format(calNow.getTime()));
        Log.w("NEXT DAY", format.format(nextDay.getTime()));

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Define our intention of executing AlertReceiver
        Intent alertIntent = new Intent(context, AlarmReceiver.class);

        alertIntent.putExtra(ScheduleFormFragment.ALARM_ID_EXTRA, scheduleDrugDO.getAlarmId().intValue());
        alertIntent.putExtra(ScheduleFormFragment.DRUG_EXTRA, scheduleDrugDO.getDrug());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, scheduleDrugDO.getAlarmId().intValue(), alertIntent, PendingIntent.FLAG_ONE_SHOT);

        Log.w("alarm delay", alarmDelay + "");

        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmDelay, alarmIntent);

        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}