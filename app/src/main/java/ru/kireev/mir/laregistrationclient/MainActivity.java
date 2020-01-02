package ru.kireev.mir.laregistrationclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextSurname;
    private EditText editTextCallSign;
    private EditText editTextPhoneNumber;
    private String name;
    private String surname;
    private String callSign;
    private String phoneNumber;
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

    }

    public void onClickSaveData(View view) {
        name = editTextName.getText().toString();
        surname = editTextSurname.getText().toString();
        callSign = editTextCallSign.getText().toString();
        phoneNumber = editTextPhoneNumber.getText().toString();
        if (name.isEmpty() || surname.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Заполните поля Имя, Фамилия и Номер телефона!", Toast.LENGTH_SHORT).show();
        }
        else {
            markForQrCode = getMessage();
            startQRCodeActivity();
        }
    }

    private String getMessage(){
         return name + "\n"
                + surname + "\n"
                + callSign + "\n"
                + phoneNumber + "\n";
    }

    private void startQRCodeActivity(){
        Intent intent = new Intent(this, QRCodeActivity.class);
        intent.putExtra("message", markForQrCode);
        startActivity(intent);
    }
}
