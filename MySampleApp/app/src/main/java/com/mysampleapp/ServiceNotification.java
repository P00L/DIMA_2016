package com.mysampleapp;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.mysampleapp.activity.HomeActivity;
import com.mysampleapp.activity.SplashActivity;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DemoNoSQLTableScheduleDrug;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;
import com.mysampleapp.fragment.ScheduleFormFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
        int alarm_id = intent.getIntExtra(ScheduleFormFragment.ALARM_ID_EXTRA, 0);
        final String drugName = intent.getStringExtra(ScheduleFormFragment.DRUG_EXTRA);

        //get shared pref file
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getApplicationContext().getString(R.string.preference_file_name), Context.MODE_PRIVATE);

        //getting data from shared pref and add the new alarm manager as pending
        //returning the pending list of id alarm manager as string or an empty set
        Set<String> s = new HashSet<String>(sharedPref.getStringSet(getApplicationContext().getString(R.string.pending_alarm),
                new HashSet<String>()));
        SharedPreferences.Editor editor = sharedPref.edit();
        s.add(alarm_id + "/" + drugName);
        editor.putStringSet(getApplicationContext().getString(R.string.pending_alarm), s);
        //persist immediatly the data in the shared pref
        editor.apply();
        //TODO METTERE UN CONTROLLO SULLA CONNESSIONE COSA FACCIAMO PER RIAGGIORNARE SE MANCA LA CONNESSIONE????
        //rischedula alarm per il prossimo giorno qui si che adesso ci vuole la query da db ma solo
        //per recuperare una specifica entry
        final DemoNoSQLOperation operation;
        DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(getApplicationContext())
                .getNoSQLTableByTableName("ScheduleDrug");
        operation = ((DemoNoSQLTableScheduleDrug) demoTable).getOperationByNameSingle(getApplicationContext(), "one", Double.parseDouble(alarm_id + ""));
        final int finalAlarmID = alarm_id;

        Boolean bb = operation.executeOperation();
        Log.w("SUCCESSO", bb.toString());
        ScheduleDrugDO item = ((DemoNoSQLTableScheduleDrug.DemoGetWithPartitionKeyAndSortKey) operation).getResult();
        Log.w("ALARM ID EXTRA", finalAlarmID + "");
        Log.w("ALARM ID DB", item.getAlarmId() + "");

        //TODO fare un metodo forse da usare anche la prima volta che si setta
        //modifica dopo cambio da Set<String> a String
        // la stringa salvata e ritornata da getDay() deve essere formattata "day1/day2/dayn"
        String days = item.getDay();
        String[] splitday = days.split("/");
        List<String> list = new ArrayList<String>();
        //convert string format of day to int to match Calendar day
        for (String day : splitday)
            switch (day) {
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

        Log.w("SERVICE LIST", list.toString());
        //get today
        Calendar calNow = Calendar.getInstance();
        Log.w("SERVICE CAL", calNow.get(Calendar.DAY_OF_WEEK) + "");

        Log.w("index of", list.indexOf(calNow.get(Calendar.DAY_OF_WEEK) + "") + "");
        //get today index inside scheduled day alarm
        int today_index = list.indexOf(calNow.get(Calendar.DAY_OF_WEEK) + "");

        //getting nex index to set alarm
        int next_day_index;
        if (today_index == list.size() - 1) {
            next_day_index = 0;
        } else {
            next_day_index = today_index + 1;
        }
        Log.w("next day index", next_day_index + "");
        Log.w("next day ", list.get(next_day_index) + "");

        //get nex day Calendar format
        Calendar next_day = Calendar.getInstance();
        while (next_day.get(Calendar.DAY_OF_WEEK) != Integer.parseInt(list.get(next_day_index))) {
            next_day.add(Calendar.DATE, 1);
        }
        //setting hour and minute
        String[] hourmin = item.getHour().split(":");
        next_day.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourmin[0]));
        next_day.set(Calendar.MINUTE, Integer.parseInt(hourmin[1]));


        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
        Log.w("TODAY", format.format(calNow.getTime()));
        Log.w("NEXT DAY", format.format(next_day.getTime()));

        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        // Define our intention of executing AlertReceiver
        Intent alertIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

        alertIntent.putExtra(ScheduleFormFragment.ALARM_ID_EXTRA, finalAlarmID);
        alertIntent.putExtra(ScheduleFormFragment.DRUG_EXTRA, drugName);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), finalAlarmID, alertIntent, PendingIntent.FLAG_ONE_SHOT);

        //set up time difference from now to next alarm
        long timeDiff = next_day.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        long alarmDelay = Calendar.getInstance().getTimeInMillis() + timeDiff;

        Log.w("alarm delay", alarmDelay + "");

        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmDelay, alarmIntent);

        createNotification(getApplicationContext(), intent, s);

    }

    public void createNotification(Context context, Intent intent, Set<String> set_result) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Drug reminder")
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
