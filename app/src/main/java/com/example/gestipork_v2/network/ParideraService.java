package com.example.gestipork_v2.network;

import com.example.gestipork_v2.modelo.Parideras;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ParideraService {

    @Headers("Prefer: resolution=merge-duplicates")
    @POST("parideras?on_conflict=id")
    Call<Void> insertarParidera(
            @Body Parideras paridera,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey
    );
}
