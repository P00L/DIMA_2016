package com.mysampleapp;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlarmService extends IntentService {

    public static final String SCHEDULE_EXTRA = "schedule";
    public static final String ACTION_EXTRA = "action";
    public static final String SCHEDULE_OLD_EXTRA = "scheduleOld";
    public static final String DRUG_EXTRA = "drug";
    public static final String DRUG_OLD_EXTRA = "drugOld";

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.w("AlarmService", "service running");
        ScheduleDrugDO scheduleDrugDO;
        ScheduleDrugDO scheduleDrugDO_old;
        DrugDO drugDO;
        DrugDO drugDO_old;
        String action = intent.getStringExtra(AlarmService.ACTION_EXTRA);
        switch (action) {
            case "set":
                Log.w("AlarmService", "setAlarm");
                scheduleDrugDO = intent.getParcelableExtra(AlarmService.SCHEDULE_EXTRA);
                setAlarm(scheduleDrugDO);
                break;
            case "update":
                Log.w("AlarmService", "updateAlarm");
                scheduleDrugDO = intent.getParcelableExtra(AlarmService.SCHEDULE_EXTRA);
                scheduleDrugDO_old = intent.getParcelableExtra(AlarmService.SCHEDULE_OLD_EXTRA);
                updateAlarm(scheduleDrugDO, scheduleDrugDO_old);
                break;
            case "cancel":
                scheduleDrugDO = intent.getParcelableExtra(AlarmService.SCHEDULE_EXTRA);
                Log.w("AlarmService", "cancelAlarm");
                cancelAlarm(scheduleDrugDO);
                break;
            case "update_drug":
                Log.w("AlarmService", "updateDrug");
                drugDO = intent.getParcelableExtra(AlarmService.DRUG_EXTRA);
                drugDO_old = intent.getParcelableExtra(AlarmService.DRUG_OLD_EXTRA);
                updateDrug(drugDO, drugDO_old);
                break;
            case "cancel_drug":
                //TODO IMPLEMENT
                Log.w("AlarmService", "cancelDrug");
                drugDO = intent.getParcelableExtra(AlarmService.DRUG_EXTRA);
                drugDO_old = intent.getParcelableExtra(AlarmService.DRUG_OLD_EXTRA);
                cancelDrug(drugDO);
                break;
        }

    }

    private void setAlarm(ScheduleDrugDO scheduleDrugDO) {
        Log.w("AlarmService", "started set alarm");
        // Restart alarm if device is rebooted ---> starting BootReceiver
        ComponentName receiver = new ComponentName(getApplicationContext(), BootReceiver.class);
        PackageManager pm = getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        String days = scheduleDrugDO.getDay();
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
            Log.w("AlarmService", "today in the list");
            nextDay.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourmin[0]));
            nextDay.set(Calendar.MINUTE, Integer.parseInt(hourmin[1]));

            if (Calendar.getInstance().getTimeInMillis() < nextDay.getTimeInMillis()) {
                //schedule alarm today
                //set up time difference from now to next alarm
                Log.w("AlarmService", "set alarm today in the future");
                timeDiff = nextDay.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                alarmDelay = Calendar.getInstance().getTimeInMillis() + timeDiff;

            } else {
                //schedule alarm next day starting searching from today ---> easy
                //getting next index to set alarm
                Log.w("AlarmService", "set alarm next day from today");
                int next_day_index;
                if (today_index == list.size() - 1) {
                    next_day_index = 0;
                } else {
                    next_day_index = today_index + 1;
                }
                //add 1 day to ensure to avoid picking really today
                nextDay.add(Calendar.DATE, 1);
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
            Log.w("AlarmService", "set alarm next day from uncknown day");
            int next_day_index = 8;
            if (calNow.get(Calendar.DAY_OF_WEEK) == 7) {
                //find smallest value in list
                next_day_index = 0;
            } else {
                //find first value greater than today
                for (String s : list) {
                    if (Integer.parseInt(s) > calNow.get(Calendar.DAY_OF_WEEK)) {
                        next_day_index = list.indexOf(s);
                        break;
                    }
                }
                if (next_day_index == 8) {
                    for (String s : list) {
                        if (Integer.parseInt(s) < calNow.get(Calendar.DAY_OF_WEEK)) {
                            next_day_index = list.indexOf(s);
                            break;
                        }
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
        Log.w("AlarmService", "day received " + list.toString());
        Log.w("AlarmService", "TODAY " + format.format(calNow.getTime()));
        Log.w("AlarmService", "NEXT DAY " + format.format(nextDay.getTime()));

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Define our intention of executing AlertReceiver
        Intent alertIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

        alertIntent.putExtra(AlarmService.SCHEDULE_EXTRA, scheduleDrugDO);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), scheduleDrugDO.getAlarmId().intValue(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.w("AlarmService", "alarm delay " + alarmDelay);

        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmDelay, alarmIntent);
    }

    private void updateAlarm(ScheduleDrugDO scheduleDrugDO, ScheduleDrugDO scheduleDrugDO_old) {
        Log.w("AlarmService", "started update alarm");
        cancelAlarm(scheduleDrugDO_old);
        setAlarm(scheduleDrugDO);
    }

    private void cancelAlarm(ScheduleDrugDO scheduleDrugDO) {
        Log.w("AlarmService", "started cancel alarm");
        //clear shared preference if schedule pending
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getApplicationContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);

        //getting data from shared pref and add the new alarm manager as pending
        //returning the pending list of id alarm manager as string or an empty set
        Set<String> s = new HashSet<String>(sharedPref.getStringSet(getApplicationContext().getString(R.string.pending_alarm),
                new HashSet<String>()));
        Log.w("AlarmService", "pending notification " + s.toString());
        String pending_id = scheduleDrugDO.getAlarmId().intValue() + "/" + scheduleDrugDO.getDrug();
        Log.w("AlarmService", "pending id " + pending_id);
        //if there are pending notification in the shared pref we delete it
        Boolean removed = s.remove(pending_id);
        Log.w("AlarmService", "removed " + removed);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(getApplicationContext().getString(R.string.pending_alarm), s);
        editor.apply();

        //clear alarm manager accordng to alarm id
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent alertIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), scheduleDrugDO.getAlarmId().intValue(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmIntent);
    }

    private void updateDrug(DrugDO drugDO, DrugDO drugDO_old) {
        Log.w("AlarmService","old name " +drugDO_old.getName());
        DemoNoSQLOperation operation;
        ArrayList<ScheduleDrugDO> items = new ArrayList<>();
        ArrayList<ScheduleDrugDO> modifyItemsList = new ArrayList<>();
        DynamoDBMapper mapper;
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();

        //getting all the scheduleDrugDO
        DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getApplicationContext())
                .getNoSQLTableByTableName("ScheduleDrug");
        operation = (DemoNoSQLOperation) demoTable.getOperationByName(getApplicationContext(), "all");

        //TODO inventarsi qualcosa se va male la query
        try {
            operation.executeOperation();
            items = ((DemoNoSQLTableScheduleDrug.DemoQueryWithPartitionKeyOnly) operation).getResultArray();
        } catch (final AmazonClientException ex) {

        }

        //creating a list of scheduleDugDO which should be modified since the name of drug changed
        for (ScheduleDrugDO d : items) {
            if (d.getDrug().equals(drugDO_old.getName())) {
                modifyItemsList.add(d);
                Log.w("AlarmService","schedule drug "+d.getDrug());
            }
        }

        //modify the founded schedule
        for (ScheduleDrugDO d : modifyItemsList) {
            //save a copy of the old schedule to update it
            ScheduleDrugDO d_old = new ScheduleDrugDO();
            d_old.setDrug(d.getDrug());
            d_old.setAlarmId(d.getAlarmId());
            d_old.setNotes(d.getNotes());
            d_old.setDay(d.getDay());
            d_old.setUserId(d.getUserId());
            d_old.setHour(d.getHour());

            //update db with the new name
            d.setDrug(drugDO.getName());
            try {
                mapper.save(d);
            } catch (final AmazonClientException ex) {
                Log.e("ScheduleFormFragment", "Failed saving item : " + ex.getMessage(), ex);
            }
            //reschedule alarm
            updateAlarm(d, d_old);
        }
        //TODO INVOCARE CREATE NOTIFICATION
    }

    private void cancelDrug(DrugDO drugDO) {

    }
}
