package com.example.gestipork_v2.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.gestipork_v2.data.ConstantesPrefs;
import com.example.gestipork_v2.modelo.Nota;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.NotasService;
import com.example.gestipork_v2.network.SupabaseConfig;
import com.example.gestipork_v2.repository.NotaRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SincronizadorNotas {

    public static void sincronizar(Context context, String authHeader, String apiKey) {
        NotaRepository repo = new NotaRepository(context);
        NotasService notasService = ApiClient.getClient().create(NotasService.class);
        SharedPreferences prefsSync = context.getSharedPreferences("syncPrefs", Context.MODE_PRIVATE);
        SharedPreferences prefsLogin = context.getSharedPreferences(ConstantesPrefs.PREFS_LOGIN, Context.MODE_PRIVATE);

        String idExplotacion = prefsLogin.getString("id_explotacion_actual", null);
        if (idExplotacion == null) {
            Log.w("SYNC_NOTAS", "No se encontró id_explotacion_actual");
            return;
        }

        // SUBIR notas no sincronizadas
        List<Nota> pendientes = repo.obtenerNotasNoSincronizadasPorExplotacion(idExplotacion);
        for (Nota nota : pendientes) {
            if (nota.getId() == null || nota.getId().isEmpty()) {
                nota.setId(java.util.UUID.randomUUID().toString());
            }

            nota.setFechaActualizacion(getFechaActual());

            Call<Void> call = notasService.insertarNota(nota, authHeader, apiKey);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        repo.marcarNotaComoSincronizada(nota.getId(), nota.getFechaActualizacion());
                        Log.d("SYNC_NOTAS", "✅ Nota subida: " + nota.getId());
                    } else {
                        Log.e("SYNC_NOTAS", "❌ Error Supabase al subir nota: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("SYNC_NOTAS", "❌ Error de red al subir nota: " + t.getMessage());
                }
            });
        }

        // DESCARGAR nuevas notas
        String ultimaFecha = prefsSync.getString("ultimaFechaNotas", "2025-01-01T00:00:00");

        Call<List<Nota>> call = notasService.getNotasModificadas(
                "gt." + ultimaFecha,
                "fecha_actualizacion.asc",
                0,
                authHeader,
                apiKey,
                SupabaseConfig.getContentType()
        );

        call.enqueue(new Callback<List<Nota>>() {
            @Override
            public void onResponse(Call<List<Nota>> call, Response<List<Nota>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Nota> nuevas = response.body();
                    for (Nota nota : nuevas) {
                        if (nota.getId_explotacion().equals(idExplotacion)) {
                            repo.insertarOActualizarNota(nota);
                            Log.d("SYNC_NOTAS", "⬇️ Nota insertada o actualizada: " + nota.getId());
                        }
                    }
                    prefsSync.edit().putString("ultimaFechaNotas", getFechaActual()).apply();
                } else {
                    Log.e("SYNC_NOTAS", "❌ Error al obtener notas: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Nota>> call, Throwable t) {
                Log.e("SYNC_NOTAS", "❌ Fallo al obtener notas: " + t.getMessage());
            }
        });
    }

    private static String getFechaActual() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                .format(new java.util.Date());
    }


}
