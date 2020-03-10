package ru.kireev.mir.laregistrationclient.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

import ru.kireev.mir.laregistrationclient.data.VolunteerDatabase;
import ru.kireev.mir.laregistrationclient.pojo.Departure;

public class DeparturesViewModel extends AndroidViewModel {
    private static VolunteerDatabase database;
    private LiveData<List<Departure>> departures;

    public DeparturesViewModel(@NonNull Application application) {
        super(application);
        database = VolunteerDatabase.getInstance(getApplication());
        departures = database.departureDao().getAllDepartures();
    }

    public LiveData<List<Departure>> getDepartures() {
        return departures;
    }

    public void insertDeparture(@NonNull Departure departure){
        new InsertDepartureTask().execute(departure);
    }

    public void deleteDeparture(@NonNull Departure departure){
        new DeleteDepartureTask().execute(departure);
    }

    public Departure getDepartureById(int id) {
        try {
            return new GetDepartureByIdTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class InsertDepartureTask extends AsyncTask<Departure, Void, Void> {

        @Override
        protected Void doInBackground(Departure... departures) {
            database.departureDao().insertDeparture(departures[0]);
            return null;
        }
    }

    private static class DeleteDepartureTask extends AsyncTask<Departure, Void, Void> {

        @Override
        protected Void doInBackground(Departure... departures) {
            database.departureDao().deleteDeparture(departures[0]);
            return null;
        }
    }

    private static class GetDepartureByIdTask extends AsyncTask<Integer, Void, Departure> {

        @Override
        protected Departure doInBackground(Integer... integers) {
            return database.departureDao().getDepartureById(integers[0]);
        }
    }
}
