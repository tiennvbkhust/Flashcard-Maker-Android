package com.piapps.flashcard.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.piapps.flashcard.R;
import com.piapps.flashcard.anim.FlipAnimation;
import com.piapps.flashcard.model.Card;
import com.piapps.flashcard.util.Utils;
import com.piapps.flashcard.view.AutoResizeTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by abduaziz on 2/19/17.
 */

public class CardFragment extends Fragment {

    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.textFront)
    AutoResizeTextView frontText;

    @BindView(R.id.textBack)
    AutoResizeTextView backText;

    @BindView(R.id.front)
    RelativeLayout front;

    @BindView(R.id.back)
    RelativeLayout back;

    @BindView(R.id.card)
    ImageView bg;

    @BindView(R.id.cardBack)
    ImageView bgBack;

    Card card;
    int mNum;

    public void setCard(Card card) {
        this.card = card;
    }

    public static CardFragment newInstance(int num) {
        CardFragment f = new CardFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    //When creating, retrieve this instance's number from its arguments
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_card, container, false);
        ButterKnife.bind(this, root);

        frontText.setText(card.getFront());
        backText.setText(card.getBack());

        if (card.getFrontImage().equals("no"))
            bg.setBackgroundColor(Color.parseColor(card.getColor()));
        else
            bg.setImageBitmap(Utils.loadImageFromStorage(card.getFrontPath(), card.getFrontImage()));
        if (card.getBackImage().equals("no"))
            bgBack.setBackgroundColor(Color.parseColor(card.getColor()));
        else
            bgBack.setImageBitmap(Utils.loadImageFromStorage(card.getBackPath(), card.getBackImage()));
        return root;
    }

    public void flipCard() {
        FlipAnimation flipAnimation = new FlipAnimation(front, back, 312);
        if (front.getVisibility() == View.GONE) {
            flipAnimation.reverse();
        }
        root.startAnimation(flipAnimation);
    }
}
