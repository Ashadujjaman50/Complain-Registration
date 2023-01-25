package com.ashadujjaman.cregistration;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.ashadujjaman.cregistration.model.ClassUserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Emergency1 extends AppCompatActivity implements View.OnClickListener {
    private CardView location, contact, police, hospital;
    String emergency1, emergency2, userName, userId;
    DocumentReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency1);

        Toolbar toolbar = findViewById(R.id.toolbarid);
        setSupportActionBar(toolbar);

        location = findViewById(R.id.locationid);
        police = findViewById(R.id.policeid);
        hospital =  findViewById(R.id.hospitalid);

        userId = FirebaseAuth.getInstance().getUid();
        userRef = FirebaseFirestore.getInstance().collection("Users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            ClassUserInfo info = documentSnapshot.toObject(ClassUserInfo.class);
            assert info != null;
            userName = info.getName();
            emergency1 = info.getEmergency1();
            //emergency2 = info.getEmergency2();
        });

        location.setOnClickListener(this);
        police.setOnClickListener(this);
        hospital.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()) {
            case R.id.locationid:
                i = new Intent(this, CurrentLocation.class);
                i.putExtra("UserName", userName);
                i.putExtra("Emergency1", emergency1);
                i.putExtra("Emergency2", emergency2);
                startActivity(i);
                break;
            case R.id.policeid:
                i = new Intent(this, NearbyPolice.class);
                startActivity(i);
                break;
            case R.id.hospitalid:
                i = new Intent(this, NearbyHospital.class);
                startActivity(i);
                break;
            default:
                break;
        }


    }
}


