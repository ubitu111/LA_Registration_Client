package ru.kireev.mir.laregistrationclient.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "volunteer_for_profile")
public class VolunteerForProfile {
    @PrimaryKey
    private int id;
    private String volunteer_id;
    private String surname;
    private String name;
    private String patronymic;
    private String dateOfBirth;
    private String phoneNumber;
    private String forumNickname;
    private String linkToVK;
    private String city;
    private String street;
    private String house;
    private String room;
    private String passToSeversk;
    private String phoneNumberConfidant;
    private String signs;
    private String specialSigns;
    private String health;
    private String otherTech;
    private String equipment;
    private String car;
    private String searchFormat;

    public VolunteerForProfile(int id, String volunteer_id, String surname, String name,
                               String patronymic, String dateOfBirth, String phoneNumber,
                               String forumNickname, String linkToVK, String city, String street,
                               String house, String room, String passToSeversk, String phoneNumberConfidant,
                               String signs, String specialSigns, String health, String otherTech,
                               String equipment, String car, String searchFormat) {
        this.id = id;
        this.volunteer_id = volunteer_id;
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.forumNickname = forumNickname;
        this.linkToVK = linkToVK;
        this.city = city;
        this.street = street;
        this.house = house;
        this.room = room;
        this.passToSeversk = passToSeversk;
        this.phoneNumberConfidant = phoneNumberConfidant;
        this.signs = signs;
        this.specialSigns = specialSigns;
        this.health = health;
        this.otherTech = otherTech;
        this.equipment = equipment;
        this.car = car;
        this.searchFormat = searchFormat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVolunteer_id() {
        return volunteer_id;
    }

    public void setVolunteer_id(String volunteer_id) {
        this.volunteer_id = volunteer_id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getForumNickname() {
        return forumNickname;
    }

    public void setForumNickname(String forumNickname) {
        this.forumNickname = forumNickname;
    }

    public String getLinkToVK() {
        return linkToVK;
    }

    public void setLinkToVK(String linkToVK) {
        this.linkToVK = linkToVK;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getPassToSeversk() {
        return passToSeversk;
    }

    public void setPassToSeversk(String passToSeversk) {
        this.passToSeversk = passToSeversk;
    }

    public String getPhoneNumberConfidant() {
        return phoneNumberConfidant;
    }

    public void setPhoneNumberConfidant(String phoneNumberConfidant) {
        this.phoneNumberConfidant = phoneNumberConfidant;
    }

    public String getSigns() {
        return signs;
    }

    public void setSigns(String signs) {
        this.signs = signs;
    }

    public String getSpecialSigns() {
        return specialSigns;
    }

    public void setSpecialSigns(String specialSigns) {
        this.specialSigns = specialSigns;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getOtherTech() {
        return otherTech;
    }

    public void setOtherTech(String otherTech) {
        this.otherTech = otherTech;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getSearchFormat() {
        return searchFormat;
    }

    public void setSearchFormat(String searchFormat) {
        this.searchFormat = searchFormat;
    }
}
