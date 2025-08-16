package com.example.gestipork_v2.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.gestipork_v2.network.SupabaseConfig;
import com.example.gestipork_v2.repository.CubricionRepository;
import com.example.gestipork_v2.repository.ItacaRepository;
import com.example.gestipork_v2.repository.ParideraRepository;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("SYNC_WORKER", "Sincronización iniciada por WorkManager");

        try {
            // 🔁 Sincronización general
            new SincronizadorLotes(getApplicationContext()).sincronizarLotes();
            new SincronizadorGeneral(getApplicationContext()).sincronizarTodo();

            String token = SupabaseConfig.getAuthHeader().replace("Bearer ", "");
            String apiKey = SupabaseConfig.getApiKey();

            Log.d("SYNC_WORKER", "🔐 Token: " + token);
            Log.d("SYNC_WORKER", "🔐 API Key: " + apiKey);

            if (token != null && apiKey != null) {
                String authHeader = "Bearer " + token;

                new CubricionRepository(getApplicationContext()).subirCubricionesNoSincronizadas(authHeader, apiKey);
                new ParideraRepository(getApplicationContext()).subirPariderasNoSincronizadas(authHeader, apiKey);
                new ItacaRepository(getApplicationContext()).subirItacasNoSincronizadas(authHeader, apiKey);

                SincronizadorNotas.sincronizar(getApplicationContext(), authHeader, apiKey);

                Log.d("SYNC_WORKER", "🔁 Repositorio Notas ejecutado");

                // ✅ Sincronizador de conteos
                SincronizadorConteos.sincronizar(getApplicationContext(), authHeader, apiKey);

                Log.d("SYNC_WORKER", "📋 Sincronizador de conteos ejecutado");

                // ✅ Sincronizador de eliminaciones
                new SincronizadorEliminaciones(getApplicationContext()).sincronizarEliminaciones();
                Log.d("SYNC_WORKER", "🗑️ Sincronizador de eliminaciones ejecutado");

            } else {
                Log.e("SYNC_WORKER", "❌ Token o API Key no disponibles.");
            }

            return Result.success();
        } catch (Exception e) {
            Log.e("SYNC_WORKER", "Error al sincronizar: " + e.getMessage(), e);
            return Result.failure();
        }
    }
}
