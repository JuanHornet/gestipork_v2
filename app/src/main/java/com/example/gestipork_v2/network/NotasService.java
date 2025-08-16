package com.example.gestipork_v2.network;

import com.example.gestipork_v2.modelo.Nota;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotasService {

    @Headers("Content-Type: application/json")
    @POST("notas")
    Call<Void> insertarNota(
            @Body Nota nota,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey
    );

    @GET("notas")
    Call<List<Nota>> getNotasModificadas(
            @Query("fecha_actualizacion") String filtroFecha,
            @Query("order") String orden,
            @Query("eliminado") int eliminado, // <-- Añadir este filtro
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );

    @retrofit2.http.DELETE("notas")
    Call<Void> eliminarNota(
            @retrofit2.http.Query("id") String id, // Usa "eq.{id}" si es necesario
            @retrofit2.http.Header("Authorization") String authHeader,
            @retrofit2.http.Header("apikey") String apiKey
    );

}

