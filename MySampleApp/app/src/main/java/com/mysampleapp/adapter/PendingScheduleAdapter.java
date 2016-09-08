package com.mysampleapp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.mysampleapp.R;
import com.mysampleapp.demo.nosql.ScheduleDrugDO;
import com.mysampleapp.fragment.ScheduleFragment;

import java.util.ArrayList;

public class PendingScheduleAdapter extends RecyclerView.Adapter<PendingScheduleAdapter.ViewHolder> {
    private final static String LOG_TAG = PendingScheduleAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<ScheduleDrugDO> mList;
    private ItemClickListener mListener;
    private ItemClickListenerAnimation animationListener;

    public PendingScheduleAdapter(Context contexts, ArrayList<ScheduleDrugDO> list, ItemClickListener mListener,
    ItemClickListenerAnimation itemAnimationListener) {
        this.mContext = contexts;
        this.mList = list;
        this.mListener = mListener;
        this.animationListener = itemAnimationListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_pending_schedule, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //TODO togliere toast
        final ScheduleDrugDO scheduleDrugDO = mList.get(position);
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                animationListener.onClick(holder.imageView, position, isLongClick);
            }
        });

        ViewCompat.setTransitionName(holder.imageView, String.valueOf(position) + "_image");

        //set drug name
        holder.drugTextView.setText(scheduleDrugDO.getDrug());
        //set sottoscorta to take
        switch (scheduleDrugDO.getQuantity().toString()) {
            case "1.0":
                holder.quantityTextView.setText("take " + "1");
                break;
            case "0.5":
                holder.quantityTextView.setText("take " + "1/2");
                break;
            case "0.25":
                holder.quantityTextView.setText("take " + "1/4");
                break;
            case "2.0":
                holder.quantityTextView.setText("take " + "2");
                break;
        }
        //skip button listener
        holder.skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v,position,false);
            }
        });
        //take button listener
        holder.takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v,position,false);
            }
        });
        //postpone button listener
        holder.postponeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(v,position,false);
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
        private Button postponeButton;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            imageView = (ImageView) itemView.findViewById(R.id.icon_ID);
            drugTextView = (TextView) itemView.findViewById(R.id.doc_name);
            quantityTextView = (TextView) itemView.findViewById(R.id.quantity);
            skipButton = (Button) itemView.findViewById(R.id.skip);
            takeButton = (Button) itemView.findViewById(R.id.take);
            postponeButton = (Button) itemView.findViewById(R.id.postpone);
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
