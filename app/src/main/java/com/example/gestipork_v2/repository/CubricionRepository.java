package com.example.gestipork_v2.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.Cubriciones;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.CubricionService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CubricionRepository {

    private final DBHelper dbHelper;
    private static final String TAG = "CUBRICION_REPO";

    public CubricionRepository(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<Cubriciones> obtenerCubricionesNoSincronizadas() {
        List<Cubriciones> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM cubriciones WHERE sincronizado = 0", null);
        while (cursor.moveToNext()) {
            Cubriciones c = new Cubriciones();
            c.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            c.setCod_cubricion(cursor.getString(cursor.getColumnIndexOrThrow("cod_cubricion")));
            c.setId_explotacion(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")));
            c.setid_lote(cursor.getString(cursor.getColumnIndexOrThrow("id_lote")));
            c.setnMadres(cursor.getInt(cursor.getColumnIndexOrThrow("nMadres")));
            c.setnPadres(cursor.getInt(cursor.getColumnIndexOrThrow("nPadres")));
            c.setFechaInicioCubricion(cursor.getString(cursor.getColumnIndexOrThrow("fechaInicioCubricion")));
            c.setFechaFinCubricion(cursor.getString(cursor.getColumnIndexOrThrow("fechaFinCubricion")));
            c.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
            c.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));

            lista.add(c);
        }

        cursor.close();
        return lista;
    }

    public void marcarComoSincronizado(String id, String fechaActualizacion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        values.put("fecha_actualizacion", fechaActualizacion);
        db.update("cubriciones", values, "id = ?", new String[]{id});
    }

    public void insertarOActualizarCubricion(Cubriciones c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM cubriciones WHERE id = ?", new String[]{c.getId()});

        ContentValues values = new ContentValues();
        values.put("id", c.getId());
        values.put("cod_cubricion", c.getCod_cubricion());
        values.put("id_explotacion", c.getId_explotacion());
        values.put("id_lote", c.getid_lote());
        values.put("nMadres", c.getnMadres());
        values.put("nPadres", c.getnPadres());
        values.put("fechaInicioCubricion", c.getFechaInicioCubricion());
        values.put("fechaFinCubricion", c.getFechaFinCubricion());
        values.put("sincronizado", 1); // ya sincronizado desde Supabase
        values.put("fecha_actualizacion", c.getFechaActualizacion());

        if (cursor.moveToFirst()) {
            db.update("cubriciones", values, "id = ?", new String[]{c.getId()});
            Log.d(TAG, "🔄 Cubrición actualizada: " + c.getId());
        } else {
            db.insert("cubriciones", null, values);
            Log.d(TAG, "✅ Cubrición insertada: " + c.getId());
        }

        cursor.close();
    }

    public void subirCubricionesNoSincronizadas(String authHeader, String apiKey) {
        List<Cubriciones> lista = obtenerCubricionesNoSincronizadas();

        Log.d("CUBRICION_REPO", "📤 Cubriciones no sincronizadas: " + lista.size());

        if (lista.isEmpty()) {
            Log.d("CUBRICION_REPO", "📭 No hay cubriciones para subir.");
            return;
        }
        CubricionService service = ApiClient.getClient().create(CubricionService.class);

        for (Cubriciones c : lista) {
            final Cubriciones cubricion = c;  // 👈 COPIA FINAL

            Call<Void> call = service.insertarCubricion(
                    c,
                    authHeader,  // ✅
                    apiKey       // ✅
            );
            Log.d(TAG, "🔄 Enviando cubrición al servidor: " + c.getCod_cubricion());

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        marcarCubricionComoSincronizada(c.getId(), c.getFechaActualizacion());
                        Log.d(TAG, "🚀 Cubrición subida: " + c.getCod_cubricion());
                    } else {
                        Log.e(TAG, "❌ Fallo al subir cubrición " + c.getCod_cubricion() + " | Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "❌ Error de red al subir cubrición " + c.getCod_cubricion(), t);
                }
            });
        }
    }
    public void marcarCubricionComoSincronizada(String id, String fechaActualizacion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        values.put("fecha_actualizacion", fechaActualizacion);
        db.update("cubriciones", values, "id = ?", new String[]{id});
    }



}
