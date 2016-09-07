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
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        //setting up all the value of the image according to the position of the row
        holder.nameTextView.setText(mList.get(position).getName());
        holder.surnameTextView.setText(mList.get(position).getSurname());
        if(mList.get(position).getActive())
            holder.imageButton.setImageResource(R.drawable.btn_star_big_on_pressed);
        else
            holder.imageButton.setImageResource(android.R.drawable.btn_star);


        //setting up the name of the shared element
        //done così in the github tutorial i leave it like this
        ViewCompat.setTransitionName(holder.imageView, String.valueOf(position) + "_image");

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                listener.onClick(holder.imageView, position, isLongClick);
            }
        });
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
        private TextView nameTextView;
        private TextView surnameTextView;
        private ItemClickListener clickListener;
        private ImageView imageView;
        private ImageButton imageButton;

        //getting the reference of all the view element of one row
        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.doc_name);
            surnameTextView = (TextView) itemView.findViewById(R.id.doc_surname);
            imageView = (ImageView) itemView.findViewById(R.id.icon_ID);
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