package com.example.doctorsjp.adapters;



import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doctorsjp.Models.Doctors;
import com.example.doctorsjp.R;
import com.example.doctorsjp.activities.DoctMapsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.ViewHolder> {

    private static final String TAG = DoctorsAdapter.class.getSimpleName();

    Context context;
    ArrayList<Doctors> doctorsArrayList;
    Doctors doc;

    public DoctorsAdapter(Context context , ArrayList<Doctors> doctorsArrayList) {

        this.context = context;
        this.doctorsArrayList = doctorsArrayList;


    }
    @NonNull
    @Override
    public DoctorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_doctors , parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorsAdapter.ViewHolder holder, int position) {

        final Doctors doctors = doctorsArrayList.get(position);

        holder.doct_date.setText(doctors.getDate());
        holder.doct_name.setText(doctors.getName());
        holder.doct_address.setText(doctors.getFulladdress());

        holder.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.itemView.setVisibility(View.GONE);

                FirebaseFirestore.getInstance().collection("Patients")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("Doctors")
                        .document(doctors.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailureDelDoc: "+  e.getMessage());

                    }
                });

                FirebaseFirestore.getInstance().collection("Doctors")
                        .document(doctors.getId())
                        .collection("Patients")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailureDelPat: " + e.getMessage());

                    }
                });

            }
        });

        if (doctors.getProfilepic() != null) {

            Glide.with(context)
                    .load(doctors.getProfilepic())
                    .placeholder(R.drawable.ic_user)
                    .into(holder.item_profileimage);
        }


            holder.pending.setVisibility(View.GONE);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context , DoctMapsActivity.class);
                intent.putExtra("id" , doctors.getId());
                intent.putExtra("latitude" , doctors.getLatitude());
                intent.putExtra("longitude" , doctors.getLongitude());
                intent.putExtra("fulladdress" , doctors.getFulladdress());
                intent.putExtra("name" , doctors.getName());
                intent.putExtra("date" , doctors.getDate());
                intent.putExtra("profileimage" , doctors.getProfilepic());
                intent.putExtra("time" , doctors.getTime());
                intent.putExtra("convertedtime" , doctors.getConvertedtime());
                intent.putExtra("status" , doctors.getStatus());
                intent.putExtra("timestamp" , doctors.getTimestamp());
                context.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return doctorsArrayList.size();
    }



    public  class ViewHolder extends RecyclerView.ViewHolder {

        TextView doct_name , doct_address , doct_date , decline , pending;
        CircleImageView item_profileimage;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            doct_date = (TextView) itemView.findViewById(R.id.doct_date);
            doct_name = (TextView) itemView.findViewById(R.id.doct_name);
            doct_address = (TextView) itemView.findViewById(R.id.doct_full_address);
            decline = (TextView) itemView.findViewById(R.id.doct_decline);
            item_profileimage = (CircleImageView) itemView.findViewById(R.id.item_profileimage);
            pending = (TextView) itemView.findViewById(R.id.doct_pending);

        }
    }
}
