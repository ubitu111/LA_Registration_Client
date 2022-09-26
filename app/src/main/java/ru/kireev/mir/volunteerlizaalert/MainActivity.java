package ru.kireev.mir.volunteerlizaalert;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;

import ru.kireev.mir.volunteerlizaalert.databinding.ActivityMainBinding;
import ru.kireev.mir.volunteerlizaalert.pojo.VolunteerForQR;
import ru.kireev.mir.volunteerlizaalert.viewmodels.MainQRViewModel;

public class MainActivity extends AppCompatActivity {

    private MainQRViewModel viewModel;
    private static final int VOLUNTEER_ID = 0;
    private ActivityMainBinding binding;

    private String fullName;
    private String callSign;
    private String forumNickName;
    private String region;
    private String phoneNumber;
    private String car;
    private String markForQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //подписка на тему Alert Томск (оповещения о поисках)
//        FirebaseMessaging.getInstance().subscribeToTopic("alert");

        //проверяем, существует ли QRCode (вводились ли данные ранее)
        //если да, то запускаем активити с QRCode из хранилища
        File file = new File(getApplicationContext().getFilesDir() + "/QRCodeLizaAlert.png");
        if (file.exists()) {
            markForQrCode = "exist";
            startQRCodeActivity();
            finish();
        }

        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(MainQRViewModel.class);
        VolunteerForQR volunteerForQR = viewModel.getVolunteerForQR();
        if (volunteerForQR != null) {
            binding.editTextFullName.setText(volunteerForQR.getFullName());
            binding.editTextCallSign.setText(volunteerForQR.getCallSign());
            binding.editTextForumNickname.setText(volunteerForQR.getForumNickName());
            binding.editTextRegion.setText(volunteerForQR.getRegion());
            binding.editTextPhoneNumber.setText(volunteerForQR.getPhoneNumber());
            binding.editTextCar.setText(volunteerForQR.getCar());
        }
    }

    public void onClickSaveData(View view) {
        fullName = String.valueOf(binding.editTextFullName.getText());
        callSign = String.valueOf(binding.editTextCallSign.getText());
        forumNickName = String.valueOf(binding.editTextForumNickname.getText());
        region = String.valueOf(binding.editTextRegion.getText());
        phoneNumber = String.valueOf(binding.editTextPhoneNumber.getText());
        car = String.valueOf(binding.editTextCar.getText());

        if (isValidString(fullName) && isValidString(phoneNumber) && isValidPhone(phoneNumber)) {
            VolunteerForQR volunteerForQR = new VolunteerForQR(VOLUNTEER_ID, fullName, callSign, forumNickName, region, phoneNumber, car);
            viewModel.insertVolunteerForQR(volunteerForQR);
            markForQrCode = getMessage();
            startQRCodeActivity();
        } else {
            Toast.makeText(this, R.string.fill_in_fields_info, Toast.LENGTH_SHORT).show();
        }
    }

    private String getMessage() {
        return fullName + "\n" + callSign + "\n" + forumNickName + "\n" + region + "\n" + phoneNumber + "\n" + car;
    }

    private void startQRCodeActivity() {
        Intent intent = new Intent(this, QRCodeActivity.class);
        intent.putExtra("message", markForQrCode);
        startActivity(intent);
        finish();
    }

    private boolean isValidString(@NonNull String value) {
        String nullString = "null";
        return !value.isEmpty() && !value.equals(nullString);
    }

    private boolean isValidPhone(String phoneNumber) {
        return phoneNumber.length() == 11;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_fill_out_profile, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        switch (id) {
//            case R.id.menu_action_profile_volunteer:
//                Intent intent = new Intent(this, VolunteerProfileActivity.class);
//                startActivity(intent);
//                break;
//            case R.id.menu_all_active_departures:
//                Intent intentDepartures = new Intent(this, AllActiveDeparturesActivity.class);
//                startActivity(intentDepartures);
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
