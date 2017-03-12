package com.codepath.apps.restclienttemplate.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.codepath.apps.restclienttemplate.MentionFragment;
import com.codepath.apps.restclienttemplate.TimelineFragment;

/**
 * Created by ThienPG on 3/12/2017.
 */

public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Timeline", "Mentions" };
    private Context context;

    public TimelineFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("position", String.valueOf(position));
        switch (position){
            case 0:
                return TimelineFragment.newInstance(position + 1);
            case 1:
                return MentionFragment.newInstance(position + 1);
            default:
                return TimelineFragment.newInstance(position + 1);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
