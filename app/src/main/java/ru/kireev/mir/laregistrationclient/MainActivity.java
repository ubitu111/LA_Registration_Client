package ru.kireev.mir.laregistrationclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

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

        checkBoxHaveACar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editTextCarMark.setVisibility(View.VISIBLE);
                    editTextCarModel.setVisibility(View.VISIBLE);
                    editTextCarRegistrationNumber.setVisibility(View.VISIBLE);
                    editTextCarColor.setVisibility(View.VISIBLE);
                }
                else {
                    editTextCarMark.setVisibility(View.INVISIBLE);
                    editTextCarModel.setVisibility(View.INVISIBLE);
                    editTextCarRegistrationNumber.setVisibility(View.INVISIBLE);
                    editTextCarColor.setVisibility(View.INVISIBLE);
                }
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editTextName.setText(preferences.getString("name", ""));
        editTextSurname.setText(preferences.getString("surname", ""));
        editTextCallSign.setText(preferences.getString("callSign",""));
        editTextPhoneNumber.setText(preferences.getString("phoneNumber", ""));
        checkBoxHaveACar.setChecked(preferences.getBoolean("haveACar", haveACar));
        editTextCarMark.setText(preferences.getString("carMark", carMark));
        editTextCarModel.setText(preferences.getString("carModel", carModel));
        editTextCarRegistrationNumber.setText(preferences.getString("carRegistrationNumber", carRegistrationNumber));
        editTextCarColor.setText(preferences.getString("carColor", carColor));
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
            Toast.makeText(this, "Заполните поля Имя, Фамилия и Номер телефона!", Toast.LENGTH_SHORT).show();
        } else if (haveACar && (carMark.isEmpty() || carModel.isEmpty() || carRegistrationNumber.isEmpty() || carColor.isEmpty())) {
            Toast.makeText(this, "Заполните все данные об автомобиле!", Toast.LENGTH_SHORT).show();
        }
        else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            preferences.edit().putString("name", name)
                     .putString("surname", surname)
                     .putString("callSign", callSign)
                     .putString("phoneNumber", phoneNumber)
                     .putBoolean("haveACar", haveACar)
                     .putString("carMark", carMark)
                     .putString("carModel", carModel)
                     .putString("carRegistrationNumber", carRegistrationNumber)
                     .putString("carColor", carColor).apply();
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
}
