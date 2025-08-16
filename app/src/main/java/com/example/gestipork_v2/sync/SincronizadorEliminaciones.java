package com.example.gestipork_v2.sync;

import android.content.Context;
import android.util.Log;

import com.example.gestipork_v2.modelo.EliminacionPendiente;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.GenericSupabaseService;
import com.example.gestipork_v2.network.SupabaseConfig;
import com.example.gestipork_v2.repository.EliminacionRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SincronizadorEliminaciones {

    private final Context context;
    private final EliminacionRepository eliminacionRepository;

    public SincronizadorEliminaciones(Context context) {
        this.context = context;
        this.eliminacionRepository = new EliminacionRepository(context);
    }

    public void sincronizarEliminaciones() {
        List<EliminacionPendiente> pendientes = eliminacionRepository.obtenerEliminacionesNoSincronizadas();

        for (EliminacionPendiente ep : pendientes) {
            String fechaEliminado = ep.getFechaEliminado();
            if (fechaEliminado == null || fechaEliminado.isEmpty()) {
                fechaEliminado = getFechaActual();
            }

            Map<String, Object> body = new HashMap<>();
            body.put("eliminado", 1);
            body.put("fecha_eliminado", fechaEliminado);
            body.put("fecha_actualizacion", fechaEliminado);

            String url = SupabaseConfig.getBaseUrl() + "/" + ep.getTabla() + "?id=eq." + ep.getIdRegistro();

            Call<Void> call = ApiClient.getClient()
                    .create(GenericSupabaseService.class)
                    .actualizarRegistro(
                            url,
                            body,
                            SupabaseConfig.getAuthHeader(),
                            SupabaseConfig.getApiKey(),
                            SupabaseConfig.getContentType(),
                            "return=representation"
                    );


            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        eliminacionRepository.eliminarEliminacionPendiente(ep.getId());

                        Log.d("SYNC_ELIM", "✅ Eliminación sincronizada: " + ep.getTabla() + ", id: " + ep.getIdRegistro());
                    } else {
                        Log.e("SYNC_ELIM", "❌ Error al sincronizar (" + response.code() + "): " + ep.getTabla()
                                + ", id: " + ep.getIdRegistro());
                        try {
                            Log.e("SYNC_ELIM", "Detalle error: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("SYNC_ELIM", "Error leyendo errorBody: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("SYNC_ELIM", "❌ Error de red al sincronizar: " + t.getMessage());
                }
            });
        }
    }

    private String getFechaActual() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());
    }
    public void sincronizarEliminacionInmediata(String tabla, String idRegistro, String fechaEliminado) {
        Map<String, Object> body = new HashMap<>();
        body.put("eliminado", 1);
        body.put("fecha_eliminado", fechaEliminado);
        body.put("fecha_actualizacion", fechaEliminado);

        String url = SupabaseConfig.getBaseUrl() + "/" + tabla + "?id=eq." + idRegistro;

        Call<Void> call = ApiClient.getClient()
                .create(GenericSupabaseService.class)
                .actualizarRegistro(
                        url,
                        body,
                        SupabaseConfig.getAuthHeader(),
                        SupabaseConfig.getApiKey(),
                        SupabaseConfig.getContentType(),
                        "return=representation"
                );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("SYNC_ELIM", "✅ Eliminación inmediata sincronizada: notas, id: " + idRegistro);
                } else {
                    Log.e("SYNC_ELIM", "❌ PATCH falló (" + response.code() + "): id: " + idRegistro);
                    try {
                        Log.e("SYNC_ELIM", "Detalle: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("SYNC_ELIM", "Error leyendo errorBody: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("SYNC_ELIM", "❌ Error de red PATCH: " + t.getMessage());
            }
        });
    }

}
