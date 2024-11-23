package com.mgkct.snapshotcircuit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://server-for-snapshot-circuit-2g7i.onrender.com/";
    private static Retrofit retrofit;

    // Метод для получения инстанса Retrofit
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Метод для получения ApiService
    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }

    public static String getBASE_URL(){
        return BASE_URL;
    }
}