package com.example.scit.diarysp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPager extends FragmentStatePagerAdapter {
    private int tabCount;

    public TabPager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                MainActivity.Diary_Write diaryWrite = new MainActivity.Diary_Write();
                return diaryWrite;
            case 2:

                MainActivity.Diary_List diaryList = new MainActivity.Diary_List();
                return diaryList;
            case 0:
                MainActivity.Diary_Setting diarySetting = new MainActivity.Diary_Setting();
                return diarySetting;
            default:
                return null;
        }
    }

    @Override    public int getCount() {
        return tabCount;
    }
}


