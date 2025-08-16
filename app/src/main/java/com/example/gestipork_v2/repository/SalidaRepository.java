package com.example.gestipork_v2.repository;

import android.content.Context;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.SalidasExplotacion;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.SalidasService;
import com.example.gestipork_v2.network.SupabaseConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalidaRepository {

    private final DBHelper dbHelper;
    private final SalidasService salidasService;
    private final Context context;
    private final String TAG = "SalidaRepository";

    public SalidaRepository(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
        this.salidasService = ApiClient.getClient().create(SalidasService.class);
    }

    public void subirSalidasNoSincronizadas() {
        List<SalidasExplotacion> salidas = dbHelper.obtenerSalidasNoSincronizadas();

        if (salidas.isEmpty()) return;

        Call<Void> call = salidasService.insertarSalidas(
                salidas,
                SupabaseConfig.getAuthHeader(),
                SupabaseConfig.getApiKey(),
                SupabaseConfig.getContentType()

        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Salidas subidas con éxito");
                    for (SalidasExplotacion salida : salidas) {
                        dbHelper.marcarSalidaComoSincronizada(salida.getId());
                    }
                } else {
                    Log.e(TAG, "Error al subir salidas: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fallo de red al subir salidas: ", t);
            }
        });
    }

    public void descargarSalidasModificadas(String ultimaFecha) {
        Call<List<SalidasExplotacion>> call = salidasService.getSalidasModificadas(
                "gte." + ultimaFecha,
                "fecha_actualizacion.asc",
                0,
                SupabaseConfig.getAuthHeader(),
                SupabaseConfig.getApiKey(),
                SupabaseConfig.getContentType()

        );

        call.enqueue(new Callback<List<SalidasExplotacion>>() {
            @Override
            public void onResponse(Call<List<SalidasExplotacion>> call, Response<List<SalidasExplotacion>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (SalidasExplotacion salida : response.body()) {
                        dbHelper.insertarOActualizarSalidaDesdeServidor(salida);
                    }
                    Log.i(TAG, "Salidas sincronizadas desde Supabase");
                } else {
                    Log.e(TAG, "Error al descargar salidas: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<SalidasExplotacion>> call, Throwable t) {
                Log.e(TAG, "Fallo de red al descargar salidas: ", t);
            }
        });
    }

    public void marcarSalidaEliminadaEnSupabase(String id, String fechaEliminado) {
        Map<String, Object> body = new HashMap<>();
        body.put("eliminado", 1);
        body.put("fecha_eliminado", fechaEliminado);
        body.put("fecha_actualizacion", fechaEliminado);

        Call<Void> call = salidasService.actualizarSalida(
                body,
                "eq." + id,
                SupabaseConfig.getAuthHeader(),
                SupabaseConfig.getApiKey(),
                SupabaseConfig.getContentType()

        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Salida marcada como eliminada en Supabase");
                } else {
                    Log.e(TAG, "Error al marcar salida como eliminada: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Fallo al eliminar salida en Supabase", t);
            }
        });
    }
}
