package ru.kireev.mir.laregistrationclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;

import ru.kireev.mir.laregistrationclient.QRcodeUtils.QRCodeGenerator;

public class QRCodeActivity extends AppCompatActivity {

    private ImageView imageViewQRCode;
    private Toast exitToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        imageViewQRCode = findViewById(R.id.imageViewQRCode);
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

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
                bitmap = QRCodeGenerator.encodeAsBitmap(message, BarcodeFormat.QR_CODE, 1000, 1000);
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


}
