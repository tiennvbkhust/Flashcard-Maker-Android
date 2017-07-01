package com.piapps.flashcard.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.piapps.flashcard.R;
import com.piapps.flashcard.activity.FlashcardActivity;
import com.piapps.flashcard.model.Flashcard;
import com.piapps.flashcard.util.Utils;
import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abduaziz on 2/14/17.
 */

public class RVMainAdapter extends RecyclerView.Adapter<RVMainAdapter.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0x01;

    private static final int VIEW_TYPE_CONTENT = 0x00;

    private static final int LINEAR = 0;

    private final List<Item> items;
    private final List<Flashcard> list;
    private final Context mContext;
    int headerCount;
    private int mHeaderDisplay;
    private boolean mMarginsFixed;

    public RVMainAdapter(Context context, int headerMode, List<Flashcard> list) {
        mContext = context;

        mHeaderDisplay = headerMode;

        items = new ArrayList<>();
        this.list = list;

        //Insert headers into list of items.
        String lastHeader = "";
        int sectionManager = 0;
        headerCount = 0;
        int sectionFirstPosition = 0;
        for (int i = 0; i < list.size(); i++) {
            String header = list.get(i).getTitle().substring(0, 1);
            if (!TextUtils.equals(lastHeader, header)) {
                // Insert new header view and update section data.
                sectionFirstPosition = i + headerCount;
                lastHeader = header;
                headerCount += 1;
                items.add(new Item(header, "", "", "", true, sectionManager, sectionFirstPosition, ""));
            }
            items.add(new Item(
                    list.get(i).getTitle(),
                    list.get(i).getCount(),
                    list.get(i).getLabel(),
                    list.get(i).getColor(),
                    false, //isHeader
                    sectionManager,
                    sectionFirstPosition,
                    list.get(i).getSetId()));
        }
    }

    @Override
    public RVMainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rv_main, parent, false);
        }
        return new RVMainAdapter.ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(RVMainAdapter.ViewHolder holder, int position) {
        final Item item = items.get(position);
        final View itemView = holder.itemView;

        holder.bindItem(item.title, item.count, item.label, item.color, item.id);

        final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());
        // Overrides xml attrs, could use different layouts too.
        if (item.isHeader) {
            lp.headerDisplay = mHeaderDisplay;
            if (lp.isHeaderInline() || (mMarginsFixed && !lp.isHeaderOverlay())) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }

            lp.headerEndMarginIsAuto = !mMarginsFixed;
            lp.headerStartMarginIsAuto = !mMarginsFixed;
        }
        lp.setSlm(item.sectionManager == LINEAR ? LinearSLM.ID : GridSLM.ID);
        lp.setColumnWidth(mContext.getResources().getDimensionPixelSize(R.dimen.grid_column_width));
        lp.setFirstPosition(item.sectionFirstPosition);
        itemView.setLayoutParams(lp);
    }

    public boolean isItemHeader(int position) {
        return items.get(position).isHeader;
    }

    public String itemToString(int position) {
        return items.get(position).title;
    }


    @Override
    public int getItemViewType(int position) {
        return items.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setHeaderDisplay(int headerDisplay) {
        mHeaderDisplay = headerDisplay;
        notifyHeaderChanges();
    }

    public void setMarginsFixed(boolean marginsFixed) {
        mMarginsFixed = marginsFixed;
        notifyHeaderChanges();
    }

    private void notifyHeaderChanges() {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.isHeader) {
                notifyItemChanged(i);
            }
        }
    }

    public static class Item {

        public int sectionManager;

        public int sectionFirstPosition;

        public boolean isHeader;

        public String title;
        public String count;
        public String label;
        public String color;
        public String id;

        public Item(String text, String count, String label, String color,
                    boolean isHeader, int sectionManager,
                    int sectionFirstPosition, String id) {
            this.isHeader = isHeader;
            this.title = text;
            this.count = count;
            this.label = label;
            this.color = color;
            this.sectionManager = sectionManager;
            this.sectionFirstPosition = sectionFirstPosition;
            this.id = id;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView done;
        ImageView label;
        ImageView imageView;
        int viewType;
        String id = "";

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            title = (TextView) itemView.findViewById(R.id.text);
            // Setup view holder.
            // You'd want some views to be optional, e.g. for header vs. normal.
            if (viewType != VIEW_TYPE_HEADER) {
                done = (TextView) itemView.findViewById(R.id.textDone);
                label = (ImageView) itemView.findViewById(R.id.label);
                imageView = (ImageView) itemView.findViewById(R.id.card);
                imageView.setOnClickListener(this);
            }
        }

        public void bindItem(String text, String count, String labelText, String cardColor, String id) {
            if (viewType == VIEW_TYPE_HEADER)
                title.setText(text.toUpperCase());
            else
                title.setText(text);
            if (viewType != VIEW_TYPE_HEADER) {
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

        @Override
        public String toString() {
            return title.getText().toString();
        }

        @Override
        public void onClick(View view) {
            if (view.equals(imageView)) {
                Intent intent = new Intent(itemView.getContext(), FlashcardActivity.class);
                intent.putExtra("SET_ID", id);
                itemView.getContext().startActivity(intent);
            }
        }
    }
}
