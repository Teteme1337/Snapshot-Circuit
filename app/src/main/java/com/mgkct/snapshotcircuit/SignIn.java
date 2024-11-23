package com.mgkct.snapshotcircuit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import retrofit2.Call;

public class SignIn extends AppCompatActivity {

    private boolean isToastShown = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        sharedPreferences = getSharedPreferences("Config", MODE_PRIVATE);

        TextView reg = findViewById(R.id.reg_textView);
        EditText input_password = findViewById(R.id.password);
        EditText input_email = findViewById(R.id.email);

        reg.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this, Registration.class);
            startActivity(intent);
        });

        Button sign_in_button = findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(v -> {
            String email = input_email.getText().toString();
            String password = input_password.getText().toString();

            if (!isToastShown && (email.isEmpty() || password.isEmpty())) {
                showToast("Заполните все поля!");
            } else if (!isToastShown && !isValidEmail(email)) {
                showToast("Введите корректную почту!");
            } else if (!isToastShown && password.length() < 8) {
                showToast("Пароль должен быть не менее 8 символов!");
            } else {
                if (email.equals("admin@gmail.com") && password.equals("admin123")) {
                    Intent intent = new Intent(SignIn.this, WebActivity.class);
                    intent.putExtra("path","https://server-for-snapshot-circuit-admin.onrender.com");
                    startActivity(intent);
                } else {
                    // Выполнить вход как пользователь
                    loginUser(email, password);
                }
            }

            // Сброс флага
            new Handler().postDelayed(() -> isToastShown = false, 2500);
        });

        ImageView visible_button = findViewById(R.id.visibility);
        visible_button.setOnClickListener(v -> {
            if (input_password.getTransformationMethod() instanceof PasswordTransformationMethod) {
                visible_button.setImageResource(R.drawable.open);
                input_password.setTransformationMethod(null);
            } else {
                visible_button.setImageResource(R.drawable.close);
                input_password.setTransformationMethod(new PasswordTransformationMethod());
            }
            input_password.setSelection(input_password.getText().length());
            input_password.requestFocus();
        });
    }

    private void loginUser(String email, String password) {
        ApiService apiService = ApiClient.getApiService();
        LoginRequest request = new LoginRequest(email, password);

        // Выполнение запроса
        apiService.loginUser(request).enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putInt("myId", loginResponse.getUserId());
                        editor.apply();

                        Toast.makeText(SignIn.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        // Если в ответе есть сообщение об ошибке, проверяем его
                        String message = loginResponse.getMessage();
                        if (message == null || message.isEmpty()) {
                            Toast.makeText(SignIn.this, "Неверный логин или пароль!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(SignIn.this, "Неверный логин или пароль!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(SignIn.this, "Ошибка подключения: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToast(String message) {
        isToastShown = true;
        Toast.makeText(SignIn.this, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isValidEmail(String validEmail) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(validEmail).matches();
    }
}



