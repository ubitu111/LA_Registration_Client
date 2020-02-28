package ru.kireev.mir.laregistrationclient.viewmodels;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.kireev.mir.laregistrationclient.R;
import ru.kireev.mir.laregistrationclient.api.ApiFactory;
import ru.kireev.mir.laregistrationclient.api.ApiService;
import ru.kireev.mir.laregistrationclient.data.VolunteerDatabase;
import ru.kireev.mir.laregistrationclient.pojo.VolunteerForMap;
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

    public void sendInfoOnMap(String id, String firstName, String middleName, String lastName,
                              String address, String phoneNumber, String linkToVK,
                              String car, boolean withCar, boolean severskPass) {
        Geocoder geocoder = new Geocoder(getApplication());
        String latitude = "56.491787";
        String longitude = "84.987642";
        JSONObject geo = new JSONObject();
        try {
            Address result = geocoder.getFromLocationName(address, 2).get(0);
            latitude = Double.toString(result.getLatitude());
            longitude = Double.toString(result.getLongitude());

        } catch (IOException ex) {
            Toast.makeText(getApplication(), getApplication().getString(R.string.error_occured_address_to_geo) + ex, Toast.LENGTH_SHORT).show();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getApplication(), getApplication().getString(R.string.error_occured_address_to_geo) + e, Toast.LENGTH_SHORT).show();
        }

        try {
            geo.put("lat", latitude).put("long", longitude);
        } catch (JSONException ex) {
            Toast.makeText(getApplication(), getApplication().getString(R.string.error_occurred) + ex, Toast.LENGTH_SHORT).show();
        }

        VolunteerForMap volunteerForMap = new VolunteerForMap(id, firstName, middleName, lastName,
                geo.toString(), phoneNumber, address, linkToVK, car, withCar, severskPass);
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        apiService.postVolunteer(volunteerForMap).enqueue(new Callback<VolunteerForMap>() {
            @Override
            public void onResponse(Call<VolunteerForMap> call, Response<VolunteerForMap> response) {
                Log.i("response", response.errorBody() + response.message() + " 78");
                Log.i("response", geo.toString() + " 78");

            }

            @Override
            public void onFailure(Call<VolunteerForMap> call, Throwable t) {
                Log.i("response", t.toString() + " 83");
            }
        });

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
