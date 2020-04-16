package ru.kireev.mir.volunteerlizaalert.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "volunteer_for_qr")
public class VolunteerForQR {
    @PrimaryKey()
    private int id;
    private String name;
    private String surname;
    private String callSign;
    private String phoneNumber;
    private String carMark;
    private String carModel;
    private String carRegistrationNumber;
    private String carColor;
    private int haveACar;

    public VolunteerForQR(int id, String name, String surname, String callSign, String phoneNumber, String carMark, String carModel, String carRegistrationNumber, String carColor, int haveACar) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.callSign = callSign;
        this.phoneNumber = phoneNumber;
        this.carMark = carMark;
        this.carModel = carModel;
        this.carRegistrationNumber = carRegistrationNumber;
        this.carColor = carColor;
        this.haveACar = haveACar;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCarMark() {
        return carMark;
    }

    public void setCarMark(String carMark) {
        this.carMark = carMark;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarRegistrationNumber() {
        return carRegistrationNumber;
    }

    public void setCarRegistrationNumber(String carRegistrationNumber) {
        this.carRegistrationNumber = carRegistrationNumber;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public int getHaveACar() {
        return haveACar;
    }

    public void setHaveACar(int haveACar) {
        this.haveACar = haveACar;
    }
}
