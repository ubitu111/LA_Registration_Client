package ru.kireev.mir.laregistrationclient.pojo;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "departures")
public class Departure {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String messageTopic;
    private String departureTime;
    private String departurePlace;
    private String coordinator;
    private String inforg;
    private String inforgPhoneNumber;
    private String forumTopic;

    public Departure(int id, String messageTopic, String departureTime, String departurePlace, String coordinator, String inforg, String inforgPhoneNumber, String forumTopic) {
        this.id = id;
        this.messageTopic = messageTopic;
        this.departureTime = departureTime;
        this.departurePlace = departurePlace;
        this.coordinator = coordinator;
        this.inforg = inforg;
        this.inforgPhoneNumber = inforgPhoneNumber;
        this.forumTopic = forumTopic;
    }

    @Ignore
    public Departure(String messageTopic, String departureTime, String departurePlace, String coordinator, String inforg, String inforgPhoneNumber, String forumTopic) {
        this.messageTopic = messageTopic;
        this.departureTime = departureTime;
        this.departurePlace = departurePlace;
        this.coordinator = coordinator;
        this.inforg = inforg;
        this.inforgPhoneNumber = inforgPhoneNumber;
        this.forumTopic = forumTopic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageTopic() {
        return messageTopic;
    }

    public void setMessageTopic(String messageTopic) {
        this.messageTopic = messageTopic;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public void setDeparturePlace(String departurePlace) {
        this.departurePlace = departurePlace;
    }

    public String getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(String coordinator) {
        this.coordinator = coordinator;
    }

    public String getInforg() {
        return inforg;
    }

    public void setInforg(String inforg) {
        this.inforg = inforg;
    }

    public String getForumTopic() {
        return forumTopic;
    }

    public void setForumTopic(String forumTopic) {
        this.forumTopic = forumTopic;
    }

    public String getInforgPhoneNumber() {
        return inforgPhoneNumber;
    }

    public void setInforgPhoneNumber(String inforgPhoneNumber) {
        this.inforgPhoneNumber = inforgPhoneNumber;
    }
}
