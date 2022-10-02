package ru.kireev.mir.volunteerlizaalert.pojo;


public class VolunteerForMap {
    private String _id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String geo;
    private String phoneNumber;
    private String address;
    private String linkToVK;
    private String car;
    private boolean withCar;
    private boolean severskPass;

    public VolunteerForMap(String _id, String firstName, String middleName, String lastName,
                           String geo, String phoneNumber, String address, String linkToVK,
                           String car, boolean withCar, boolean severskPass) {
        this._id = _id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.geo = geo;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.linkToVK = linkToVK;
        this.car = car;
        this.withCar = withCar;
        this.severskPass = severskPass;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
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

    public String getLinkToVK() {
        return linkToVK;
    }

    public void setLinkToVK(String linkToVK) {
        this.linkToVK = linkToVK;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
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

