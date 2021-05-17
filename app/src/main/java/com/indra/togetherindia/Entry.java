package com.indra.togetherindia;

public class Entry {

    private String name;
    private String age;
    private String mobileNo;
    private String severity;
    private String requirement;
    private String state;
    private String city;
    private String dateTime;
    private  String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    private String android_id;
    private String pincode;

    public Entry()
    {

    }
    public Entry(String name, String age, String mobileNo, String severity, String requirement, String state, String city, String date) {
        this.name = name;
        this.age = age;
        this.mobileNo = mobileNo;
        this.severity = severity;
        this.requirement = requirement;
        this.state = state;
        this.city = city;
        this.dateTime = dateTime;
    }

    public Entry(String name, String age, String mobileNo, String severity, String requirement, String state, String city, String date,String email,String pincode,String android_id) {
        this.name = name;
        this.age = age;
        this.mobileNo = mobileNo;
        this.severity = severity;
        this.requirement = requirement;
        this.state = state;
        this.city = city;
        this.dateTime = dateTime;
        this.email=email;
        this.pincode=pincode;
        this.android_id=android_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
