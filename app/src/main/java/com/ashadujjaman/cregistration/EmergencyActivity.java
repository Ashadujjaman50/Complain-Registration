package com.ashadujjaman.cregistration;

import android.os.Bundle;

import com.ashadujjaman.cregistration.databinding.ActivityEmergencyBinding;


public class EmergencyActivity extends DrawerBaseActivity {
    ActivityEmergencyBinding activityEmergencyBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEmergencyBinding= ActivityEmergencyBinding.inflate(getLayoutInflater());
        setContentView(activityEmergencyBinding.getRoot());
        allocateActivityTitle("Emergency");
    }
}