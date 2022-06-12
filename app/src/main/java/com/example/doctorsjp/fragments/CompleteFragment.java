package com.example.doctorsjp.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.doctorsjp.Models.Payments;
import com.example.doctorsjp.R;
import com.example.doctorsjp.adapters.PaymentsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CompleteFragment extends Fragment {

    ListView payments_list;
    BottomNavigationView bottomNavigationView;
    ProgressBar search_doc_progress;
    Button search_docs;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_complete, container, false);
        payments_list = (ListView) view.findViewById(R.id.payment_list);

        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);


        getAllPayments();
        return view;

    }

    public void getAllPayments() {

        FirebaseFirestore.getInstance().collection("Patients")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Payments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            return;
                        }

                        if (!value.isEmpty()) {

                            List<Payments> paymentsList = value.toObjects(Payments.class);

                            PaymentsAdapter paymentsAdapter = new PaymentsAdapter(getContext() , R.layout.single_item_completed ,  paymentsList);
                            payments_list.setAdapter(paymentsAdapter);
                            paymentsAdapter.notifyDataSetChanged();


                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();


    }
}