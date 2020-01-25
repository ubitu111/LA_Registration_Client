package ru.kireev.mir.laregistrationclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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

import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VolunteerProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private EditText etFullNameAnswer;
    private EditText etDateOfBirthAnswer;
    private EditText etPhoneNumberAnswer;
    private EditText etForumNicknameAnswer;
    private EditText etLinkToVKAnswer;
    private EditText etAddress;
    private RadioButton rbYesPassToSeversk;
    private RadioButton rbNoPassToSeversk;
    private EditText etPhoneNumberConfidant;
    private EditText etSigns;
    private EditText etSpecialSigns;
    private EditText etHealth;
    private RadioButton rbNoCar;
    private RadioButton rbYesCar;
    private EditText etCar;
    private EditText etOtherTech;
    private EditText etEquipment;
    private CheckBox cbSearchFormatForest;
    private CheckBox cbSearchFormatCity;
    private CheckBox cbSearchFormatInfo;
    private CheckBox cbSearchFormatResource;
    private Object[] data;
    private SharedPreferences preferences;

    private GoogleAccountCredential mCredential;
    private ProgressDialog mProgress;
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { SheetsScopes.SPREADSHEETS };
    private static final String SPREADSHEET_ID = "17HH6oNL2XJ8_DnHJcLpY64v5gIASSLWxUslsWMonOfI";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_profile);
        initializeElements();
    }

    public void onClickSendProfile(View view) {
        if (getEnteredData()) {
            setDataToApi();
        } else {
            Toast.makeText(this, R.string.fill_all_fields_with_asterisk, Toast.LENGTH_SHORT).show();
        }
    }

    private void setDataToApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                setDataToApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.request_google_acc),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, R.string.request_google_play_services, Toast.LENGTH_SHORT).show();
                } else {
                    setDataToApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        setDataToApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    setDataToApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     * @param requestCode The request code passed in
     *     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
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
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     * @param requestCode The request code associated with the requested
     *         permission
     * @param list The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
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
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void initializeElements(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        data = new Object[16];
        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getString(R.string.sending_request));
        etFullNameAnswer = findViewById(R.id.etFullNameAnswer);
        etDateOfBirthAnswer = findViewById(R.id.etDateOfBirthAnswer);
        etPhoneNumberAnswer = findViewById(R.id.etPhoneNumberAnswer);
        etForumNicknameAnswer = findViewById(R.id.etForumNicknameAnswer);
        etLinkToVKAnswer = findViewById(R.id.etLinkToVKAnswer);
        etAddress = findViewById(R.id.etAddress);
        rbYesPassToSeversk = findViewById(R.id.rbYesPassToSeversk);
        rbNoPassToSeversk = findViewById(R.id.rbNoPassToSeversk);
        rbNoPassToSeversk.setChecked(true);
        etPhoneNumberConfidant = findViewById(R.id.etPhoneNumberConfidant);
        etSigns = findViewById(R.id.etSigns);
        etSpecialSigns = findViewById(R.id.etSpecialSigns);
        etHealth = findViewById(R.id.etHealth);
        rbNoCar = findViewById(R.id.rbNoCar);
        rbNoCar.setChecked(true);
        rbYesCar = findViewById(R.id.rbYesCar);
        etCar = findViewById(R.id.etCar);
        rbYesCar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etCar.setVisibility(View.VISIBLE);
                } else {
                    etCar.setVisibility(View.INVISIBLE);
                    etCar.setText("");
                }
            }
        });
        etOtherTech = findViewById(R.id.etOtherTech);
        etEquipment = findViewById(R.id.etEquipment);
        cbSearchFormatForest = findViewById(R.id.cbSearchFormatForest);
        cbSearchFormatCity = findViewById(R.id.cbSearchFormatCity);
        cbSearchFormatInfo = findViewById(R.id.cbSearchFormatInfo);
        cbSearchFormatResource = findViewById(R.id.cbSearchFormatResource);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        etFullNameAnswer.setText(preferences.getString("fullName", ""));
        etDateOfBirthAnswer.setText(preferences.getString("dateOfBirth", ""));
        etPhoneNumberAnswer.setText(preferences.getString("phoneNumberProfile", ""));
        etForumNicknameAnswer.setText(preferences.getString("forumNickName", ""));
        etLinkToVKAnswer.setText(preferences.getString("linkToVk", ""));
        etAddress.setText(preferences.getString("address", ""));
        String pass = preferences.getString("passToSeversk", "");
        if (!pass.isEmpty() && pass.equals(getString(R.string.yes))) {
                rbYesPassToSeversk.setChecked(true);
                rbNoPassToSeversk.setChecked(false);
        }
        etPhoneNumberConfidant.setText(preferences.getString("phoneNumberConfidant", ""));
        etSigns.setText(preferences.getString("signs", ""));
        etSpecialSigns.setText(preferences.getString("specialSigns", ""));
        etHealth.setText(preferences.getString("health", ""));
        etOtherTech.setText(preferences.getString("otherTech", ""));
        etEquipment.setText(preferences.getString("equipment", ""));
        String car = preferences.getString("car", "");
        if (!car.isEmpty()) {
            rbYesCar.setChecked(true);
            etCar.setText(car);
        }
        String searchFormat = preferences.getString("searchFormat", "");
        if (!searchFormat.isEmpty()) {
            String[] searchFormatArr = searchFormat.split("___");
            for (String s : searchFormatArr) {
                if (s.equals(getString(R.string.forest)))
                        cbSearchFormatForest.setChecked(true);
                else if (s.equals(getString(R.string.city)))
                        cbSearchFormatCity.setChecked(true);
                else if (s.equals(getString(R.string.info_search)))
                        cbSearchFormatInfo.setChecked(true);
                else if (s.equals(getString(R.string.resource_help)))
                        cbSearchFormatResource.setChecked(true);
                }
            }
        }



    private boolean getEnteredData() {
        String fullName = etFullNameAnswer.getText().toString();
        String dateOfBirth = etDateOfBirthAnswer.getText().toString();
        String phoneNumber = etPhoneNumberAnswer.getText().toString();
        String forumNickname = etForumNicknameAnswer.getText().toString();
        String linkToVk = etLinkToVKAnswer.getText().toString();
        String address = etAddress.getText().toString();

        if (fullName.isEmpty() || dateOfBirth.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
            return false;
        }

        String passToSeversk = rbYesPassToSeversk.isChecked() ? getString(R.string.yes) : getString(R.string.no);
        String phoneNumberConfidant = etPhoneNumberConfidant.getText().toString();
        String signs = etSigns.getText().toString();
        String specialSigns = etSpecialSigns.getText().toString();
        String health = etHealth.getText().toString();
        String car = rbYesCar.isChecked() ? etCar.getText().toString() : getString(R.string.no);
        String otherTech = etOtherTech.getText().toString();
        String equipment = etEquipment.getText().toString();
        String searchFormat;
        StringBuilder sb = new StringBuilder();
        if (cbSearchFormatForest.isChecked()) sb.append(getString(R.string.forest)).append("___");
        if (cbSearchFormatCity.isChecked()) sb.append(getString(R.string.city)).append("___");
        if (cbSearchFormatInfo.isChecked()) sb.append(getString(R.string.info_search)).append("___");
        if (cbSearchFormatResource.isChecked()) sb.append(getString(R.string.resource_help));
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
        preferences.edit()
                .putString("fullName", fullName)
                .putString("dateOfBirth", dateOfBirth)
                .putString("phoneNumberProfile", phoneNumber)
                .putString("forumNickName", forumNickname)
                .putString("linkToVk", linkToVk)
                .putString("address", address)
                .putString("car", car)
                .putString("passToSeversk", passToSeversk)
                .putString("searchFormat", searchFormat)
                .putString("otherTech", otherTech)
                .putString("equipment", equipment)
                .putString("phoneNumberConfidant", phoneNumberConfidant)
                .putString("signs", signs)
                .putString("specialSigns", specialSigns)
                .putString("health", health).apply();

        return true;
    }

    /**
     * An asynchronous task that handles the Google Sheets API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private class MakeRequestTask extends AsyncTask <Void, Void, Void> {
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
         * TestTable
         * https://docs.google.com/spreadsheets/d/17HH6oNL2XJ8_DnHJcLpY64v5gIASSLWxUslsWMonOfI/edit#gid=0
         * @throws IOException
         */
        private void setDataToApi() throws IOException {
            String range = "Data!A2";
            List<List<Object>> values = Arrays.asList(Arrays.asList(data));
            ValueRange valueRange = new ValueRange();
            valueRange.setValues(values);
            mService.spreadsheets().values().append(SPREADSHEET_ID, range, valueRange)
                    .setValueInputOption("RAW")
                    .execute();

        }

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgress.hide();
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
                            + mLastError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(VolunteerProfileActivity.this, R.string.request_cancelled, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
