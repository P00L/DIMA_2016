package com.mysampleapp.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysampleapp.R;
import com.mysampleapp.nosqldb.models.ScheduleDrugDO;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private final static String LOG_TAG = PendingScheduleAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<ScheduleDrugDO> mList;
    private ItemClickListener mListener;
    private ItemClickListenerAnimation animationListener;

    public HomeAdapter(Context contexts, ArrayList<ScheduleDrugDO> list, ItemClickListener mListener,
                                  ItemClickListenerAnimation itemAnimationListener) {
        this.mContext = contexts;
        this.mList = list;
        this.mListener = mListener;
        this.animationListener = itemAnimationListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_home, parent, false);
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
        holder.hourTextView.setText(scheduleDrugDO.getHour());
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
        private ImageView imageView;
        private TextView hourTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            imageView = (ImageView) itemView.findViewById(R.id.icon_ID);
            drugTextView = (TextView) itemView.findViewById(R.id.doc_name);
            quantityTextView = (TextView) itemView.findViewById(R.id.quantity);
            hourTextView = (TextView) itemView.findViewById(R.id.hour);
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
