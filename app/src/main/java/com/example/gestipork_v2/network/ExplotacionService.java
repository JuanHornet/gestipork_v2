package com.example.gestipork_v2.network;



import com.example.gestipork_v2.modelo.Explotacion;

import retrofit2.http.DELETE;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ExplotacionService {

    @PATCH("explotaciones")
    Call<Void> actualizarExplotacion(
            @Query("id") String id,  // SIN "eq." ni llaves aquí
            @Body Explotacion explotacion,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );


    @GET("explotaciones")
    Call<List<Explotacion>> obtenerExplotacionesPorUsuario(
            @Query("id_usuario") String uuidUsuario,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );

    @POST("explotaciones")
    Call<Void> insertarExplotacion(
            @Body Explotacion explotacion,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );

    @DELETE("explotaciones")
    Call<Void> eliminarExplotacion(
            @Query("id") String id,  // 🔄 Aquí pones "eq.{uuid}"
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );


}
