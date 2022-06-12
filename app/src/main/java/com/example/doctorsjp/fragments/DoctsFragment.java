package com.example.doctorsjp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.doctorsjp.Models.Doctors;
import com.example.doctorsjp.R;
import com.example.doctorsjp.adapters.DoctorsAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class DoctsFragment extends Fragment {

    RecyclerView doctsrecycler;
    BottomNavigationView bottomNavigationView;
    ProgressBar search_doc_progress;
    Button search_docs;
    SwipeRefreshLayout swipeRefreshLayout;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_docts, container, false);

        bottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getAllDocts();

            }
        });



        doctsrecycler = (RecyclerView) view.findViewById(R.id.docts_recycler);
        doctsrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        doctsrecycler.setHasFixedSize(true);

        getAllDocts();


        return view;
    }

    public void getAllDocts() {

        FirebaseFirestore.getInstance().collection("Patients")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("Doctors")
                .whereEqualTo("status" , "accepted")
                .orderBy("timestamp" , Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            return;
                        }

                        if (!value.isEmpty()) {

                            ArrayList<Doctors> doctorsArrayList = (ArrayList<Doctors>) value.toObjects(Doctors.class);
                            DoctorsAdapter doctorsAdapter = new DoctorsAdapter(getContext() , doctorsArrayList);
                            doctsrecycler.setAdapter(doctorsAdapter);
                            doctorsAdapter.notifyDataSetChanged();

                        }




                    }
                });

    }

    @Override
    public void onStart() {
        super.onStart();

    }
}