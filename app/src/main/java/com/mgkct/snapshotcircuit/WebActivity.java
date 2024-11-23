package com.mgkct.snapshotcircuit;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);

        WebView webView = findViewById(R.id.WebView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Включаем поддержку JavaScript
        webSettings.setDomStorageEnabled(true); // Включаем DOM-хранилище
        webSettings.setAllowFileAccess(true);   // Разрешаем доступ к файлам

        webView.setWebViewClient(new WebViewClient());

        String path = getIntent().getStringExtra("path");

        webView.loadUrl(path);
    }
}