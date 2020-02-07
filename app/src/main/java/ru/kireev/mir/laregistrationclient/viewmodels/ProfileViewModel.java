package ru.kireev.mir.laregistrationclient.viewmodels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.concurrent.ExecutionException;

import ru.kireev.mir.laregistrationclient.data.VolunteerDatabase;
import ru.kireev.mir.laregistrationclient.pojo.VolunteerForProfile;

public class ProfileViewModel extends AndroidViewModel {
    private static VolunteerDatabase database;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        database = VolunteerDatabase.getInstance(getApplication());
    }

    public VolunteerForProfile getVolunteerForProfile(){
        try {
            return new GetVolunteerForProfileTask().execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertVolunteerForProfile(VolunteerForProfile volunteer){
        new InsertVolunteerForProfileTask().execute(volunteer);
    }

    private static class GetVolunteerForProfileTask extends AsyncTask<Void, Void, VolunteerForProfile>{

        @Override
        protected VolunteerForProfile doInBackground(Void... voids) {
            return database.volunteerForProfileDao().getVolunteerForProfile();
        }
    }

    private static class InsertVolunteerForProfileTask extends AsyncTask<VolunteerForProfile, Void, Void>{

        @Override
        protected Void doInBackground(VolunteerForProfile... volunteerForProfiles) {
            if (volunteerForProfiles.length > 0 && volunteerForProfiles[0] != null){
                database.volunteerForProfileDao().insertVolunteerForProfile(volunteerForProfiles[0]);
            }
            return null;
        }
    }
}
