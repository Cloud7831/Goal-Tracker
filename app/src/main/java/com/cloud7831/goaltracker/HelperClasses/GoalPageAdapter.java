package com.cloud7831.goaltracker.HelperClasses;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.cloud7831.goaltracker.Activities.GoalOverviewFragment;
import com.cloud7831.goaltracker.Activities.GoalProgressFragment;

public class GoalPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public GoalPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    // tab titles
    private String[] tabTitles = new String[]{"Overview", "Settings", "Progress"};

    // overriding getPageTitle()
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new GoalOverviewFragment();
            case 1:
                return new GoalOverviewFragment();
            case 2:
                return new GoalProgressFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
