package com.example.gestipork_v2.network;


import com.example.gestipork_v2.modelo.Cubriciones;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CubricionService {

    @Headers("Content-Type: application/json")
    @POST("cubriciones")
    Call<Void> insertarCubricion(
            @Body Cubriciones cubricion,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey
    );
}
