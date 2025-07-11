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

        // Sincroniza lotes
        new SincronizadorLotes(getApplicationContext()).sincronizarLotes();

        try {
            // Puedes ampliar aquí más sincronizadores
            SincronizadorGeneral sincronizador = new SincronizadorGeneral(getApplicationContext());
            sincronizador.sincronizarTodo();

            // Recupera token y API key directamente desde SupabaseConfig
            String token = SupabaseConfig.getAuthHeader().replace("Bearer ", "");
            String apiKey = SupabaseConfig.getApiKey();

            Log.d("SYNC_WORKER", "🔐 Token leído desde SupabaseConfig: " + token);
            Log.d("SYNC_WORKER", "🔐 API Key leída desde SupabaseConfig: " + apiKey);

            if (token != null && apiKey != null) {
                String authHeader = "Bearer " + token;

                // Sincroniza cubriciones
                CubricionRepository repoCubricion = new CubricionRepository(getApplicationContext());
                repoCubricion.subirCubricionesNoSincronizadas(authHeader, apiKey);

                // ✅ Sincroniza parideras
                ParideraRepository repoParidera = new ParideraRepository(getApplicationContext());
                repoParidera.subirPariderasNoSincronizadas(authHeader, apiKey);
                Log.d("SYNC_WORKER", "🔁 Repositorio Parideras ejecutado");

                // ✅ Sincroniza itaca
                ItacaRepository repoItaca = new ItacaRepository(getApplicationContext());
                repoItaca.subirItacasNoSincronizadas(authHeader, apiKey);
                Log.d("SYNC_WORKER", "🔁 Repositorio Itaca ejecutado");



                // Aquí puedes añadir más sincronizadores (parideras, itaca, etc.)
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
