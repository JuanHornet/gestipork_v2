package com.example.gestipork_v2.network;

import com.example.gestipork_v2.modelo.tabs.Accion;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.DELETE;
import retrofit2.http.Url;

public interface AccionService {

    @GET("acciones")
    Call<List<Accion>> getAccionesModificadas(
            @Query("fecha_actualizacion") String filtroFecha,
            @Query("order") String orden,
            @Query("eliminado") int eliminado,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );

    @GET("acciones")
    Call<List<Accion>> obtenerAcciones(
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );

    @POST("acciones")
    Call<Void> insertarAcciones(
            @Body List<Accion> acciones,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );

    @PATCH("acciones")
    Call<Void> actualizarAccion(
            @Body Map<String, Object> body,
            @Query("id") String eq,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );

    @DELETE
    Call<Void> eliminarAccion(
            @Url String url,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );
}
