package com.mysampleapp.adapter;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysampleapp.R;
import com.mysampleapp.demo.nosql.DoctorDO;

import java.util.ArrayList;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<DoctorDO> mList;
    private ArrayList<DoctorDO> mListCopy;
    private final ItemClickListenerAnimation listener;

    public DocAdapter(Context contexts, ArrayList<DoctorDO> list, ItemClickListenerAnimation listener) {
        this.mContext = contexts;
        this.mList = list;
        this.listener = listener;
        this.mListCopy = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_doc, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //setting up all the value of the image according to the position of the row
        holder.titleTextView.setText(mList.get(position).getName());
        holder.imageView.setImageResource(R.drawable.ic_icon_doctor);
        //handle click listener of all the row
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //redirect it to the fragment list passing the image view element shared to be animated
                listener.onClick(holder.imageView, position, isLongClick);
            }
        });
        //setting up the name of the shared element
        //done così in the github tutorial i leave it like this
        ViewCompat.setTransitionName(holder.imageView, String.valueOf(position) + "_image");

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mList = (ArrayList<DoctorDO>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<DoctorDO> filteredResults = null;
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

    protected ArrayList<DoctorDO> getFilteredResults(String constraint) {
        ArrayList<DoctorDO> results = new ArrayList<>();

        for (DoctorDO item : mListCopy) {
            if (item.getName().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }

    //this listener is to catch click and long click on all the row of the recycler view
    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private TextView titleTextView;
        private ItemClickListener clickListener;
        private ImageView imageView;

        //getting the reference of all the view element of one row
        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.text_name);
            imageView = (ImageView) itemView.findViewById(R.id.icon_ID);
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