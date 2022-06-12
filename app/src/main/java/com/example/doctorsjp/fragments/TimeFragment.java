package com.example.doctorsjp.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.example.doctorsjp.R;

public class TimeFragment extends Fragment {

    TimePicker timePicker;
    int hrs , mins;



    public TimeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_time, container, false);
        timePicker = (TimePicker) view.findViewById(R.id.choose_time_picker);
        timePicker.setIs24HourView(true);

        return view;
    }

    public String returnTime() {

        hrs = timePicker.getCurrentHour();
        mins = timePicker.getCurrentMinute();

        return (String.valueOf(hrs) + ":" + String.valueOf(mins));
    }

    public int returnConvertedTime() {

        return ((hrs*60) + mins);
    }
}