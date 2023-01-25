package com.ashadujjaman.cregistration.Support;

import android.os.Bundle;

import com.ashadujjaman.cregistration.DrawerBaseActivity;
import com.ashadujjaman.cregistration.databinding.ActivitySupportListBinding;


public class ActivitySupportList extends DrawerBaseActivity {
    ActivitySupportListBinding activitySupportBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySupportBinding = ActivitySupportListBinding.inflate(getLayoutInflater());
        setContentView(activitySupportBinding.getRoot());
        allocateActivityTitle("Support");
    }
}