package ru.kireev.mir.laregistrationclient;

import android.Manifest;
import android.accounts.Account;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.kireev.mir.laregistrationclient.databinding.ActivityVolunteerProfileBinding;
import ru.kireev.mir.laregistrationclient.pojo.VolunteerForProfile;
import ru.kireev.mir.laregistrationclient.viewmodels.ProfileViewModel;

public class VolunteerProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private Object[] data;
    private static final int VOLUNTEER_FOR_DB_PRIMARY_KEY_ID = 0;
    private String volunteer_id;
    private ProfileViewModel viewModel;
    private ActivityVolunteerProfileBinding binding;
    private GoogleAccountCredential mCredential;
    private ProgressDialog mProgress;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    private static final String SPREADSHEET_ID = ""; // ID гугл таблицы
    private static final String GOOGLE_SHEETS_TAB = "Anketa app!A2";
    private static final String DELIMITER_FOR_SEARCH_FORMAT = " ; ";
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVolunteerProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initializeElements();

    }

    public void onClickSendProfile(View view) {
        //v2
        if (getEnteredData()) {
            sendDataToSheets();
        }
        else {
            Toast.makeText(this, R.string.fill_all_fields_with_asterisk, Toast.LENGTH_SHORT).show();
        }

    }

    private void sendDataToSheets(){
        if (!isDeviceOnline()) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
        else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        }
        else{
            new MakeRequestTask(mCredential).execute();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                sendDataToSheets();
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount(){
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)){
            if (mAuth.getCurrentUser() != null) {
                mCredential.setSelectedAccount(new Account(mAuth.getCurrentUser().getEmail(), getPackageName()));
                sendDataToSheets();
            }
            else {
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.GoogleBuilder().build());

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setIsSmartLockEnabled(false)
                                .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
            }
        }
        else {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.request_google_acc),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                VolunteerProfileActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void initializeElements() {
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(ProfileViewModel.class);
        data = new Object[16];
        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getString(R.string.sending_request));
        binding.rbNoPassToSeversk.setChecked(true);
        binding.rbNoCar.setChecked(true);
        binding.rbYesCar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.etCar.setVisibility(View.VISIBLE);
            } else {
                binding.etCar.setVisibility(View.GONE);
                binding.etCar.setText("");
            }
        });
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        mAuth = FirebaseAuth.getInstance();
        fillTheViews();
    }


    private void fillTheViews() {
        VolunteerForProfile volunteer = viewModel.getVolunteerForProfile();
        if (volunteer != null) {
            binding.etSurnameAnswer.setText(volunteer.getSurname());
            binding.etNameAnswer.setText(volunteer.getName());
            binding.etPatronymicAnswer.setText(volunteer.getPatronymic());
            binding.etDateOfBirthAnswer.setText(volunteer.getDateOfBirth());
            binding.etPhoneNumberAnswer.setText(volunteer.getPhoneNumber());
            binding.etForumNicknameAnswer.setText(volunteer.getForumNickname());
            binding.etLinkToVKAnswer.setText(volunteer.getLinkToVK());
            binding.etCity.setText(volunteer.getCity());
            binding.etStreet.setText(volunteer.getStreet());
            binding.etHouse.setText(volunteer.getHouse());
            binding.etRoom.setText(volunteer.getRoom());
            String pass = volunteer.getPassToSeversk();
            if (!pass.isEmpty() && pass.equals(getString(R.string.yes))) {
                binding.rbYesPassToSeversk.setChecked(true);
                binding.rbNoPassToSeversk.setChecked(false);
            }
            binding.etPhoneNumberConfidant.setText(volunteer.getPhoneNumberConfidant());
            binding.etSigns.setText(volunteer.getSigns());
            binding.etSpecialSigns.setText(volunteer.getSpecialSigns());
            binding.etHealth.setText(volunteer.getHealth());
            binding.etOtherTech.setText(volunteer.getOtherTech());
            binding.etEquipment.setText(volunteer.getEquipment());
            String car = volunteer.getCar();
            if (!car.isEmpty()) {
                binding.rbYesCar.setChecked(true);
                binding.etCar.setText(car);
            }
            String searchFormat = volunteer.getSearchFormat();
            if (!searchFormat.isEmpty()) {
                String[] searchFormatArr = searchFormat.split(DELIMITER_FOR_SEARCH_FORMAT);
                for (String s : searchFormatArr) {
                    if (s.equals(getString(R.string.forest)))
                        binding.cbSearchFormatForest.setChecked(true);
                    else if (s.equals(getString(R.string.city)))
                        binding.cbSearchFormatCity.setChecked(true);
                    else if (s.equals(getString(R.string.info_search)))
                        binding.cbSearchFormatInfo.setChecked(true);
                    else if (s.equals(getString(R.string.resource_help)))
                        binding.cbSearchFormatResource.setChecked(true);
                }
            }
            volunteer_id = volunteer.getVolunteer_id();
        } else {
            volunteer_id = UUID.randomUUID().toString();
        }

    }


    private boolean getEnteredData() {
        String surname = binding.etSurnameAnswer.getText().toString();
        String name = binding.etNameAnswer.getText().toString();
        String patronymic = binding.etPatronymicAnswer.getText().toString();
        String fullName = String.format("%s %s %s", surname, name, patronymic);
        String dateOfBirth = binding.etDateOfBirthAnswer.getText().toString();
        String phoneNumber = binding.etPhoneNumberAnswer.getText().toString();
        String forumNickname = binding.etForumNicknameAnswer.getText().toString();
        String linkToVk = binding.etLinkToVKAnswer.getText().toString();
        String city = binding.etCity.getText().toString();
        String street = binding.etStreet.getText().toString();
        String house = binding.etHouse.getText().toString();
        String room = binding.etRoom.getText().toString();

        if (surname.isEmpty() || name.isEmpty() || patronymic.isEmpty() || dateOfBirth.isEmpty() || phoneNumber.isEmpty()
                || city.isEmpty() || street.isEmpty() || house.isEmpty() || room.isEmpty()) {
            return false;
        }
        String address = String.format("%s, %s, %s, %s", city, street, house, room);
        String passToSeversk = binding.rbYesPassToSeversk.isChecked() ? getString(R.string.yes) : getString(R.string.no);
        String phoneNumberConfidant = binding.etPhoneNumberConfidant.getText().toString();
        String signs = binding.etSigns.getText().toString();
        String specialSigns = binding.etSpecialSigns.getText().toString();
        String health = binding.etHealth.getText().toString();
        String car = binding.rbYesCar.isChecked() ? binding.etCar.getText().toString() : getString(R.string.no);
        String otherTech = binding.etOtherTech.getText().toString();
        String equipment = binding.etEquipment.getText().toString();
        String searchFormat;
        StringBuilder sb = new StringBuilder();
        if (binding.cbSearchFormatForest.isChecked()) sb.append(getString(R.string.forest)).append(DELIMITER_FOR_SEARCH_FORMAT);
        if (binding.cbSearchFormatCity.isChecked()) sb.append(getString(R.string.city)).append(DELIMITER_FOR_SEARCH_FORMAT);
        if (binding.cbSearchFormatInfo.isChecked())
            sb.append(getString(R.string.info_search)).append(DELIMITER_FOR_SEARCH_FORMAT);
        if (binding.cbSearchFormatResource.isChecked()) sb.append(getString(R.string.resource_help));
        searchFormat = sb.toString();
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        data[0] = dateFormat.format(currentDate);
        data[1] = fullName;
        data[2] = dateOfBirth;
        data[3] = phoneNumber;
        data[4] = forumNickname;
        data[5] = linkToVk;
        data[6] = address;
        data[7] = car;
        data[8] = passToSeversk;
        data[9] = searchFormat;
        data[10] = otherTech;
        data[11] = equipment;
        data[12] = phoneNumberConfidant;
        data[13] = signs;
        data[14] = specialSigns;
        data[15] = health;
        VolunteerForProfile volunteer = new VolunteerForProfile(VOLUNTEER_FOR_DB_PRIMARY_KEY_ID, volunteer_id, surname, name, patronymic,
                dateOfBirth, phoneNumber, forumNickname, linkToVk, city, street, house, room, passToSeversk, phoneNumberConfidant, signs, specialSigns, health,
                otherTech, equipment, car, searchFormat);
        viewModel.insertVolunteerForProfile(volunteer);

        //Отправка данных на карту волонтеров
//        viewModel.sendInfoOnMap(volunteer_id, name, patronymic, surname, address, phoneNumber,
//                linkToVk, car, binding.rbYesCar.isChecked(), binding.rbYesPassToSeversk.isChecked());


        return true;
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {
        private Sheets mService;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(getString(R.string.app_name))
                    .build();
        }

        /**
         * Background task to call Google Sheets API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected Void doInBackground(Void... params) {
            try {
                setDataToApi();
            } catch (IOException e) {
                mLastError = e;
                cancel(true);

            }
            return null;
        }

        /**
         * Fetch a list of names and majors of students in a sample spreadsheet:
         *
         * @throws IOException
         */
        private void setDataToApi() throws IOException {
            List<List<Object>> values = Arrays.asList(Arrays.asList(data));
            ValueRange valueRange = new ValueRange();
            valueRange.setValues(values);
            mService.spreadsheets().values().append(SPREADSHEET_ID, GOOGLE_SHEETS_TAB, valueRange)
                    .setValueInputOption("RAW")
                    .execute();

        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgress.dismiss();
            Toast.makeText(VolunteerProfileActivity.this, R.string.ok_sending_profile, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            VolunteerProfileActivity.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(VolunteerProfileActivity.this, getString(R.string.error_occurred)
                            + mLastError, Toast.LENGTH_LONG).show();

                }
            } else {
                Toast.makeText(VolunteerProfileActivity.this, R.string.request_cancelled, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fill_out_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_action_profile_volunteer:
                Intent intent = new Intent(this, VolunteerProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_all_active_departures:
                Intent intentDepartures = new Intent(this, AllActiveDeparturesActivity.class);
                startActivity(intentDepartures);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
