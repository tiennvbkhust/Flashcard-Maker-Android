package com.piapps.flashcard.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.piapps.flashcard.R;
import com.piapps.flashcard.adapter.RVMainAdapter;
import com.piapps.flashcard.db.FlashcardDb;
import com.piapps.flashcard.model.Flashcard;
import com.piapps.flashcard.util.Utils;
import com.piapps.flashcard.view.RecyclerListView;
import com.tonicartos.superslim.LayoutManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * Created by abduaziz on 2/14/17.
 */

public class LabelFragment extends Fragment {

    private static final String TAG = "FLASH";
    private static final String KEY_HEADER_POSITIONING = "key_header_mode";
    private static final String KEY_MARGINS_FIXED = "key_margins_fixed";
    public static LabelFragment instance;
    @BindView(R.id.recycler_view)
    RecyclerListView rv;
    FlashcardDb db;
    List<Flashcard> flashcards;
    private RVMainAdapter adapter;
    private int mHeaderDisplay;
    private boolean mAreMarginsFixed;

    public static LabelFragment newInstance(String label) {
        LabelFragment labelFragment = new LabelFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", label);
        labelFragment.setArguments(bundle);
        return labelFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_important, container, false);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mHeaderDisplay = savedInstanceState
                    .getInt(KEY_HEADER_POSITIONING,
                            getResources().getInteger(R.integer.default_header_display));
            mAreMarginsFixed = savedInstanceState
                    .getBoolean(KEY_MARGINS_FIXED,
                            getResources().getBoolean(R.bool.default_margins_fixed));
        } else {
            mHeaderDisplay = getResources().getInteger(R.integer.default_header_display);
            mAreMarginsFixed = getResources().getBoolean(R.bool.default_margins_fixed);
        }

        db = FlashcardDb.getInstance(getActivity().getApplicationContext());
        flashcards = db.getAllFlashcards();
        Log.d(TAG, "onViewCreated: flashcards.size() = " + flashcards.size());
        rv.setLayoutManager(new LayoutManager(getActivity()));
        //OverScrollDecoratorHelper.setUpOverScroll(rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        adapter = new RVMainAdapter(getActivity(), mHeaderDisplay, Utils.getFlashcardsByLabel(flashcards, getString(R.string.menu_todo)));
        adapter.setMarginsFixed(mAreMarginsFixed);
        adapter.setHeaderDisplay(mHeaderDisplay);
        rv.setAdapter(new ScaleInAnimationAdapter(new AlphaInAnimationAdapter(adapter)));

        String label = getArguments().getString("title", "aksjdhiqwhe27813487132647813");

        LinearLayout noSets = (LinearLayout) view.findViewById(R.id.no_sets);
        if (Utils.getFlashcardsByLabel(flashcards, label).isEmpty())
            noSets.setVisibility(View.VISIBLE);
        else
            noSets.setVisibility(View.GONE);
    }
}
