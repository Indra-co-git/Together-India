package com.indra.togetherindia;

public class Entry {

    private String name;
    private int age;
    private String mobileNo;
    private String stage;
    private String requirement;
    private String state;
    private String city;
    private String date;

    public Entry(String name, int age, String mobileNo, String stage, String requirement, String state, String city, String date) {
        this.name = name;
        this.age = age;
        this.mobileNo = mobileNo;
        this.stage = stage;
        this.requirement = requirement;
        this.state = state;
        this.city = city;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
