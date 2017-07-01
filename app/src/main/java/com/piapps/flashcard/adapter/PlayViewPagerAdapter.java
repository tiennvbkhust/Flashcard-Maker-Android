package com.piapps.flashcard.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.piapps.flashcard.fragment.CardFragment;

import java.util.List;

/**
 * Created by abduaziz on 2/19/17.
 */

public class PlayViewPagerAdapter extends FragmentStatePagerAdapter{

    int count = 0;

    List<CardFragment> list;

    public PlayViewPagerAdapter(FragmentManager fm, List<CardFragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
