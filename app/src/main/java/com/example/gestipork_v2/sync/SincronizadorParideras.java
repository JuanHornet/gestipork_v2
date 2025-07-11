package com.example.gestipork_v2.sync;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.Parideras;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.ParideraService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class SincronizadorParideras {

    private static final String TAG = "SINCRONIZADOR_PARIDERAS";

    public static void sincronizar(Context context, String authHeader, String apiKey) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM parideras WHERE sincronizado = 0", null);

        while (cursor.moveToNext()) {
            Parideras paridera = new Parideras(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("nacidosVivos")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("nParidas")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("nVacias")),
                    cursor.getString(cursor.getColumnIndexOrThrow("cod_paridera")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_lote")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fechaInicioParidera")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fechaFinParidera")),
                    0,
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion"))
            );


            ParideraService service = ApiClient.getClient().create(ParideraService.class);
            Call<Void> call = service.insertarParidera(paridera, authHeader, apiKey);

            try {
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    dbHelper.marcarParideraComoSincronizada(db, paridera.getId());
                    Log.d(TAG, "✅ Paridera sincronizada: " + paridera.getCod_paridera());
                } else {
                    Log.e(TAG, "❌ Error al sincronizar paridera: " + response.code());
                }
            } catch (IOException e) {
                Log.e(TAG, "❌ Excepción al sincronizar paridera", e);
            }
        }

        cursor.close();
        db.close();
    }
}
