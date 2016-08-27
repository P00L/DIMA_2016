package com.mysampleapp.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.mysampleapp.AlarmService;
import com.mysampleapp.R;
import com.mysampleapp.demo.nosql.DemoNoSQLOperation;
import com.mysampleapp.demo.nosql.DemoNoSQLTableBase;
import com.mysampleapp.demo.nosql.DemoNoSQLTableDrug;
import com.mysampleapp.demo.nosql.DemoNoSQLTableFactory;
import com.mysampleapp.demo.nosql.DemoNoSQLTableScheduleDrug;
import com.mysampleapp.demo.nosql.DrugDO;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;
import com.mysampleapp.fragment.ScheduleFormFragment;
import com.mysampleapp.fragment.ScheduleFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PendingScheduleAdapter extends RecyclerView.Adapter<PendingScheduleAdapter.ViewHolder> {
    private final static String LOG_TAG = PendingScheduleAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<ScheduleDrugDO> mList;
    private ProgressDialog mProgressDialog;

    public PendingScheduleAdapter(Context contexts, ArrayList<ScheduleDrugDO> list) {
        this.mContext = contexts;
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_pending_schedule, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //TODO togliere toast
       final  ScheduleDrugDO scheduleDrugDO =  mList.get(position);
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(mContext, "#" + position + " - " + mList.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                } else {
                    Fragment fragment = ScheduleFragment.newInstance(scheduleDrugDO);
                    AppCompatActivity activity = (AppCompatActivity) mContext;
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();
                }
            }
        });

        //set drug name
        holder.drugTextView.setText(scheduleDrugDO.getDrug());
        //set quantity to take
        switch (scheduleDrugDO.getQuantity().toString()){
            case "1":
                holder.quantityTextView.setText("take " + "1");
                break;
            case "0.5":
                holder.quantityTextView.setText("take " + "1/2");
                break;
            case "0.25":
                holder.quantityTextView.setText("take " + "1/4");
                break;
            case "2":
                holder.quantityTextView.setText("take " + "2");
                break;
        }
        //skip button listener
        holder.skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("adapter","skip");
                mList.remove(scheduleDrugDO);
                PendingScheduleAdapter.this.notifyDataSetChanged();
                clearSharedPref(scheduleDrugDO);
            }
        });
        //skip button listener
        holder.takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("adapter","take");
                new SaveTask(scheduleDrugDO).execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private ItemClickListener clickListener;
        private TextView drugTextView;
        private TextView quantityTextView;
        private Button takeButton;
        private Button skipButton;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            drugTextView = (TextView) itemView.findViewById(R.id.drug_name);
            quantityTextView = (TextView) itemView.findViewById(R.id.quantity);
            skipButton = (Button) itemView.findViewById(R.id.skip);
            takeButton = (Button) itemView.findViewById(R.id.take);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        private Boolean success;
        private ScheduleDrugDO scheduleDrugDO;
        private DrugDO drugItem;

        public SaveTask(ScheduleDrugDO scheduleDrug) {
            scheduleDrugDO = scheduleDrug;
            success = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //TODO NON RIMANE SULLA ROTAZIONE
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(mContext);
            // Set progressdialog title
            mProgressDialog.setTitle("Save data");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            //getting the drug associated to the schedule to decrement the quantity
            DemoNoSQLTableBase demoTable = DemoNoSQLTableFactory.instance(mContext)
                    .getNoSQLTableByTableName("Drug");
            DemoNoSQLOperation operation = ((DemoNoSQLTableDrug)demoTable).getOperationByNameSingle(mContext,"one",scheduleDrugDO.getDrug());
            DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
            try {
                success = operation.executeOperation();
                drugItem = ((DemoNoSQLTableDrug.DemoGetWithPartitionKeyAndSortKey)operation).getResult();
                Log.w(LOG_TAG,drugItem.getQuantity() +"-"+ scheduleDrugDO.getQuantity() + "=" + (drugItem.getQuantity() - scheduleDrugDO.getQuantity()));
                drugItem.setQuantity(drugItem.getQuantity() - scheduleDrugDO.getQuantity());
                mapper.save(drugItem);
                success = true;

            } catch (final AmazonClientException ex) {
                Log.e("ASD", "Failed saving item : " + ex.getMessage(), ex);
                success = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            if (success) {
                mProgressDialog.dismiss();
                mList.remove(scheduleDrugDO);
                PendingScheduleAdapter.this.notifyDataSetChanged();
                clearSharedPref(scheduleDrugDO);

            } else {
                mProgressDialog.dismiss();
                //TODO se qualcosa non va bisogna resettare docDO ai valori precedenti se viene premuto discard
                //o forse meglio usare un oggetto temporaneo per salvare tutto se va bene settare anche quello giusto
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Error")
                        .setTitle("an error as occurred");
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private void clearSharedPref(ScheduleDrugDO scheduleDrugDO){
        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.preference_file_name), Context.MODE_PRIVATE);

        //getting data from shared pref and add the new alarm manager as pending
        //returning the pending list of id alarm manager as string or an empty set
        Set<String> s = new HashSet<String>(sharedPref.getStringSet(mContext.getString(R.string.pending_alarm),
                new HashSet<String>()));
        Log.w("AlarmService", "pending notification " + s.toString());
        String pending_id = scheduleDrugDO.getAlarmId().intValue() + "/" + scheduleDrugDO.getDrug();
        Log.w("AlarmService", "pending id " + pending_id);
        //if there are pending notification in the shared pref we delete it
        Boolean removed = s.remove(pending_id);
        Log.w("AlarmService", "removed " + removed);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(mContext.getString(R.string.pending_alarm), s);
        editor.apply();

    }
}
