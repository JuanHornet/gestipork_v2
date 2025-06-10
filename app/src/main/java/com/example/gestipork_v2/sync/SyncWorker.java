package com.example.gestipork_v2.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("SYNC_WORKER", "Sincronización iniciada por WorkManager");
        new SincronizadorLotes(getApplicationContext()).sincronizarLotes();

        try {
            SincronizadorGeneral sincronizador = new SincronizadorGeneral(getApplicationContext());
            sincronizador.sincronizarTodo();  // De momento solo sincroniza Lotes
            return Result.success();
        } catch (Exception e) {
            Log.e("SYNC_WORKER", "Error al sincronizar: " + e.getMessage(), e);
            return Result.failure();
        }
    }
}
