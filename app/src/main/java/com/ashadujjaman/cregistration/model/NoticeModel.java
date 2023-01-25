package com.ashadujjaman.cregistration.model;

public class NoticeModel {

    String noticeId, noticeTitle, noticeDesc, noticePublish;

    public NoticeModel() {
    }

    public NoticeModel(String noticeId, String noticeTitle, String noticeDesc, String noticePublish) {
        this.noticeId = noticeId;
        this.noticeTitle = noticeTitle;
        this.noticeDesc = noticeDesc;
        this.noticePublish = noticePublish;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeDesc() {
        return noticeDesc;
    }

    public void setNoticeDesc(String noticeDesc) {
        this.noticeDesc = noticeDesc;
    }

    public String getNoticePublish() {
        return noticePublish;
    }

    public void setNoticePublish(String noticePublish) {
        this.noticePublish = noticePublish;
    }
}
