package ru.kireev.mir.volunteerlizaalert;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;

import ru.kireev.mir.volunteerlizaalert.databinding.ActivityQrcodeBinding;
import ru.kireev.mir.volunteerlizaalert.pojo.VolunteerForQR;
import ru.kireev.mir.volunteerlizaalert.utils.QRCodeGenerator;
import ru.kireev.mir.volunteerlizaalert.viewmodels.MainQRViewModel;

public class QRCodeActivity extends AppCompatActivity {

    private ActivityQrcodeBinding binding;
    private Toast exitToast;
    private MainQRViewModel viewModel;
    private static final int IMAGE_WIDTH = 700;
    private static final int IMAGE_HEIGHT = 700;
    private static final String NAME_OF_QR_CODE_FILE = "QRCodeLizaAlert.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrcodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewModel = new MainQRViewModel(getApplication());
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        VolunteerForQR volunteerForQR = viewModel.getVolunteerForQR();
        if (volunteerForQR != null) {
            binding.textViewSavedFullName.setText(volunteerForQR.getFullName());
            binding.textViewSavedCallSign.setText(volunteerForQR.getCallSign());
            binding.textViewSavedForumNickName.setText(volunteerForQR.getForumNickName());
            binding.textViewSavedRegion.setText(volunteerForQR.getRegion());
            binding.textViewSavedPhoneNumber.setText(volunteerForQR.getPhoneNumber());
            binding.textViewSavedCarMark.setText(volunteerForQR.getCar());
        }

        //смотрим результат метки из прошлой активности
        //если равно exist, значит QRCode уже существует и его нужно взять с хранилища
        //и вывести на экран

        if (message.equals("exist")){
            Bitmap bitmap = BitmapFactory.decodeFile(
                    getApplicationContext().getFilesDir()
                            + "/" + NAME_OF_QR_CODE_FILE);

            binding.imageViewQRCode.setImageBitmap(bitmap);
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
                binding.imageViewQRCode.setImageBitmap(bitmap);
                saveQRCode(bitmap);
            }
        }
    }

    private void saveQRCode(Bitmap bitmap) {
        File file = new File(getApplicationContext().getFilesDir(), NAME_OF_QR_CODE_FILE);

        try {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void onClickEditData(View view) {
        File file = new File(getApplicationContext().getFilesDir(), NAME_OF_QR_CODE_FILE);
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
            finish();
        }
    }

    //Убрана возможность отправить анкету и просмотр оповещений для регионов
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
