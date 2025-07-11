package com.example.gestipork_v2.sync;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.Itaca;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.ItacaService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class SincronizadorItaca {

    private static final String TAG = "SINCRONIZADOR_ITACA";

    public static void sincronizar(Context context, String authHeader, String apiKey) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM itaca WHERE sincronizado = 0", null);

        while (cursor.moveToNext()) {
            Itaca itaca = new Itaca(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("cod_itaca")),
                    "", // DCER no se está usando ahora (valor temporal vacío)
                    cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("nMadres")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("nPadres")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fechaPNacimiento")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fechaUltNacimiento")),
                    cursor.getString(cursor.getColumnIndexOrThrow("raza")),
                    cursor.getString(cursor.getColumnIndexOrThrow("color")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("crotalesSolicitados")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_lote")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")),
                    0,
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion"))
            );


            ItacaService service = ApiClient.getClient().create(ItacaService.class);
            Call<Void> call = service.insertarItaca(itaca, authHeader, apiKey);

            try {
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    dbHelper.marcarItacaComoSincronizada(db, itaca.getId());
                    Log.d(TAG, "✅ Itaca sincronizada: " + itaca.getCod_itaca());
                } else {
                    Log.e(TAG, "❌ Error al sincronizar itaca: " + response.code());
                }
            } catch (IOException e) {
                Log.e(TAG, "❌ Excepción al sincronizar itaca", e);
            }
        }

        cursor.close();
        db.close();
    }
}
