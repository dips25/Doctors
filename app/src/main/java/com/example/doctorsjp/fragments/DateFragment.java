package com.example.doctorsjp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.example.doctorsjp.R;


public class DateFragment extends Fragment {

    DatePicker datePicker;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_date, container, false);
        datePicker = (DatePicker) view.findViewById(R.id.choose_date_picker);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    public String returnDate() {

        int date = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        return (String.valueOf(date) + "/" + String.valueOf(month) + "/" + String.valueOf(year));


    }
}