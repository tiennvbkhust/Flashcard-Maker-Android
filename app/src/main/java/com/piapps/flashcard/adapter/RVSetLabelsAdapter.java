package com.piapps.flashcard.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.piapps.flashcard.R;
import com.piapps.flashcard.model.Label;

import java.util.List;

/**
 * Created by abduaziz on 2/18/17.
 */

public class RVSetLabelsAdapter extends RecyclerView.Adapter<RVSetLabelsAdapter.ViewHolder>{

    List<Label> list;

    public RVSetLabelsAdapter(List<Label> list){
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_set_labels,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public ViewHolder(View itemView){
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.label);
        }

    }

}
