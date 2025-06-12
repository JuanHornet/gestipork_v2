package com.example.gestipork_v2.network;

import com.example.gestipork_v2.modelo.Lotes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface LoteService {

    // Obtener lotes modificados después de una fecha
    @GET("lotes")
    Call<List<Lotes>> getLotesModificados(
            @Query("fecha_actualizacion") String filtroFecha,   // ejemplo: "gt.2024-01-01T00:00:00"
            @Query("order") String orden,                       // ejemplo: "fecha_actualizacion.asc"
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType          // Agregado para asegurar formato
    );

    // Insertar lote nuevo
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @POST("lotes")
    Call<Void> insertarLote(
            @Body Lotes lote,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey
    );

    // Actualizar lote por ID
    @Headers("Content-Type: application/json")
    @PATCH("lotes")
    Call<Void> actualizarLote(
            @Query("id") String filtroId,                      // ejemplo: "eq.5"
            @Body Lotes lote,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey
    );
}
