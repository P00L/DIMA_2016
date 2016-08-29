package com.mysampleapp.adapter;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mysampleapp.R;
import com.mysampleapp.demo.nosql.DoctorDO;
import com.mysampleapp.fragment.DocFragment;

import java.util.ArrayList;

public class DocAdapter extends RecyclerView.Adapter<DocAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<DoctorDO> mList;
    private final ItemClickListenerAnimation listener;

    public DocAdapter(Context contexts, ArrayList<DoctorDO> list, ItemClickListenerAnimation listener) {
        this.mContext = contexts;
        this.mList = list;
        this.listener = listener;
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
        holder.imageView.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
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