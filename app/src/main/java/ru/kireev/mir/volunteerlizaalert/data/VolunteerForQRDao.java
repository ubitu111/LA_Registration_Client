package ru.kireev.mir.volunteerlizaalert.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import ru.kireev.mir.volunteerlizaalert.pojo.VolunteerForQR;

@Dao
public interface VolunteerForQRDao {
    @Query("SELECT * FROM volunteer_for_qr limit 1")
    VolunteerForQR getVolunteerForQR();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertVolunteerForQR(VolunteerForQR volunteer);

}
