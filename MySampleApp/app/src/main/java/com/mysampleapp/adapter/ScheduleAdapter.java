package com.mysampleapp.adapter;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mysampleapp.R;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;
import com.mysampleapp.fragment.ScheduleFragment;

import java.util.ArrayList;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ScheduleDrugDO> mList;

    public ScheduleAdapter(Context contexts, ArrayList<ScheduleDrugDO> list) {
        this.mContext = contexts;
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTextView.setText(mList.get(position).getDrug());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(mContext, "#" + position + " - " + mList.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "#" + position + " - " + mList.get(position), Toast.LENGTH_SHORT).show();
                    ScheduleFragment fragment = ScheduleFragment.newInstance(mList.get(position));
                    fragment.setResult(mList.get(position));
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
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        private TextView titleTextView;
        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView)itemView.findViewById(R.id.text_name);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

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
}