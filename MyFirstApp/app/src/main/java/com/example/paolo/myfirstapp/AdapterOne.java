package com.example.paolo.myfirstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.paolo.myfirstapp.db.TabellaDb;

import java.util.List;

/**
 * Created by paolo on 30/03/2016.
 */
public class AdapterOne extends BaseAdapter {

    private List<TabellaDb> tabellaDbs =null;
    private Context context=null;

    public AdapterOne(Context context,List<TabellaDb> tabellaDbs) {
        this.tabellaDbs = tabellaDbs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return tabellaDbs.size();
    }

    @Override
    public Object getItem(int position) {
        return tabellaDbs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View v, ViewGroup g) {
        if (v==null)
        {

            v= LayoutInflater.from(context).inflate(R.layout.list_row, null);
        }
        TabellaDb tb=(TabellaDb) getItem(position);
        TextView txt=(TextView) v.findViewById(R.id.txt_article_description);
        txt.setText(tb.getColumn_1());
        txt=(TextView) v.findViewById(R.id.txt_article_url);
        txt.setText(tb.getColumn_2());
        txt=(TextView) v.findViewById(R.id.txt_article_datetime);
        txt.setText("id" + tb.getIds());
        return v;
    }

}
