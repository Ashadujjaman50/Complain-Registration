package com.ashadujjaman.cregistration.model;

public class ClassUserInfo {
    String name, phone, fatherName, emergency1, emergency2, address, userId;

    public ClassUserInfo(){}

    public ClassUserInfo(String name, String phone, String fatherName, String emergency1, String emergency2, String address, String userId) {
        this.name = name;
        this.phone = phone;
        this.fatherName = fatherName;
        this.emergency1 = emergency1;
        this.emergency2 = emergency2;
        this.address = address;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getEmergency1() {
        return emergency1;
    }

    public void setEmergency1(String emergency1) {
        this.emergency1 = emergency1;
    }

    public String getEmergency2() {
        return emergency2;
    }

    public void setEmergency2(String emergency2) {
        this.emergency2 = emergency2;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
