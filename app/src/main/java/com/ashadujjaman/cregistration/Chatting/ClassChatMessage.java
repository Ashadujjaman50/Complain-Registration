package com.ashadujjaman.cregistration.Chatting;

public class ClassChatMessage {
    String message, sentTime, userId, adminId, status;

    public ClassChatMessage(){}
    public ClassChatMessage(String message, String sentTime, String userId, String adminId, String status) {
        this.message = message;
        this.sentTime = sentTime;
        this.userId = userId;
        this.adminId = adminId;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
