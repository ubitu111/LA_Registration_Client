package ru.kireev.mir.laregistrationclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import ru.kireev.mir.laregistrationclient.pojo.VolunteerForQR;
import ru.kireev.mir.laregistrationclient.utils.QRCodeGenerator;
import ru.kireev.mir.laregistrationclient.viewmodels.MainQRViewModel;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView imageViewQRCode;
    private Toast exitToast;
    private MainQRViewModel viewModel;
    private static final int IMAGE_WIDTH = 700;
    private static final int IMAGE_HEIGHT = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        imageViewQRCode = findViewById(R.id.imageViewQRCode);
        viewModel = new MainQRViewModel(getApplication());
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        TextView textViewSavedName = findViewById(R.id.textViewSavedName);
        TextView textViewSavedSurname = findViewById(R.id.textViewSavedSurname);
        TextView textViewSavedCallSign = findViewById(R.id.textViewSavedCallSign);
        TextView textViewSavedPhoneNumber = findViewById(R.id.textViewSavedPhoneNumber);
        TextView textViewSavedCarMark = findViewById(R.id.textViewSavedCarMark);
        TextView textViewSavedCarModel = findViewById(R.id.textViewSavedCarModel);
        TextView textViewSavedCarRegistrationNumber = findViewById(R.id.textViewSavedCarRegistrationNumber);
        TextView textViewSavedCarColor = findViewById(R.id.textViewSavedCarColor);
        LinearLayout linearLayoutInfoCarGroupTitles = findViewById(R.id.linearLayoutInfoCarGroupTitles);
        LinearLayout linearLayoutInfoCarGroup = findViewById(R.id.linearLayoutInfoCarGroup);

        //тестовый json для карт
//        try {
//            JSONObject value = new JSONObject()
//                    .put("id", "123")
//                    .put("firstName", "Геннадий")
//                    .put("middleName", "Иванович")
//                    .put("lastName", "Пупкин")
//                    .put("geo", new JSONObject().put("lat", "52.0000").put("long", "83.0000"))
//                    .put("phoneNumber", "89995556633")
//                    .put("address", "Томск, Ленина 13, 14")
//                    .put("withCar", true)
//                    .put("severskPass", true);
//
//            Log.i("myjson", value.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        UUID id = UUID.randomUUID();
//        Log.i("myjson", id.toString());
//        Geocoder geocoder = new Geocoder(this);
//        try {
//            List<Address> addresses = geocoder.getFromLocationName("г. Томск, улица Карла Ильмера 21, подъезд 6, квартира 215 ", 2);
//            for (Address address : addresses) {
//                Log.i("myjson", Double.toString(address.getLatitude()));
//                Log.i("myjson", Double.toString(address.getLongitude()));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        VolunteerForQR volunteerForQR = viewModel.getVolunteerForQR();
        if (volunteerForQR != null) {
            textViewSavedName.setText(volunteerForQR.getName());
            textViewSavedSurname.setText(volunteerForQR.getSurname());
            textViewSavedCallSign.setText(volunteerForQR.getCallSign());
            textViewSavedPhoneNumber.setText(volunteerForQR.getPhoneNumber());
            if (volunteerForQR.getHaveACar() == 1) {
                linearLayoutInfoCarGroupTitles.setVisibility(View.VISIBLE);
                linearLayoutInfoCarGroup.setVisibility(View.VISIBLE);
                textViewSavedCarMark.setText(volunteerForQR.getCarMark());
                textViewSavedCarModel.setText(volunteerForQR.getCarModel());
                textViewSavedCarRegistrationNumber.setText(volunteerForQR.getCarRegistrationNumber());
                textViewSavedCarColor.setText(volunteerForQR.getCarColor());
            } else {
                linearLayoutInfoCarGroupTitles.setVisibility(View.INVISIBLE);
                linearLayoutInfoCarGroup.setVisibility(View.INVISIBLE);
            }
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
            exitToast = Toast.makeText(this, R.string.back_to_exit , Toast.LENGTH_LONG);
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
