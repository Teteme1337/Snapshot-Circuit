package com.mgkct.snapshotcircuit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ApiService {
    @GET("/componentType")
    Call<List<CategoryResponse>> getCategories();

    @GET("/componentType/{id}")
    Call<ComponentType> getComponentTypeById(@Path("id") int id);

    @GET("/components/{subtypeId}")
    Call<List<Component>> getComponentsBySubtypeId(@Path("subtypeId") int subtypeId);

    @GET("/component/{id}")
    Call<Component> getComponentById(@Path("id") int componentId);

    @POST("/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

    @POST("/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @POST("find-most-similar")
    Call<Integer> findMostSimilarComponent(@Body ImageRequest imageRequest);
}