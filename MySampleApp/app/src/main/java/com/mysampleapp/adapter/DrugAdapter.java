package com.mysampleapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysampleapp.R;
import com.mysampleapp.fragment.DrugFormFragment;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.ViewHolder> {
    private String[] mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public ViewHolder(View tv) {
            super(tv);
            mTextView = (TextView) tv.findViewById(R.id.text_name);
        }
    }

    public DrugAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DrugAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drug_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset[position]);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
