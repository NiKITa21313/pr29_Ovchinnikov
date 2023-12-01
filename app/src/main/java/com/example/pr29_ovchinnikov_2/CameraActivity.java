package com.example.pr29_ovchinnikov_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    Button foto, back;
    ImageView fotoView;
    Uri mUri;
    Context mContext;
    private static final int PHOTO_INTENT_REQUEST_CODE = 100;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final String PHOTO_DIRECTORY_NAME = "Foto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mContext = this;
        foto = findViewById(R.id.btnCamera);
        foto.setOnClickListener(this);
        back = findViewById(R.id.btnBack);
        back.setOnClickListener(this);
        fotoView = findViewById(R.id.fotoView);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnCamera) {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            // Фотка сделана, извлекаем миниатюру картинки
            Bundle extras = data.getExtras();
            Bitmap thumbnailBitmap = (Bitmap) extras.get("data");
            fotoView.setImageBitmap(thumbnailBitmap);//фотка в imgView на экране
            if (isExternalStorageWritable()) {
                // Получаем публичную директорию Pictures
                File photoDirectory = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), PHOTO_DIRECTORY_NAME);
                if (!photoDirectory.exists()) {
                    // Создаем директорию, если она не существует
                    if (!photoDirectory.mkdirs()) {
                        // Обработка ошибки создания директории
                        //return null;
                    }
                }
                String timeStamp = String.valueOf(System.currentTimeMillis());//название файла, это будет текущее время для удобства
                String photoFileName = "IMG_" + timeStamp + ".png";

                // Создаем файл для сохранения фотографии
                File photoFile = new File(photoDirectory, photoFileName);

                try {
                    // Сохраняем фотографию в файл
                    FileOutputStream fos = new FileOutputStream(photoFile);
                    thumbnailBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                } catch (IOException e) {
                    // Обработка ошибок сохранения фотографии
                    e.printStackTrace();
                }
            } else {
                // Обработка отсутствия доступного внешнего хранилища
            }
        }


    }
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
