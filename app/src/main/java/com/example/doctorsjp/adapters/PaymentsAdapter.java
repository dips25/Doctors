package com.example.doctorsjp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.doctorsjp.Models.Payments;
import com.example.doctorsjp.R;

import java.util.ArrayList;
import java.util.List;

public class PaymentsAdapter extends ArrayAdapter<Payments> {

    Context context;
    List<Payments> paymentsArrayList;

    public PaymentsAdapter(@NonNull Context context, int resource , List<Payments> paymentsArrayList) {
        super(context, resource , paymentsArrayList);
        this.context = context;
        this.paymentsArrayList = paymentsArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.single_item_completed, null);

            final Payments payments = paymentsArrayList.get(position);

            if (payments != null) {

                TextView name = (TextView) v.findViewById(R.id.payment_name);
                TextView amount = (TextView) v.findViewById(R.id.payment_amount);
                TextView date = (TextView) v.findViewById(R.id.payment_date);

                name.setText(payments.getName());
                date.setText(payments.getDate());
                amount.setText("Rs. " + payments.getAmount());
            }



        }

        return v;

    }
}
