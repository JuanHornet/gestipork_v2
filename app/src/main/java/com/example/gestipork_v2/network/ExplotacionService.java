package com.example.gestipork_v2.network;



import com.example.gestipork_v2.modelo.Explotacion;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ExplotacionService {

    @POST("explotaciones")
    Call<Void> insertarExplotacion(
            @Body Explotacion explotacion,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );
}
