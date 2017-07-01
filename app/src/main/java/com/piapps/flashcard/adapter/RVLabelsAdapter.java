package com.piapps.flashcard.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.piapps.flashcard.R;
import com.piapps.flashcard.model.CLabel;

import java.util.List;

/**
 * Created by abduaziz on 2/18/17.
 */

public class RVLabelsAdapter extends RecyclerView.Adapter<RVLabelsAdapter.ViewHolder> {

    List<CLabel> list;
    private boolean onBind;

    public RVLabelsAdapter(List<CLabel> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_labels, parent, false);
        ViewHolder holder = new ViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(list.get(position).getTitle());
        holder.icon.setImageResource(list.get(position).getIcon());
        onBind = true;
        holder.checkBox.setChecked(list.get(position).isChecked());
        onBind = false;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener{

        ImageView icon;
        TextView textView;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.label_name);
            icon = (ImageView) itemView.findViewById(R.id.label_icon);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(!onBind) {
                list.get(getAdapterPosition()).setChecked(b);
                notifyDataSetChanged();
            }
        }
    }

}
