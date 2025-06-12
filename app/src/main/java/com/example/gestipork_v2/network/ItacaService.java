package com.example.gestipork_v2.network;

import com.example.gestipork_v2.modelo.Itaca;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ItacaService {

    @Headers("Content-Type: application/json")
    @POST("itaca")
    Call<Void> insertarItaca(
            @Body Itaca itaca,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey
    );
}
