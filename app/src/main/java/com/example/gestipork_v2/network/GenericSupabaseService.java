package com.example.gestipork_v2.network;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GenericSupabaseService {

    @PATCH
    Call<Void> actualizarRegistro(
            @Url String url, // URL completa con filtro
            @Body Map<String, Object> body,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType,
            @Header("Prefer") String prefer // Nueva cabecera necesaria para Supabase
    );

}
