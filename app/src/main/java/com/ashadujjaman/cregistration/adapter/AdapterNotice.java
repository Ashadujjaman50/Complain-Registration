package com.ashadujjaman.cregistration.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashadujjaman.cregistration.R;
import com.ashadujjaman.cregistration.model.NoticeModel;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterNotice extends RecyclerView.Adapter<AdapterNotice.HolderViewNotice> {

    Context context;
    ArrayList<NoticeModel> noticeList;

    public AdapterNotice(Context context, ArrayList<NoticeModel> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    @NonNull
    @Override
    public HolderViewNotice onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate
        View view = LayoutInflater.from(context).inflate(R.layout.card_notice, parent, false);
        return new HolderViewNotice(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderViewNotice holder, int position) {
        //get data
        NoticeModel noticeModel = noticeList.get(position);
        String noticeId = noticeModel.getNoticeId();
        String noticeTitle = noticeModel.getNoticeTitle();
        String noticeDesc = noticeModel.getNoticeDesc();
        String noticePublish = noticeModel.getNoticePublish();

        //convert timestamp to dd.mm/yyy hh:mm aa
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(Long.parseLong(noticeId));
        CharSequence time = DateFormat.format("dd MMM hh:mm aa", calendar1);

        //set data
        holder.noticeTitleTv.setText(noticeTitle);
        holder.noticeDescTv.setText(noticeDesc);
        holder.noticeTimeTv.setText(time);
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    static class HolderViewNotice extends RecyclerView.ViewHolder{

        TextView noticeTitleTv, noticeDescTv, noticeTimeTv;

        public HolderViewNotice(@NonNull View itemView) {
            super(itemView);
            //init view
            noticeTitleTv = itemView.findViewById(R.id.noticeTitleTv);
            noticeDescTv = itemView.findViewById(R.id.noticeDescTv);
            noticeTimeTv = itemView.findViewById(R.id.noticeTimeTv);
        }
    }
}
