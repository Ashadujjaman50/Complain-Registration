package com.ashadujjaman.cregistration.model;

public class ComplainModel {
    String complainId, complainTitle, complainDesc, complainImageIv, complainAction, userId;

    public ComplainModel() {
    }

    public ComplainModel(String complainId, String complainTitle, String complainDesc, String complainImageIv, String complainAction, String userId) {
        this.complainId = complainId;
        this.complainTitle = complainTitle;
        this.complainDesc = complainDesc;
        this.complainImageIv = complainImageIv;
        this.complainAction = complainAction;
        this.userId = userId;
    }

    public String getComplainId() {
        return complainId;
    }

    public void setComplainId(String complainId) {
        this.complainId = complainId;
    }

    public String getComplainTitle() {
        return complainTitle;
    }

    public void setComplainTitle(String complainTitle) {
        this.complainTitle = complainTitle;
    }

    public String getComplainDesc() {
        return complainDesc;
    }

    public void setComplainDesc(String complainDesc) {
        this.complainDesc = complainDesc;
    }

    public String getComplainImageIv() {
        return complainImageIv;
    }

    public void setComplainImageIv(String complainImageIv) {
        this.complainImageIv = complainImageIv;
    }

    public String getComplainAction() {
        return complainAction;
    }

    public void setComplainAction(String complainAction) {
        this.complainAction = complainAction;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
