package com.example.gestipork_v2.network;

import com.example.gestipork_v2.modelo.Itaca;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ItacaService {

    @Headers({
            "Content-Type: application/json",
            "Prefer: resolution=merge-duplicates"
    })
    @POST("itaca?on_conflict=id")
    Call<Void> insertarItaca(
            @Body Itaca itaca,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey
    );

    @GET("itaca")
    Call<List<Itaca>> getItacasModificadas(
            @Query("fecha_actualizacion") String filtroFecha,
            @Query("order") String orden,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );
}
