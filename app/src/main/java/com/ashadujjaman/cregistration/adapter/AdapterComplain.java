package com.ashadujjaman.cregistration.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashadujjaman.cregistration.ComplaintShowActivity;
import com.ashadujjaman.cregistration.R;
import com.ashadujjaman.cregistration.model.ComplainModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterComplain extends RecyclerView.Adapter<AdapterComplain.HolderViewComplain> {

    Context context;
    ArrayList<ComplainModel> complainArrayList;

    public AdapterComplain(Context context, ArrayList<ComplainModel> complainArrayList) {
        this.context = context;
        this.complainArrayList = complainArrayList;
    }

    @NonNull
    @Override
    public HolderViewComplain onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate Layout
        View view = LayoutInflater.from(context).inflate(R.layout.card_complain, parent, false );
        return new HolderViewComplain(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderViewComplain holder, int position) {
        //get data
        ComplainModel complainModel = complainArrayList.get(position);
        String complainId = complainModel.getComplainId();
        String complainTitle = complainModel.getComplainTitle();
        String complainDesc = complainModel.getComplainDesc();
        String complainImageIv = complainModel.getComplainImageIv();
        String complainAction = complainModel.getComplainAction();
        String userId = complainModel.getUserId();

        //convert timestamp to dd.mm/yyy hh:mm aa
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(Long.parseLong(complainId));
        CharSequence time = DateFormat.format("dd MMM hh:mm aa", calendar1);

        userInformation(holder, userId);

        //set data
        holder.complainTimeTv.setText(time);
        holder.complainTitleTv.setText(complainTitle);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ComplaintShowActivity.class);
                intent.putExtra("complainId", complainId);
                intent.putExtra("userId", userId);
                context.startActivity(intent);
            }
        });
    }

    private void userInformation(HolderViewComplain holder, String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = ""+snapshot.child("name").getValue();

                        holder.userNameTv.setText(userName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return complainArrayList.size();
    }

    static class HolderViewComplain extends ChatAdapter.ViewHolder{
        TextView userNameTv, complainTitleTv, complainTimeTv;

        public HolderViewComplain(View itemView) {
            super(itemView);

            //init views
            userNameTv = itemView.findViewById(R.id.userNameTv);
            complainTitleTv = itemView.findViewById(R.id.complainTitleTv);
            complainTimeTv = itemView.findViewById(R.id.complainTimeTv);
        }
    }
}
