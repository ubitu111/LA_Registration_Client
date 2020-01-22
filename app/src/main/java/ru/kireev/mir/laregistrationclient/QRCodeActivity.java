package ru.kireev.mir.laregistrationclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;

import ru.kireev.mir.laregistrationclient.QRcodeUtils.QRCodeGenerator;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView imageViewQRCode;
    private Toast exitToast;
    private static final int IMAGE_WIDTH = 700;
    private static final int IMAGE_HEIGHT = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        imageViewQRCode = findViewById(R.id.imageViewQRCode);
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        TextView savedName = findViewById(R.id.textViewSavedName);
        TextView savedSurname = findViewById(R.id.textViewSavedSurname);
        TextView savedCallSign = findViewById(R.id.textViewSavedCallSign);
        TextView savedPhoneNumber = findViewById(R.id.textViewSavedPhoneNumber);
        TextView textViewSavedCarMark = findViewById(R.id.textViewSavedCarMark);
        TextView textViewSavedCarModel = findViewById(R.id.textViewSavedCarModel);
        TextView textViewSavedCarRegistrationNumber = findViewById(R.id.textViewSavedCarRegistrationNumber);
        TextView textViewSavedCarColor = findViewById(R.id.textViewSavedCarColor);
        LinearLayout linearLayoutInfoCarGroupTitles = findViewById(R.id.linearLayoutInfoCarGroupTitles);
        LinearLayout linearLayoutInfoCarGroup = findViewById(R.id.linearLayoutInfoCarGroup);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        savedName.setText(preferences.getString("name", ""));
        savedSurname.setText(preferences.getString("surname", ""));
        savedCallSign.setText(preferences.getString("callSign",""));
        savedPhoneNumber.setText(preferences.getString("phoneNumber", ""));
        textViewSavedCarMark.setText(preferences.getString("carMark", ""));
        textViewSavedCarModel.setText(preferences.getString("carModel", ""));
        textViewSavedCarRegistrationNumber.setText(preferences.getString("carRegistrationNumber", ""));
        textViewSavedCarColor.setText(preferences.getString("carColor", ""));
        if (preferences.getBoolean("haveACar", false)) {
            linearLayoutInfoCarGroupTitles.setVisibility(View.VISIBLE);
            linearLayoutInfoCarGroup.setVisibility(View.VISIBLE);
        } else {
            linearLayoutInfoCarGroupTitles.setVisibility(View.INVISIBLE);
            linearLayoutInfoCarGroup.setVisibility(View.INVISIBLE);
        }

        //смотрим результат метки из прошлой активности
        //если равно exist, значит QRCode уже существует и его нужно взять с хранилища
        //и вывести на экран

        if (message.equals("exist")){
            Bitmap bitmap = BitmapFactory.decodeFile(
                    getApplicationContext().getFilesDir()
                            + "/QRCodeLizaAlert.png");

            imageViewQRCode.setImageBitmap(bitmap);
        }

        //иначе, сохраняем введенные данные и выводим на экран QRCode

        else {
            Bitmap bitmap = null;
            try {
                bitmap = QRCodeGenerator.encodeAsBitmap(message, BarcodeFormat.QR_CODE, IMAGE_WIDTH, IMAGE_HEIGHT);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                imageViewQRCode.setImageBitmap(bitmap);
                saveQRCode(bitmap);
            }
        }

    }

    private void saveQRCode(Bitmap bitmap) {
        File file = new File(getApplicationContext().getFilesDir(), "QRCodeLizaAlert.png");

        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } finally {
                if (fos != null) fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void onClickEditData(View view) {
        File file = new File(getApplicationContext().getFilesDir(), "QRCodeLizaAlert.png");
        file.delete();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }



    @Override
    public void onBackPressed()
    {
        if (exitToast == null || exitToast.getView() == null || exitToast.getView().getWindowToken() == null) {
            exitToast = Toast.makeText(this, "Нажмите еще раз для выхода", Toast.LENGTH_LONG);
            exitToast.show();
        } else {
            exitToast.cancel();
            moveTaskToBack(true);
            finish();
            System.runFinalizersOnExit(true);
            System.exit(0);
        }


    }

    public void onDestroy() {
        super.onDestroy();

        System.runFinalizersOnExit(true);
        System.exit(0);
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
