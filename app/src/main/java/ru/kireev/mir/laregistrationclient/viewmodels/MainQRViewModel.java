package ru.kireev.mir.laregistrationclient.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.concurrent.ExecutionException;

import ru.kireev.mir.laregistrationclient.data.VolunteerDatabase;
import ru.kireev.mir.laregistrationclient.pojo.VolunteerForQR;

public class MainQRViewModel extends AndroidViewModel {
    private static VolunteerDatabase database;

    public MainQRViewModel(@NonNull Application application) {
        super(application);
        database = VolunteerDatabase.getInstance(getApplication());
    }

    public VolunteerForQR getVolunteerForQR(){
        try {
            return new GetVolunteerForQRTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertVolunteerForQR(VolunteerForQR volunteer){
        new InsertVolunteerForQRTask().execute(volunteer);
    }

    private static class GetVolunteerForQRTask extends AsyncTask<Void, Void, VolunteerForQR>{

        @Override
        protected VolunteerForQR doInBackground(Void... voids) {
            return database.volunteerForQRDao().getVolunteerForQR();
        }
    }

    private static class InsertVolunteerForQRTask extends AsyncTask<VolunteerForQR, Void, Void> {

        @Override
        protected Void doInBackground(VolunteerForQR... volunteerForQRS) {
            if (volunteerForQRS.length > 0 && volunteerForQRS[0] != null) {
                database.volunteerForQRDao().insertVolunteerForQR(volunteerForQRS[0]);
            }
            return null;
        }
    }
}
