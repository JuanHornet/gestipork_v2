package com.example.gestipork_v2.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.gestipork_v2.modelo.Lotes;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.LoteService;
import com.example.gestipork_v2.network.SupabaseConfig;
import com.example.gestipork_v2.repository.LoteRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SincronizadorLotes {

    private final Context context;
    private final LoteService loteService;
    private final LoteRepository loteRepository;
    private final SharedPreferences prefs;

    public SincronizadorLotes(Context context) {
        this.context = context;
        this.loteService = ApiClient.getClient().create(LoteService.class);
        this.loteRepository = new LoteRepository(context);
        this.prefs = context.getSharedPreferences("syncPrefs", Context.MODE_PRIVATE);
    }

    public void sincronizarLotes() {
        subirLotesPendientes();
        descargarLotesNuevos();
    }

    private void subirLotesPendientes() {
        List<Lotes> lotes = loteRepository.obtenerLotesNoSincronizados();
        for (Lotes lote : lotes) {
            lote.setFecha_actualizacion(getFechaActual());

            Call<Void> call = loteService.insertarLote(
                    lote,
                    SupabaseConfig.getAuthHeader(),
                    SupabaseConfig.getApiKey()
            );

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        loteRepository.marcarLoteComoSincronizado(lote.getId(), lote.getFecha_actualizacion());
                        Log.d("SINCRONIZADOR_LOTES", "✅ Lote subido y sincronizado: " + lote.getId());
                    } else {
                        Log.e("SINCRONIZADOR_LOTES", "❌ Error Supabase al subir lote: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("SINCRONIZADOR_LOTES", "❌ Error de red al subir lote: " + t.getMessage());
                }
            });
        }
    }

    private void descargarLotesNuevos() {
        String ultimaFecha = prefs.getString("ultimaFechaLotes", "2025-01-01T00:00:00");

        Call<List<Lotes>> call = loteService.getLotesModificados(
                "gt." + ultimaFecha,
                "fecha_actualizacion.asc",
                SupabaseConfig.getAuthHeader(),
                SupabaseConfig.getApiKey(),
                SupabaseConfig.getContentType()
        );


        call.enqueue(new Callback<List<Lotes>>() {
            @Override
            public void onResponse(Call<List<Lotes>> call, Response<List<Lotes>> response) {
                if (response.isSuccessful()) {
                    List<Lotes> nuevos = response.body();
                    for (Lotes lote : nuevos) {
                        loteRepository.insertarOActualizarLote(lote);
                        Log.d("SINCRONIZADOR_LOTES", "⬇️ Lote actualizado o insertado: " + lote.getId());
                    }

                    prefs.edit().putString("ultimaFechaLotes", getFechaActual()).apply();
                    Log.d("SINCRONIZADOR_LOTES", "✅ Sincronización de descarga completada");
                } else {
                    Log.e("SINCRONIZADOR_LOTES", "❌ Error al obtener lotes: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Lotes>> call, Throwable t) {
                Log.e("SINCRONIZADOR_LOTES", "❌ Fallo al obtener lotes: " + t.getMessage());
            }
        });
    }

    private String getFechaActual() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

}
