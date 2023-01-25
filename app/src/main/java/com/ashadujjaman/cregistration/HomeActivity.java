package com.ashadujjaman.cregistration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import com.ashadujjaman.cregistration.Chatting.ActivityChatMessage;
import com.ashadujjaman.cregistration.Scholar.ActivityScholarMessage;
import com.ashadujjaman.cregistration.Support.ActivitySupportMessage;
import com.ashadujjaman.cregistration.databinding.ActivityHomeBinding;


public class HomeActivity extends DrawerBaseActivity implements View.OnClickListener {
    ActivityHomeBinding activityHomeBinding;
    private CardView profile, complaint, complainView, chatMessageCardView, danger, noticeCardView;
    ImageView emergencyCallBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());
        allocateActivityTitle("Home");

        profile = findViewById(R.id.profileid);
        complaint = findViewById(R.id.complaintid);
        complainView = findViewById(R.id.complainView);
        chatMessageCardView = findViewById(R.id.chatMessageCardView);
        danger = findViewById(R.id.dangerid);
        noticeCardView = findViewById(R.id.noticeCardView);
        emergencyCallBtn = findViewById(R.id.emergencyCallBtn);

        profile.setOnClickListener(this);
        complaint.setOnClickListener(this);
        complainView.setOnClickListener(this);
        chatMessageCardView.setOnClickListener(this);
        danger.setOnClickListener(this);
        noticeCardView.setOnClickListener(this);

        emergencyCallBtn.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + "999"));
            startActivity(callIntent);
        });
    }

    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()) {
            case R.id.profileid:
                i = new Intent(this, Information.class);
                startActivity(i);
                break;
            case R.id.complaintid:
                i = new Intent(this, ComplaintSubmitActivity.class);
                startActivity(i);
                break;
            case R.id.complainView:
                i = new Intent(this, ComplaintActivity.class);
                startActivity(i);
                break;
            case R.id.chatMessageCardView:
                i = new Intent(this, ActivitySupportMessage.class);
                startActivity(i);
                break;
            case R.id.dangerid:
                i = new Intent(this, Emergency1.class);
                startActivity(i);
                break;
            case R.id.noticeCardView:
                i = new Intent(this, NoticeActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

    }
}