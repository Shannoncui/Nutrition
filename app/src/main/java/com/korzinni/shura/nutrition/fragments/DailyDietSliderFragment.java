package com.korzinni.shura.nutrition.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.korzinni.shura.nutrition.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DailyDietSliderFragment extends Fragment {
    private static final int MIDDLE = 1;
    public static final String DATE = "day";
    ViewPager mPager;
    Calendar c;
    DailyDietFragment[] fragments=new DailyDietFragment[3];
    int mSelectedPageIndex=1;
    long currentDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c=Calendar.getInstance();
        if(savedInstanceState!=null){
            currentDate=savedInstanceState.getLong(DATE);
        }else{
            currentDate=cutTimeInDate(c);
        }
        fragments[0]=DailyDietFragment.newInstance(minusDays(currentDate, 1));
        fragments[1]=DailyDietFragment.newInstance(currentDate);
        fragments[2]=DailyDietFragment.newInstance(plusDays(currentDate,1));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DATE,currentDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.daily_diet_slider, null);
        mPager=(ViewPager)view.findViewById(R.id.pager);
        DailyDietSlidePagerAdapter adapter=new DailyDietSlidePagerAdapter(getChildFragmentManager(),fragments);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                mSelectedPageIndex=i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == ViewPager.SCROLL_STATE_IDLE) {
                    if (mSelectedPageIndex < MIDDLE) {
                        currentDate=minusDays(currentDate,1);
                    } else if (mSelectedPageIndex > MIDDLE) {
                        currentDate=plusDays(currentDate,1);
                    }
                    SimpleDateFormat f=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    Log.d("tag","currentDate: "+ f.format(currentDate));
                    fragments[0].setDate(minusDays(currentDate, 1),getActivity());
                    fragments[1].setDate(currentDate,getActivity());
                    fragments[2].setDate(plusDays(currentDate,1),getActivity());
                    mPager.setCurrentItem(1, false);


                }
            }
        });

        mPager.setAdapter(adapter);
        mPager.setCurrentItem(1, false);
        return view;
    }
    private class DailyDietSlidePagerAdapter extends FragmentStatePagerAdapter {
        DailyDietFragment[] fragments;
        public DailyDietSlidePagerAdapter(FragmentManager fm,DailyDietFragment[] fragments) {
            super(fm);
            this.fragments=fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

    }
    private long plusDays(long date,int countDays){
        return date+(countDays*86400000);
    }
    private long minusDays(long date,int countDays){
        return date-(countDays*86400000);
    }

    private long cutTimeInDate(Calendar c){
        long date=c.getTimeInMillis();
        long onlyTime=date%86400000;
        return date-onlyTime;
    }
    public DailyDietFragment getCurrentDailyDietFagment(){
        return fragments[1];
    }
}
