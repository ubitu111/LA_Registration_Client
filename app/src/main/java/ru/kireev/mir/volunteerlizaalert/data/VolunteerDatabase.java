package ru.kireev.mir.volunteerlizaalert.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ru.kireev.mir.volunteerlizaalert.pojo.Departure;
import ru.kireev.mir.volunteerlizaalert.pojo.VolunteerForProfile;
import ru.kireev.mir.volunteerlizaalert.pojo.VolunteerForQR;

@Database(entities = {VolunteerForQR.class, VolunteerForProfile.class, Departure.class}, version = 6, exportSchema = false)
public abstract class VolunteerDatabase extends RoomDatabase {
    private static VolunteerDatabase database;
    private static final String DB_NAME = "volunteer.db";
    private static final Object LOCK = new Object();

    public static VolunteerDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (database == null) {
                database = Room.databaseBuilder(context, VolunteerDatabase.class, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return database;
        }
    }

    public abstract VolunteerForQRDao volunteerForQRDao();
    public abstract VolunteerForProfileDao volunteerForProfileDao();
    public abstract DepartureDao departureDao();
}
