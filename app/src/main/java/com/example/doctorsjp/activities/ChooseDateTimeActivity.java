package com.example.doctorsjp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.doctorsjp.R;
import com.example.doctorsjp.adapters.ViewPagerAdapter;
import com.example.doctorsjp.fragments.DateFragment;
import com.example.doctorsjp.fragments.TimeFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;

public class ChooseDateTimeActivity extends AppCompatActivity {

    TabLayout choose_date_time_tablayout;
    ViewPager choose_date_time_viewpager;
    ViewPagerAdapter viewPagerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_date_time);

        choose_date_time_tablayout = (TabLayout) findViewById(R.id.choose_date_time_tablayout);
        choose_date_time_viewpager = (ViewPager) findViewById(R.id.choose_date_time_viewpager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new DateFragment() , "Date");
        viewPagerAdapter.addFragments(new TimeFragment() , "Time");

        choose_date_time_viewpager.setAdapter(viewPagerAdapter);

        choose_date_time_tablayout.setupWithViewPager(choose_date_time_viewpager);


    }
}