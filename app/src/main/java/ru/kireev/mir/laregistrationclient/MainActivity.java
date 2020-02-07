package ru.kireev.mir.laregistrationclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import ru.kireev.mir.laregistrationclient.pojo.VolunteerForQR;
import ru.kireev.mir.laregistrationclient.viewmodels.MainQRViewModel;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextCallSign;
    private EditText editTextPhoneNumber;
    private CheckBox checkBoxHaveACar;
    private EditText editTextCarMark;
    private EditText editTextCarModel;
    private EditText editTextCarRegistrationNumber;
    private EditText editTextCarColor;
    private MainQRViewModel viewModel;
    private static final int VOLUNTEER_ID = 0;

    private String name;
    private String surname;
    private String callSign;
    private String phoneNumber;
    private boolean haveACar;
    private String carMark;
    private String carModel;
    private String carRegistrationNumber;
    private String carColor;
    private String markForQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //проверяем, существует ли QRCode (вводились ли данные ранее)
        //если да, то запускаем активити с QRCode из хранилища
        File file = new File(getApplicationContext().getFilesDir() + "/QRCodeLizaAlert.png");
        if (file.exists()) {
            markForQrCode = "exist";
            startQRCodeActivity();
        }

        editTextName = findViewById(R.id.editTextName);
        editTextSurname = findViewById(R.id.editTextSurname);
        editTextCallSign = findViewById(R.id.editTextCallSign);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        checkBoxHaveACar = findViewById(R.id.checkBoxHaveACar);
        editTextCarMark = findViewById(R.id.editTextCarMark);
        editTextCarModel = findViewById(R.id.editTextCarModel);
        editTextCarRegistrationNumber = findViewById(R.id.editTextCarRegistrationNumber);
        editTextCarColor = findViewById(R.id.editTextCarColor);

        checkBoxHaveACar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextCarMark.setVisibility(View.VISIBLE);
                editTextCarModel.setVisibility(View.VISIBLE);
                editTextCarRegistrationNumber.setVisibility(View.VISIBLE);
                editTextCarColor.setVisibility(View.VISIBLE);
            }
            else {
                editTextCarMark.setVisibility(View.GONE);
                editTextCarModel.setVisibility(View.GONE);
                editTextCarRegistrationNumber.setVisibility(View.GONE);
                editTextCarColor.setVisibility(View.GONE);
            }
        });
        viewModel = new MainQRViewModel(getApplication());
        VolunteerForQR volunteerForQR = viewModel.getVolunteerForQR();
        if (volunteerForQR != null) {
            editTextName.setText(volunteerForQR.getName());
            editTextSurname.setText(volunteerForQR.getSurname());
            editTextCallSign.setText(volunteerForQR.getCallSign());
            editTextPhoneNumber.setText(volunteerForQR.getPhoneNumber());
            checkBoxHaveACar.setChecked(volunteerForQR.getHaveACar() == 1);
            editTextCarMark.setText(volunteerForQR.getCarMark());
            editTextCarModel.setText(volunteerForQR.getCarModel());
            editTextCarRegistrationNumber.setText(volunteerForQR.getCarRegistrationNumber());
            editTextCarColor.setText(volunteerForQR.getCarColor());
        }

//        ApiFactory factory = ApiFactory.getInstance();
//        ApiService apiService = factory.getApiService();
//        Disposable disposable = apiService.getResponse()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(responses -> {
//                    for (Response response : responses) {
//                        Log.i("response", response.getHello());
//                        Log.i("response", response.getId());
//                    }
//                },
//                        throwable -> Log.i("response", throwable.toString()));
//
//        Response response = new Response();
//        response.setHello("wakamakafo");
//        response.setId("123456712");
//        apiService.postResponse(response).enqueue(new Callback<Response>() {
//            @Override
//            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
//                Log.i("response", response.message());
//            }
//
//            @Override
//            public void onFailure(Call<Response> call, Throwable t) {
//
//            }
//        });
    }

    public void onClickSaveData(View view) {
        name = editTextName.getText().toString();
        surname = editTextSurname.getText().toString();
        callSign = editTextCallSign.getText().toString();
        phoneNumber = editTextPhoneNumber.getText().toString();
        haveACar = checkBoxHaveACar.isChecked();
        carMark = editTextCarMark.getText().toString();
        carModel = editTextCarModel.getText().toString();
        carRegistrationNumber = editTextCarRegistrationNumber.getText().toString();
        carColor = editTextCarColor.getText().toString();

        if (name.isEmpty() || surname.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, R.string.fill_in_fields_info, Toast.LENGTH_SHORT).show();
        } else if (haveACar && (carMark.isEmpty() || carModel.isEmpty() || carRegistrationNumber.isEmpty() || carColor.isEmpty())) {
            Toast.makeText(this, R.string.fill_in_fields_car, Toast.LENGTH_SHORT).show();
        }
        else {
            int haveACarInt = haveACar ? 1 : 0;
            VolunteerForQR volunteerForQR = new VolunteerForQR(VOLUNTEER_ID, name, surname, callSign, phoneNumber, carMark, carModel, carRegistrationNumber, carColor, haveACarInt);
            viewModel.insertVolunteerForQR(volunteerForQR);
            if (haveACar) {
                markForQrCode = getMessageWithCar();
            } else {
                markForQrCode = getMessageWithoutCar();
            }
            startQRCodeActivity();
        }
    }

    private String getMessageWithoutCar(){
         return name + "\n"
                + surname + "\n"
                + callSign + "\n"
                + phoneNumber;
    }

    private String getMessageWithCar(){
        return name + "\n"
                + surname + "\n"
                + callSign + "\n"
                + phoneNumber + "\n"
                + carMark + "\n"
                + carModel + "\n"
                + carRegistrationNumber + "\n"
                + carColor;
    }

    private void startQRCodeActivity(){
        Intent intent = new Intent(this, QRCodeActivity.class);
        intent.putExtra("message", markForQrCode);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fill_out_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_profile_volunteer) {
            Intent intent = new Intent(this, VolunteerProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
