package com.example.gestipork_v2.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.gestipork_v2.data.ConstantesPrefs;
import com.example.gestipork_v2.modelo.tabs.Accion;
import com.example.gestipork_v2.network.AccionService;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.SupabaseConfig;
import com.example.gestipork_v2.repository.AccionRepository;
import com.example.gestipork_v2.base.FechaUtils;


import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class SincronizadorAcciones {

    public static void sincronizar(Context context, String authHeader, String apiKey) {
        AccionRepository repo = new AccionRepository(context);
        AccionService service = ApiClient.getClient().create(AccionService.class);

        SharedPreferences prefsLogin = context.getSharedPreferences(ConstantesPrefs.PREFS_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences prefsSync = context.getSharedPreferences("syncPrefs", Context.MODE_PRIVATE);

        String idExplotacion = prefsLogin.getString("id_explotacion_actual", null);
        if (idExplotacion == null) {
            Log.w("SYNC_ACCIONES", "No se encontró id_explotacion_actual");
            return;
        }

        // ✅ SUBIDA de acciones locales no sincronizadas
        List<Accion> pendientes = repo.obtenerAccionesNoSincronizadasPorExplotacion(idExplotacion);
        for (Accion accion : pendientes) {
            if (accion.getId() == null || accion.getId().isEmpty()) {
                accion.setId(java.util.UUID.randomUUID().toString());
            }

            accion.setFechaActualizacion(FechaUtils.obtenerFechaActual());

            boolean esNueva = accion.getSincronizado() == 0 && accion.getFecha() != null && accion.getFecha().length() > 6;

            // Asumimos que ya fue subida si tiene ID y simplemente fue modificada → usamos PATCH
            if (!esNueva) {
                // PATCH → actualizar en Supabase
                java.util.Map<String, Object> body = new java.util.HashMap<>();
                body.put("tipoAccion", accion.getTipo());
                body.put("fechaAccion", accion.getFecha());
                body.put("nAnimales", accion.getCantidad());
                body.put("observacion", accion.getObservaciones());
                body.put("fecha_actualizacion", accion.getFechaActualizacion());

                Call<Void> patchCall = service.actualizarAccion(
                        body,
                        "eq." + accion.getId(),
                        SupabaseConfig.getAuthHeader(),
                        SupabaseConfig.getApiKey(),
                        SupabaseConfig.getContentType()
                );

                patchCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            repo.marcarAccionComoSincronizada(accion.getId(), accion.getFechaActualizacion());
                            Log.d("SYNC_ACCIONES", "🟡 Acción actualizada: " + accion.getId());
                        } else {
                            Log.e("SYNC_ACCIONES", "❌ Error PATCH acción: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("SYNC_ACCIONES", "❌ Error red PATCH acción: " + t.getMessage());
                    }
                });

            } else {
                // POST → insertar nueva acción
                Call<Void> callInsert = service.insertarAcciones(
                        Collections.singletonList(accion),
                        SupabaseConfig.getAuthHeader(),
                        SupabaseConfig.getApiKey(),
                        SupabaseConfig.getContentType()
                );

                callInsert.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            repo.marcarAccionComoSincronizada(accion.getId(), accion.getFechaActualizacion());
                            Log.d("SYNC_ACCIONES", "🟢 Acción subida nueva: " + accion.getId());
                        } else {
                            Log.e("SYNC_ACCIONES", "❌ Error POST acción: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("SYNC_ACCIONES", "❌ Error red POST acción: " + t.getMessage());
                    }
                });
            }
        }
    }


}
