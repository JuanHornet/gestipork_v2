package com.example.gestipork_v2.network;

import com.example.gestipork_v2.modelo.Cubriciones;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CubricionService {

    @Headers({
            "Content-Type: application/json",
            "Prefer: resolution=merge-duplicates"
    })
    @POST("cubriciones?on_conflict=id")
    Call<Void> insertarCubricion(
            @Body Cubriciones cubricion,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey
    );


    @GET("cubriciones")
    Call<List<Cubriciones>> getCubricionesModificadas(
            @Query("fecha_actualizacion") String filtroFecha,     // ej: "gt.2024-01-01T00:00:00"
            @Query("order") String orden,                         // ej: "fecha_actualizacion.asc"
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );
}
