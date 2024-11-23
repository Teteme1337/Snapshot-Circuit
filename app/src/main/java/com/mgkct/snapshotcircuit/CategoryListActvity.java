package com.mgkct.snapshotcircuit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryListActvity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<CategoryItem> categoryItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_adapter_activity);

        recyclerView = findViewById(R.id.items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryItemList = new ArrayList<>();

        // Получаем экземпляр ApiService через ApiClient
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Запрос к серверу
        Call<List<CategoryResponse>> call = apiService.getCategories();
        call.enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryResponse> categories = response.body();

                    // Преобразуем ответ сервера в список CategoryItem
                    for (CategoryResponse category : categories) {
                        categoryItemList.add(new CategoryItem(category.getType_name(), category.getType_image()));
                    }

                    // Уведомляем адаптер об изменении данных
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CategoryListActvity.this, "Не удалось загрузить категории", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                Toast.makeText(CategoryListActvity.this, "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show();
            }
        });

        // Настройка адаптера для RecyclerView
        adapter = new CategoryAdapter(this, categoryItemList);
        recyclerView.setAdapter(adapter);

        // Обработчик кликов по элементам
        adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(CategoryListActvity.this, TypeActivity.class);
                intent.putExtra("position", position + 1); // Передаем ID категории
                startActivity(intent);
            }
        });
    }
}