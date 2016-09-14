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
import com.mysampleapp.nosqldb.models.DrugDO;

import java.util.ArrayList;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.ViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<DrugDO> mList;
    private ArrayList<DrugDO> mListCopy;
    private final ItemClickListenerAnimation listener;

    public DrugAdapter(Context contexts, ArrayList<DrugDO> list, ItemClickListenerAnimation listener) {
        this.mContext = contexts;
        this.mList = list;
        this.listener = listener;
        this.mListCopy = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_drug, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.nameTextView.setText(mList.get(position).getName());
        holder.quantityTextView.setText(mList.get(position).getQuantity().intValue()+"");
        if(mList.get(position).getQuantity().intValue() <= mList.get(position).getMinqty().intValue())
            holder.imageButton.setImageResource(R.drawable.pill_red);
        else
            holder.imageButton.setImageResource(R.drawable.pill);
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                listener.onClick(holder.imageView, position, isLongClick);
            }
        });

        ViewCompat.setTransitionName(holder.imageView, String.valueOf(position) + "_image");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //FONDAMENTALE per SEARCHVIEW
    public DrugDO getItem(int position){
        return mList.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mList = (ArrayList<DrugDO>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<DrugDO> filteredResults = null;
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

    protected ArrayList<DrugDO> getFilteredResults(String constraint) {
        ArrayList<DrugDO> results = new ArrayList<>();

        for (DrugDO item : mListCopy) {
            if (item.getName().toLowerCase().contains(constraint)) {
                results.add(item);
            }
        }
        return results;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        private TextView nameTextView;
        private ImageView imageView;
        private TextView quantityTextView;
        private ImageButton imageButton;

        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.drug_name);
            imageView = (ImageView) itemView.findViewById(R.id.icon_ID);
            quantityTextView = (TextView) itemView.findViewById(R.id.drug_qty);
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