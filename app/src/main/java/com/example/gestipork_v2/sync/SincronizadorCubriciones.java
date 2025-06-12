package com.example.gestipork_v2.sync;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.Cubriciones;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.CubricionService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SincronizadorCubriciones {

    private static final String TAG = "SINCRONIZADOR_CUBRICIONES";

    public static void sincronizar(Context context, String authHeader, String apiKey) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM cubriciones WHERE sincronizado = 0", null);
        if (cursor.moveToFirst()) {
            do {
                Cubriciones c = DBHelper.cursorACubricion(cursor); // Asegúrate de tener este método

                try {
                    CubricionService service = ApiClient.getClient().create(CubricionService.class);
                    Call<Void> call = service.insertarCubricion(c, authHeader, apiKey); // 👈 ahora con headers

                    Response<Void> response = call.execute();
                    if (response.isSuccessful()) {
                        ContentValues values = new ContentValues();
                        values.put("sincronizado", 1);
                        db.update("cubriciones", values, "id = ?", new String[]{c.getId()});
                        Log.d(TAG, "✅ Cubrición subida: " + c.getCod_cubricion());
                    } else {
                        Log.e(TAG, "❌ Error Supabase al subir cubrición: " + response.code());
                    }

                } catch (Exception e) {
                    Log.e(TAG, "❌ Error al sincronizar cubrición", e);
                }

            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No hay cubriciones nuevas que sincronizar.");
        }

        cursor.close();
        db.close();
    }

}
