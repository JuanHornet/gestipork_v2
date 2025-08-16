package com.example.gestipork_v2.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.gestipork_v2.data.ConstantesPrefs;
import com.example.gestipork_v2.modelo.Conteo;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.ConteoService;
import com.example.gestipork_v2.network.SupabaseConfig;
import com.example.gestipork_v2.repository.ConteoRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SincronizadorConteos {

    public static void sincronizar(Context context, String authHeader, String apiKey) {
        ConteoRepository repo = new ConteoRepository(context);
        ConteoService service = ApiClient.getClient().create(ConteoService.class);
        SharedPreferences prefsSync = context.getSharedPreferences("syncPrefs", Context.MODE_PRIVATE);
        SharedPreferences prefsLogin = context.getSharedPreferences(ConstantesPrefs.PREFS_LOGIN, Context.MODE_PRIVATE);

        String idExplotacion = prefsLogin.getString("id_explotacion_actual", null);
        if (idExplotacion == null) {
            Log.w("SYNC_CONTEOS", "No se encontró id_explotacion_actual");
            return;
        }

        // SUBIDA
        List<Conteo> pendientes = repo.obtenerConteosNoSincronizadosPorExplotacion(idExplotacion);
        for (Conteo conteo : pendientes) {
            if (conteo.getId() == null || conteo.getId().isEmpty()) {
                conteo.setId(java.util.UUID.randomUUID().toString());
            }
            conteo.setFechaActualizacion(getFechaActual());

            Call<Void> call = service.insertarConteo(conteo, authHeader, apiKey);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        repo.marcarConteoComoSincronizado(conteo.getId(), conteo.getFechaActualizacion());
                        Log.d("SYNC_CONTEOS", "✅ Conteo subido: " + conteo.getId());
                    } else {
                        Log.e("SYNC_CONTEOS", "❌ Error Supabase al subir conteo: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("SYNC_CONTEOS", "❌ Error de red al subir conteo: " + t.getMessage());
                }
            });
        }

        // DESCARGA
        String ultimaFecha = prefsSync.getString("ultimaFechaConteos", "2025-01-01T00:00:00");

        Call<List<Conteo>> call = service.getConteosModificados(
                "gt." + ultimaFecha,
                "fecha_actualizacion.asc",
                0, // solo no eliminados
                authHeader,
                apiKey,
                SupabaseConfig.getContentType()
        );

        call.enqueue(new Callback<List<Conteo>>() {
            @Override
            public void onResponse(Call<List<Conteo>> call, Response<List<Conteo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Conteo> nuevos = response.body();
                    for (Conteo conteo : nuevos) {
                        if (conteo.getId_explotacion().equals(idExplotacion)) {
                            repo.insertarOActualizarConteo(conteo);
                            Log.d("SYNC_CONTEOS", "⬇️ Conteo insertado o actualizado: " + conteo.getId());
                        }
                    }
                    prefsSync.edit().putString("ultimaFechaConteos", getFechaActual()).apply();
                } else {
                    Log.e("SYNC_CONTEOS", "❌ Error al obtener conteos: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Conteo>> call, Throwable t) {
                Log.e("SYNC_CONTEOS", "❌ Fallo al obtener conteos: " + t.getMessage());
            }
        });
    }

    private static String getFechaActual() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
