package com.piapps.flashcard.fragment;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.piapps.flashcard.R;
import com.piapps.flashcard.adapter.RVTrashAdapter;
import com.piapps.flashcard.db.CardDb;
import com.piapps.flashcard.db.FlashcardDb;
import com.piapps.flashcard.db.TrashFlashcardDb;
import com.piapps.flashcard.model.Card;
import com.piapps.flashcard.model.Flashcard;
import com.piapps.flashcard.util.Utils;
import com.piapps.flashcard.view.RecyclerListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * Created by abduaziz on 8/3/17.
 */

public class TrashFragment extends Fragment {
    private static final String TAG = "FLASH";
    private static final String KEY_HEADER_POSITIONING = "key_header_mode";
    private static final String KEY_MARGINS_FIXED = "key_margins_fixed";
    @BindView(R.id.recycler_view)
    RecyclerListView rv;
    TrashFlashcardDb trashFlashcardDb;
    FlashcardDb flashcardDb;
    CardDb cardDb;
    List<Flashcard> flashcards;
    private RVTrashAdapter adapter;
    private int mHeaderDisplay;
    private boolean mAreMarginsFixed;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trash, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trashFlashcardDb = TrashFlashcardDb.getInstance(getActivity().getApplicationContext());
        flashcardDb = FlashcardDb.getInstance(getActivity().getApplicationContext());
        cardDb = CardDb.getInstance(getActivity().getApplicationContext());

        flashcards = trashFlashcardDb.getAllFlashcards();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RVTrashAdapter(Utils.getFlashcardsInAlphabeticalOrder(flashcards));
        rv.setAdapter(new ScaleInAnimationAdapter(new AlphaInAnimationAdapter(adapter)));
        LinearLayout noSets = (LinearLayout) view.findViewById(R.id.no_sets);
        if (Utils.getFlashcardsInAlphabeticalOrder(flashcards).isEmpty())
            noSets.setVisibility(View.VISIBLE);
        else
            noSets.setVisibility(View.GONE);

        rv.addOnItemClickListener(getContext(), new RecyclerListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.what_to_with_this_flashcard)
                        .setPositiveButton(R.string.put_back, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                flashcardDb.addFlashcard(flashcards.get(position));
                                trashFlashcardDb.deleteFlashcard(flashcards.get(position));
                                flashcards.remove(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, flashcards.size());
                            }
                        })
                        .setNegativeButton(R.string.delete_forever, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                                //first delete all cards
                                final String setId = flashcards.get(position).getSetId();
                                AsyncTask.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        List<Card> cardList = cardDb.getSetCards(setId);
                                        for (int c = 0; c < cardList.size(); c++) {
                                            cardDb.deleteCard(cardList.get(c));
                                        }
                                    }
                                });
                                ///then delete set itself
                                trashFlashcardDb.deleteFlashcard(flashcards.get(position));
                                flashcards.remove(position);
                                adapter.notifyItemRemoved(position);
                                adapter.notifyItemRangeChanged(position, flashcards.size());
                            }
                        });
                builder.show();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        });
        if (flashcards.isEmpty())
            noSets.setVisibility(View.VISIBLE);
        else
            noSets.setVisibility(View.GONE);
    }
}
