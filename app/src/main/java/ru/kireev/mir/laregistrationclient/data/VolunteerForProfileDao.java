package ru.kireev.mir.laregistrationclient.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import ru.kireev.mir.laregistrationclient.pojo.VolunteerForProfile;

@Dao
public interface VolunteerForProfileDao {

    @Query("SELECT * FROM volunteer_for_profile limit 1")
    VolunteerForProfile getVolunteerForProfile();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVolunteerForProfile (VolunteerForProfile volunteer);
}
