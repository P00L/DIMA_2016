package com.mysampleapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DemoNoSQLTableScheduleDrug;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;
import com.mysampleapp.fragment.ScheduleFormFragment;

import java.util.ArrayList;

public class BootReceiver extends BroadcastReceiver {

    DemoNoSQLOperation operation;
    ArrayList<ScheduleDrugDO> items;

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
                //start service to keep update or set alarm
                Intent i = new Intent(context, AlarmService.class);
                i.putExtra(AlarmService.SCHEDULE_EXTRA, scheduleDrugDO);
                i.putExtra(AlarmService.ACTION_EXTRA, "set");
                context.startService(i);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

        }
    }

      private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}