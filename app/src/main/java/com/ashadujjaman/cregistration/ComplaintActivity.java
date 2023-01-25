package com.ashadujjaman.cregistration;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashadujjaman.cregistration.adapter.AdapterComplain;
import com.ashadujjaman.cregistration.model.ComplainModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ComplaintActivity extends DrawerBaseActivity {

    RecyclerView complainRV;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    AdapterComplain adapterComplain;
    ArrayList<ComplainModel> complainList;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        complainRV = findViewById(R.id.complainRV);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait....");
        progressDialog.setMessage("Loading your complain....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        loadComplain();

    }

    private void loadComplain() {
        complainList = new ArrayList<>();
        //setup adapter
        adapterComplain = new AdapterComplain(ComplaintActivity.this, complainList);

        //get data
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Complain");
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //before getting reset list
                complainList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ComplainModel complainModel = ds.getValue(ComplainModel.class);
                    String userId = ""+ds.child("userId").getValue();
                    if (userId.equals(firebaseUser.getUid())){
                        complainList.add(complainModel);
                    }
                    progressDialog.dismiss();
                }
                complainRV.setAdapter(adapterComplain);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}