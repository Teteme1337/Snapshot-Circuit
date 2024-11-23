package com.mgkct.snapshotcircuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComponentActivity extends AppCompatActivity {

    private TextView componentTitle, componentDescription, propertiesWord;
    private String documentationName;
    private ImageView componentImage;
    private LinearLayout propertiesContainer, pdf, pdfLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_activity);

        // Инициализация UI элементов
        componentTitle = findViewById(R.id.component_title);
        componentDescription = findViewById(R.id.component_description);
        componentImage = findViewById(R.id.component_image);
        propertiesContainer = findViewById(R.id.properties_container);
        pdf = findViewById(R.id.pdf);
        pdfLayout = findViewById(R.id.pdf_layout);
        propertiesWord = findViewById(R.id.properties_word);

        sharedPreferences = getSharedPreferences("Config", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("myId", -1);

        // Получаем ID компонента из Intent
        int componentId = getIntent().getIntExtra("componentId", -1);
        Log.d("componentId", componentId + "");

        if (componentId == -1) {
            Toast.makeText(this, "Неверный ID компонента", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем экземпляр ApiService через ApiClient
        ApiService apiService = ApiClient.getApiService();

        // Отправка запроса на сервер для получения компонента по ID
        Call<Component> call = apiService.getComponentById(componentId);

        call.enqueue(new Callback<Component>() {
            @Override
            public void onResponse(Call<Component> call, Response<Component> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Component component = response.body();
                    populateUI(component);  // Заполняем UI данными компонента
                } else {
                    Toast.makeText(ComponentActivity.this, "Ошибка: Нет данных для компонента", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Component> call, Throwable t) {
                Log.e("API_ERROR", "Ошибка: " + t.getMessage());
                Toast.makeText(ComponentActivity.this, "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });

        // Обработка нажатия на кнопку для перехода на PDF
        pdf.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(documentationName), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        });

    }

    // Метод для заполнения UI данными компонента
    private void populateUI(Component component) {
        // Заполняем название компонента
        if (component.getTitle() != null && !component.getTitle().isEmpty()) {
            componentTitle.setText(component.getTitle());
        } else {
            componentTitle.setVisibility(View.GONE);  // Скрываем, если нет данных
        }

        // Заполняем описание компонента
        if (component.getDescription() != null && !component.getDescription().equals("-") && !component.getDescription().isEmpty()) {
            componentDescription.setText(component.getDescription());
        } else {
            componentDescription.setVisibility(View.GONE);  // Скрываем, если нет данных
        }

        // Заполняем имя документации
        documentationName = component.getDocumentationName();

        // Загружаем изображение компонента через Glide
        if (component.getComponentPhoto() != null && !component.getComponentPhoto().isEmpty()) {
            Glide.with(this)
                    .load(component.getComponentPhoto())  // URL изображения
                    .into(componentImage);
        } else {
            componentImage.setVisibility(View.GONE);  // Скрываем, если нет данных
        }

        // Заполняем свойства компонента
        if (component.getComponentProperties() != null && !component.getComponentProperties().isEmpty()) {
            propertiesWord.setVisibility(View.VISIBLE);
            for (Component.Property property : component.getComponentProperties()) {
                TextView propertyTextView = new TextView(this);
                propertyTextView.setText(property.getPropertyName() + ": " + property.getPropertyValue());
                propertyTextView.setTextColor(getResources().getColor(R.color.gray));
                propertyTextView.setTextSize(14f);
                propertiesContainer.addView(propertyTextView);  // Добавляем в контейнер свойств
            }
        } else {
            propertiesContainer.setVisibility(View.GONE);  // Скрываем, если нет данных
        }

        // Если документации нет, скрываем раздел
        if (documentationName != null || !documentationName.isEmpty()) {
            pdfLayout.setVisibility(View.VISIBLE);
        }
    }
}
