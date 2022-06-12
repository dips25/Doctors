package com.example.doctorsjp.fragments;

import static com.example.doctorsjp.fragments.MapsFragment.fulladdress;
import static com.example.doctorsjp.fragments.MapsFragment.latitude;
import static com.example.doctorsjp.fragments.MapsFragment.longitude;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.example.doctorsjp.GetValues;
import com.example.doctorsjp.R;
import com.example.doctorsjp.adapters.ViewPagerAdapter;
import com.example.doctorsjp.services.SearchDocService;
import com.google.android.material.tabs.TabLayout;

public class ViewPagerDialogFragment extends DialogFragment {

    TabLayout choose_date_time_tablayout;
    ViewPager choose_date_time_viewpager;
    ViewPagerAdapter viewPagerAdapter;
    ImageView next , previous;
    DateFragment dateFragment;
    TimeFragment timeFragment;
    String date , time;
    int convertedtime;
    AlertDialog alertDialog;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.activity_choose_date_time , null);
        builder.setView(v);
        builder.setCancelable(false);

        choose_date_time_tablayout = (TabLayout) v.findViewById(R.id.choose_date_time_tablayout);
        choose_date_time_viewpager = (ViewPager) v.findViewById(R.id.choose_date_time_viewpager);

        dateFragment = new DateFragment();
        timeFragment = new TimeFragment();

        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragments(dateFragment , "Date");
        viewPagerAdapter.addFragments(timeFragment , "Time");

        choose_date_time_viewpager.setAdapter(viewPagerAdapter);

        choose_date_time_tablayout.setupWithViewPager(choose_date_time_viewpager);

        next = (ImageView) v.findViewById(R.id.next);
        previous = (ImageView) v.findViewById(R.id.previous);

        if (choose_date_time_viewpager.getCurrentItem() == 0) {

            previous.setVisibility(View.GONE);

        }

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (choose_date_time_viewpager.getCurrentItem() == 1) {

                    choose_date_time_viewpager.setCurrentItem(0);
                    previous.setVisibility(View.GONE);

                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (choose_date_time_viewpager.getCurrentItem() == 0) {

                    date = dateFragment.returnDate();
                    choose_date_time_viewpager.setCurrentItem(1);
                    previous.setVisibility(View.VISIBLE);

                } else if (choose_date_time_viewpager.getCurrentItem() == 1){



                    time = timeFragment.returnTime();
                    convertedtime = timeFragment.returnConvertedTime();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setMessage("Your date is " + date + " and time is " + time + ". Do you want to proceed?");
                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {



                             Intent intent = new Intent(getContext() , SearchDocService.class);
                             intent.putExtra("latitude" , latitude);
                             intent.putExtra("longitude" , longitude);
                             intent.putExtra("fulladdress" , fulladdress);
                             intent.putExtra("date" , date);
                             intent.putExtra("time" , time);
                             intent.putExtra("convertedtime" , convertedtime);
                             getContext().startService(intent);
                             dialog.dismiss();
                             alertDialog.cancel();



                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();



                        }
                    });

                    builder1.show();

                }
            }
        });


        alertDialog = builder.create();
        return alertDialog;


    }

    public interface DateTime {

        void getDate();
        void getTime();
    }


}
