package com.piapps.flashcard.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.piapps.flashcard.R;
import com.piapps.flashcard.model.Flashcard;
import com.piapps.flashcard.util.Utils;

import java.util.List;

/**
 * Created by abduaziz on 2/14/17.
 */

public class RVTrashAdapter extends RecyclerView.Adapter<RVTrashAdapter.ViewHolder> {

    List<Flashcard> list;

    public RVTrashAdapter(List<Flashcard> list) {
        this.list = list;
    }

    @Override
    public RVTrashAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_main_trash, parent, false));
    }

    @Override
    public void onBindViewHolder(RVTrashAdapter.ViewHolder holder, int position) {
        Flashcard item = list.get(position);
        holder.bindItem(item.getTitle(), item.getCount(), item.getLabel(), item.getColor(), item.getSetId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView done;
        ImageView label;
        ImageView imageView;
        String id = "";

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text);
            done = (TextView) itemView.findViewById(R.id.textDone);
            label = (ImageView) itemView.findViewById(R.id.label);
            imageView = (ImageView) itemView.findViewById(R.id.card);
        }

        public void bindItem(String text, String count, String labelText, String cardColor, String id) {
            title.setText(text);
            this.id = id;
            done.setText(count + " " + itemView.getContext().getResources().getString(R.string.flashcards));
            //set the color to imageView
            imageView.setBackgroundColor(Color.parseColor(cardColor));

            if (labelText.equals(Utils.LABEL_IMPORTANT))
                label.setImageResource(R.drawable.ic_important_24dp);
            else if (labelText.equals(Utils.LABEL_TODO))
                label.setImageResource(R.drawable.ic_todo_24dp);
            else if (labelText.equals(Utils.LABEL_DICTIONARY))
                label.setImageResource(R.drawable.ic_dictionary_24dp);
            else
                label.setImageResource(R.drawable.ic_other_label_24dp);

        }
    }
}
