package com.ashadujjaman.cregistration;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ashadujjaman.cregistration.model.ComplainModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;


public class ComplaintShowActivity extends AppCompatActivity {

    TextView userNameTv, complainTimeTv, complainTitleTv, complainDescTv;
    ImageView complainImageIv, callBtn, complainActionIv;
    String complainId, userId, complainTitle, complainDesc, complainAction, complainImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_show);

        //init views
        userNameTv = findViewById(R.id.userNameTv);
        complainTimeTv = findViewById(R.id.complainTimeTv);
        complainTitleTv = findViewById(R.id.complainTitleTv);
        complainDescTv = findViewById(R.id.complainDescTv);
        complainImageIv = findViewById(R.id.complainImageIv);
        complainActionIv = findViewById(R.id.complainActionIv);
        callBtn = findViewById(R.id.callBtn);

        //get Intent Data
        Intent intent = getIntent();
        complainId = intent.getStringExtra("complainId");
        userId = intent.getStringExtra("userId");

        userInformation();

        loadComplainInfo();

    }

    private void loadComplainInfo() {
        //convert timestamp to dd.mm/yyy hh:mm aa
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(Long.parseLong(complainId));
        CharSequence time = DateFormat.format("dd MMM hh:mm aa", calendar1);
        complainTimeTv.setText(time);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Complain");
        Query query = ref.orderByChild("complainId").equalTo(complainId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    complainTitle = ""+ds.child("complainTitle").getValue();
                    complainDesc = ""+ds.child("complainDesc").getValue();
                    complainAction = ""+ds.child("complainAction").getValue();
                    complainImage = ""+ds.child("complainImageIv").getValue();

                    complainTitleTv.setText(complainTitle);
                    complainDescTv.setText(complainDesc);

                    if (complainImage.equals("")){
                        complainImageIv.setVisibility(View.GONE);
                    }
                    else {
                        complainImageIv.setVisibility(View.VISIBLE);
                    }

                    if (complainAction.equals("pending")){
                        complainActionIv.setImageResource(R.drawable.ic_pending);
                    }
                    else if (complainAction.equals("process")){
                        complainActionIv.setImageResource(R.drawable.ic_process);
                    }
                    else if (complainAction.equals("done")){
                        complainActionIv.setImageResource(R.drawable.ic_done);
                    }

                    try {
                        Picasso.get().load(complainImage).placeholder(R.drawable.ic_image_loading).into(complainImageIv);
                    }
                    catch (Exception e){
                        complainImageIv.setImageResource(R.drawable.ic_image_loading);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInformation() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userName = ""+snapshot.child("name").getValue();
                        String mobile = ""+snapshot.child("phone").getValue();

                        userNameTv.setText(userName);
                        callBtn.setOnClickListener(v -> {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                            callIntent.setData(Uri.parse("tel:" + mobile));
                            startActivity(callIntent);
                            //Toast.makeText(ComplaintShowActivity.this, ""+mobile, Toast.LENGTH_SHORT).show();
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}