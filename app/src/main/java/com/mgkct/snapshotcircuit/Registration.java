package com.mgkct.snapshotcircuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import retrofit2.Call;

public class Registration extends AppCompatActivity {

    private boolean isToastShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        TextView reg = findViewById(R.id.reg_textView);
        EditText input_password = findViewById(R.id.password);
        EditText input_copy_password = findViewById(R.id.copy_password);
        EditText input_email = findViewById(R.id.email);

        reg.setOnClickListener(v -> finish());

        Button sign_in_button = findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(v -> {
            String email = input_email.getText().toString();
            String password = input_password.getText().toString();
            String copyPassword = input_copy_password.getText().toString();

            if (!isToastShown && (email.isEmpty() || password.isEmpty() || copyPassword.isEmpty())) {
                showToast("Заполните все поля!");
            } else if (!isToastShown && !isValidEmail(email)) {
                showToast("Введите корректную почту!");
            } else if (!isToastShown && password.length() < 8) {
                showToast("Пароль должен быть не менее 8 символов!");
            } else if (!isToastShown && !password.equals(copyPassword)) {
                showToast("Пароли не совпадают!");
            } else {
                // Регистрация пользователя
                registerUser(email, password);
            }

            // Сброс флага после задержки
            new Handler().postDelayed(() -> isToastShown = false, 2500);
        });

        ImageView visible_button = findViewById(R.id.visibility);
        visible_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_password.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    visible_button.setImageResource(R.drawable.open);
                    input_password.setTransformationMethod(null);
                }
                else {
                    visible_button.setImageResource(R.drawable.close);
                    input_password.setTransformationMethod(new PasswordTransformationMethod());
                }
                input_password.setSelection(input_password.getText().length());
                input_password.requestFocus();
            }
        });

        ImageView copy_visible_button = findViewById(R.id.copy_visibility);
        copy_visible_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_copy_password.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    copy_visible_button.setImageResource(R.drawable.open);
                    input_copy_password.setTransformationMethod(null);
                }
                else {
                    copy_visible_button.setImageResource(R.drawable.close);
                    input_copy_password.setTransformationMethod(new PasswordTransformationMethod());
                }
                input_copy_password.setSelection(input_copy_password.getText().length());
                input_copy_password.requestFocus();
            }
        });
    }

    private void showToast(String message) {
        isToastShown = true;
        Toast.makeText(Registration.this, message, Toast.LENGTH_SHORT).show();
    }

    // Проверка формата email
    public static boolean isValidEmail(String validEmail) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(validEmail).matches();
    }

    private void registerUser(String email, String password) {
        ApiService apiService = ApiClient.getApiService();
        RegisterRequest request = new RegisterRequest(email, password);

        // Выполнение запроса
        apiService.registerUser(request).enqueue(new retrofit2.Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, retrofit2.Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    if (registerResponse.isSuccess()) {
                        Toast.makeText(Registration.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
                        finish(); // Закрыть активность после успешной регистрации
                    } else {
                        Toast.makeText(Registration.this, registerResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Registration.this, "Ошибка регистрации!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(Registration.this, "Ошибка подключения: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
