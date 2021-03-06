package com.mysampleapp.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysampleapp.R;
import com.mysampleapp.nosqldb.models.ScheduleDrugDO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<ScheduleDrugDO> mList;
    private ArrayList<ScheduleDrugDO> mListCopy;
    private final ItemClickListenerAnimation listener;

    public ScheduleAdapter(Context contexts, ArrayList<ScheduleDrugDO> list, ItemClickListenerAnimation listener) {
        this.mContext = contexts;
        this.mList = list;
        this.listener = listener;
        this.mListCopy = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.drugTextView.setText(mList.get(position).getDrug());

        //set sottoscorta to take
        switch (mList.get(position).getQuantity().toString()) {
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

        String days = mList.get(position).getDay();
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
        if (list.contains(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + "")) {
            holder.imageButton.setImageResource(R.drawable.calendar_blue);

        }else{
            holder.imageButton.setImageResource(R.drawable.calendar);
        }

    holder.setClickListener(new

    ItemClickListener() {
        @Override
        public void onClick (View view,int position, boolean isLongClick){
            listener.onClick(holder.imageView, position, isLongClick);
        }
    }

    );

    ViewCompat.setTransitionName(holder.imageView,String.valueOf(position)+"_image");
}

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //FONDAMENTALE per SEARCHVIEW
    public ScheduleDrugDO getItem(int position) {
        return mList.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mList = (ArrayList<ScheduleDrugDO>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<ScheduleDrugDO> filteredResults = null;
                if (constraint.length() == 0) {
                    filteredResults = mListCopy;
                } else {
                    filteredResults = getFilteredResults(constraint.toString().toLowerCase());
                }

                FilterResults results = new FilterResults();
                results.values = filteredResults;

                return results;
            }
        };
    }

    protected ArrayList<ScheduleDrugDO> getFilteredResults(String constraint) {
        ArrayList<ScheduleDrugDO> results = new ArrayList<>();

        for (ScheduleDrugDO item : mListCopy) {
            if (item.getDrug().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }

public static class ViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {
    private TextView drugTextView;
    private TextView quantityTextView;
    private ImageView imageView;
    private ImageButton imageButton;
    private ItemClickListener clickListener;

    public ViewHolder(View itemView) {
        super(itemView);
        drugTextView = (TextView) itemView.findViewById(R.id.schedule_drug_name);
        imageView = (ImageView) itemView.findViewById(R.id.icon_ID);
        quantityTextView = (TextView) itemView.findViewById(R.id.schedule_drug_qty);
        imageButton = (ImageButton) itemView.findViewById(R.id.active_image_button);
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