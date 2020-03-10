package ru.kireev.mir.laregistrationclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

import ru.kireev.mir.laregistrationclient.databinding.ActivityMainBinding;
import ru.kireev.mir.laregistrationclient.pojo.VolunteerForQR;
import ru.kireev.mir.laregistrationclient.viewmodels.MainQRViewModel;

public class MainActivity extends AppCompatActivity {

    private MainQRViewModel viewModel;
    private static final int VOLUNTEER_ID = 0;
    private ActivityMainBinding binding;

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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //подписка на тему Alert
        FirebaseMessaging.getInstance().subscribeToTopic("alert");

        //проверяем, существует ли QRCode (вводились ли данные ранее)
        //если да, то запускаем активити с QRCode из хранилища
        File file = new File(getApplicationContext().getFilesDir() + "/QRCodeLizaAlert.png");
        if (file.exists()) {
            markForQrCode = "exist";
            startQRCodeActivity();
            finish();
        }

        binding.checkBoxHaveACar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.editTextCarMark.setVisibility(View.VISIBLE);
                binding.editTextCarModel.setVisibility(View.VISIBLE);
                binding.editTextCarRegistrationNumber.setVisibility(View.VISIBLE);
                binding.editTextCarColor.setVisibility(View.VISIBLE);
            }
            else {
                binding.editTextCarMark.setVisibility(View.GONE);
                binding.editTextCarModel.setVisibility(View.GONE);
                binding.editTextCarRegistrationNumber.setVisibility(View.GONE);
                binding.editTextCarColor.setVisibility(View.GONE);
            }
        });
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(MainQRViewModel.class);
        VolunteerForQR volunteerForQR = viewModel.getVolunteerForQR();
        if (volunteerForQR != null) {
            binding.editTextName.setText(volunteerForQR.getName());
            binding.editTextSurname.setText(volunteerForQR.getSurname());
            binding.editTextCallSign.setText(volunteerForQR.getCallSign());
            binding.editTextPhoneNumber.setText(volunteerForQR.getPhoneNumber());
            binding.checkBoxHaveACar.setChecked(volunteerForQR.getHaveACar() == 1);
            binding.editTextCarMark.setText(volunteerForQR.getCarMark());
            binding.editTextCarModel.setText(volunteerForQR.getCarModel());
            binding.editTextCarRegistrationNumber.setText(volunteerForQR.getCarRegistrationNumber());
            binding.editTextCarColor.setText(volunteerForQR.getCarColor());
        }

    }

    public void onClickSaveData(View view) {
        name = binding.editTextName.getText().toString();
        surname = binding.editTextSurname.getText().toString();
        callSign = binding.editTextCallSign.getText().toString();
        phoneNumber = binding.editTextPhoneNumber.getText().toString();
        haveACar = binding.checkBoxHaveACar.isChecked();
        carMark = binding.editTextCarMark.getText().toString();
        carModel = binding.editTextCarModel.getText().toString();
        carRegistrationNumber = binding.editTextCarRegistrationNumber.getText().toString();
        carColor = binding.editTextCarColor.getText().toString();

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
        finish();
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
