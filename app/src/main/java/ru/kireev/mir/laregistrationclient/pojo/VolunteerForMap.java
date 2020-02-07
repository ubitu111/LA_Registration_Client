package ru.kireev.mir.laregistrationclient.pojo;

import org.json.JSONObject;

public class VolunteerForMap {
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private JSONObject geo;
    private String phoneNumber;
    private String address;
    private boolean withCar;
    private boolean severskPass;

    public VolunteerForMap(String id, String firstName, String middleName, String lastName, JSONObject geo, String phoneNumber, String address, boolean withCar, boolean severskPass) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.geo = geo;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.withCar = withCar;
        this.severskPass = severskPass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public JSONObject getGeo() {
        return geo;
    }

    public void setGeo(JSONObject geo) {
        this.geo = geo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isWithCar() {
        return withCar;
    }

    public void setWithCar(boolean withCar) {
        this.withCar = withCar;
    }

    public boolean isSeverskPass() {
        return severskPass;
    }

    public void setSeverskPass(boolean severskPass) {
        this.severskPass = severskPass;
    }
}

