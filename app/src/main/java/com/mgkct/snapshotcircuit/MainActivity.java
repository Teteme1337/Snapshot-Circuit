package com.mgkct.snapshotcircuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import retrofit2.Call;
import java.util.Random;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;

    private Uri photoUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("Config", MODE_PRIVATE);

        // Лаунчер для получения результата
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Uri imageUri = (data != null && data.getData() != null) ? data.getData() : photoUri;
                        if (imageUri != null) {
                            String imageUrl = imageUri.toString();
                            // Отправка URL изображения на сервер
                            Intent intent = new Intent(MainActivity.this, ComponentActivity.class);
                            Random random = new Random();
                            intent.putExtra("componentId", random.nextInt(501));
                            Toast.makeText(MainActivity.this, "Распознавание все еще настраивается и иногда может выдать ОЧЕНЬ странный результат!", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                    }
                });

        CardView buttonCatalog = findViewById(R.id.component_background_1);
        buttonCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CategoryListActvity.class);
                startActivity(intent);
            }
        });

        CardView exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putInt("myId", -1);
                editor.commit();
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        CardView buttonCamera = findViewById(R.id.component_background_3);
        buttonCamera.setOnClickListener(view -> checkPermissionsAndShowChooser());
    }

    // Запрос разрешений на камеру и хранилище
    private void checkPermissionsAndShowChooser() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            showImageSourceChooser();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE || requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageSourceChooser();
            } else {
                Toast.makeText(this, "Разрешения не предоставлены!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showImageSourceChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(this, "com.mgkct.snapshotcircuit.fileprovider", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }

        Intent chooserIntent = Intent.createChooser(galleryIntent, "Выберите источник изображения");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        activityResultLauncher.launch(chooserIntent);
    }

    private File createImageFile() throws IOException {
        String fileName = "photo_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(null);
        return File.createTempFile(fileName, ".jpg", storageDir);
    }
}