package com.example.gestipork_v2.network;

import com.example.gestipork_v2.modelo.Conteo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ConteoService {

    @Headers("Content-Type: application/json")
    @POST("contar")
    Call<Void> insertarConteo(
            @Body Conteo conteo,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey
    );

    @GET("contar")
    Call<List<Conteo>> getConteosModificados(
            @Query("fecha_actualizacion") String filtroFecha,
            @Query("order") String orden,
            @Query("eliminado") int eliminado, // 👈 Añadido para filtrar por eliminados
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );

}
