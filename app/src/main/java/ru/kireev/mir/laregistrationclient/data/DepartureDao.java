package ru.kireev.mir.laregistrationclient.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ru.kireev.mir.laregistrationclient.pojo.Departure;

@Dao
public interface DepartureDao {
    @Query("SELECT * FROM departures ORDER BY id DESC")
    LiveData<List<Departure>> getAllDepartures();

    @Insert
    void insertDeparture(Departure departure);

    @Delete
    void deleteDeparture(Departure departure);

    @Query("SELECT * FROM departures WHERE id =:id")
    Departure getDepartureById(int id);
}
