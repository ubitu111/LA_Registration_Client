package ru.kireev.mir.volunteerlizaalert.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "volunteer_for_qr")
public class VolunteerForQR {
    @PrimaryKey()
    private int id;
    private String fullName;
    private String callSign;
    private String forumNickName;
    private String region;
    private String phoneNumber;
    private String car;

    public VolunteerForQR(
            int id,
            String fullName,
            String callSign,
            String forumNickName,
            String region,
            String phoneNumber,
            String car
    ) {
        this.id = id;
        this.fullName = fullName;
        this.callSign = callSign;
        this.forumNickName = forumNickName;
        this.region = region;
        this.phoneNumber = phoneNumber;
        this.car = car;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getForumNickName() {
        return forumNickName;
    }

    public void setForumNickName(String forumNickName) {
        this.forumNickName = forumNickName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }
}
