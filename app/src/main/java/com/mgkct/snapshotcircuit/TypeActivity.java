package com.mgkct.snapshotcircuit;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TypeActivity extends AppCompatActivity {

    private TextView titleTextView;
    private ImageView imageView;
    private TextView descriptionTextView;
    private LinearLayout typesContainer;
    private TextView typeWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_type_activity);

        titleTextView = findViewById(R.id.title);
        imageView = findViewById(R.id.img);
        descriptionTextView = findViewById(R.id.description);
        typesContainer = findViewById(R.id.typesContainer);
        typeWord = findViewById(R.id.type);

        int position = getIntent().getIntExtra("position", -1);

        if (position == -1) {
            Toast.makeText(this, "Некорректный ID типа компонента", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<ComponentType> call = apiService.getComponentTypeById(position);
        call.enqueue(new Callback<ComponentType>() {
            @Override
            public void onResponse(Call<ComponentType> call, Response<ComponentType> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ComponentType componentType = response.body();

                    if (titleTextView != null) {
                        titleTextView.setText(componentType.getType_name());
                    }
                    if (descriptionTextView != null) {
                        descriptionTextView.setText(componentType.getType_description());
                    }

                    if (imageView != null) {
                        Glide.with(TypeActivity.this)
                                .load(componentType.getType_image())
                                .into(imageView);
                    }

                    typeWord.setText("Типы");

                    if (typesContainer != null) {
                        for (ComponentType.ComponentSubtype subtype : componentType.getSubtypes()) {
                            TextView typeTextView = new TextView(TypeActivity.this);
                            typeTextView.setText(subtype.getSubtype_name());
                            typeTextView.setTextColor(getResources().getColor(R.color.blue));
                            typeTextView.setTextSize(14);

                            LayoutParams layoutParams = new LayoutParams(
                                    LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT
                            );
                            layoutParams.setMargins(0, 0, 0, (int) getResources().getDisplayMetrics().density * 20); // 20dp
                            typeTextView.setLayoutParams(layoutParams);

                            typeTextView.setOnClickListener(view -> {
                                int subtypeId = subtype.getSubtype_id();
                                Intent intent = new Intent(TypeActivity.this, ComponentListActivity.class);
                                intent.putExtra("subtypeId", subtypeId);
                                startActivity(intent);
                            });

                            typesContainer.addView(typeTextView);
                        }
                    }
                } else {
                    Toast.makeText(TypeActivity.this, "Ошибка при получении данных", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ComponentType> call, Throwable t) {
                Toast.makeText(TypeActivity.this, "Ошибка на сервере", Toast.LENGTH_SHORT).show();
            }
        });
    }
}