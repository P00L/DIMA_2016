package com.example.paolo.myfirstapp.Recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.paolo.myfirstapp.R;
import com.example.paolo.myfirstapp.db.TabellaDb;

import java.util.List;

/*praticamente questa non è altro che una lista di oggetti view :)*/
public class AdapterRecycler extends  RecyclerView.Adapter<AdapterRecycler.TabellaViewHolder> {

    private List<TabellaDb> tabellaDbs;
    private Context context;


    public AdapterRecycler(Context context,List<TabellaDb> tabellaDbs) {
        this.tabellaDbs = tabellaDbs;
        this.context = context;
    }

    // Creo una view riga
    @Override
    public TabellaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // creo una nuova view che sarebbe l'xml delal riga
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        //setta roba della view
        return new TabellaViewHolder(v);
    }

    // setto gli elementi delal view riga da visualizzare grazie alla posizione nella lista tabellaDbs
    @Override
    public void onBindViewHolder(TabellaViewHolder holder, int position) {
        TabellaDb tb = tabellaDbs.get(position);
        holder.txt_1.setText(tb.getColumn_1());
        holder.txt_2.setText(tb.getColumn_2());
        holder.txt_3.setText("id" + tb.getIds());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tabellaDbs.size();
    }

    public TabellaDb getTabella(int position){
        return tabellaDbs.get(position);
    }

    //classe di appoggio per definire il contenuto di una riga
    public static class TabellaViewHolder extends RecyclerView.ViewHolder{

        protected TextView txt_1;
        protected TextView txt_2;
        protected TextView txt_3;

        public TabellaViewHolder(View itemView) {
            super(itemView);
            txt_1 = (TextView) itemView.findViewById(R.id.txt_article_description);
            txt_2 = (TextView) itemView.findViewById(R.id.txt_article_url);
            txt_3 = (TextView) itemView.findViewById(R.id.txt_article_datetime);
        }
    }

}
