package com.mgkct.snapshotcircuit;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComponentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ComponentAdapter adapter;
    private List<Component> componentList;
    private EditText searchEditText;
    private ImageView searchImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.components_list_activity);

        recyclerView = findViewById(R.id.items);
        searchEditText = findViewById(R.id.search);
        searchImage = findViewById(R.id.searchImage);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Получаем ID подтипа из Intent
        int subtypeId = getIntent().getIntExtra("subtypeId", -1);

        // Инициализация API и выполнение запроса
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<List<Component>> call = apiService.getComponentsBySubtypeId(subtypeId);

        call.enqueue(new Callback<List<Component>>() {
            @Override
            public void onResponse(Call<List<Component>> call, Response<List<Component>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    componentList = response.body();

                    // Инициализация адаптера и передача слушателя кликов
                    adapter = new ComponentAdapter(ComponentListActivity.this, componentList, componentId -> {
                        // Создаем Intent и передаем ID компонента
                        Intent intent = new Intent(ComponentListActivity.this, ComponentActivity.class);
                        intent.putExtra("componentId", componentId);
                        startActivity(intent);
                    });

                    recyclerView.setAdapter(adapter);

                    // Устанавливаем слушатель изменений в поле поиска
                    searchEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                            // Фильтруем список при изменении текста
                            adapter.filterList(charSequence.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                        }
                    });
                } else {
                    Toast.makeText(ComponentListActivity.this, "Нет данных для отображения", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Component>> call, Throwable t) {
                Log.e("API_ERROR", "Ошибка: " + t.getMessage());
                Toast.makeText(ComponentListActivity.this, "Ошибка подключения", Toast.LENGTH_SHORT).show();
            }
        });
    }
}