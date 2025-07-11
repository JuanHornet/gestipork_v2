package com.example.gestipork_v2.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.Parideras;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.ParideraService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParideraRepository {

    private final DBHelper dbHelper;
    private static final String TAG = "PARIDERA_REPO";

    public ParideraRepository(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<Parideras> obtenerPariderasNoSincronizadas() {
        List<Parideras> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM parideras WHERE sincronizado = 0", null);
        while (cursor.moveToNext()) {
            Parideras p = new Parideras();
            p.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            p.setCod_paridera(cursor.getString(cursor.getColumnIndexOrThrow("cod_paridera")));
            p.setId_explotacion(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")));
            p.setid_lote(cursor.getString(cursor.getColumnIndexOrThrow("id_lote")));
            p.setFechaInicioParidera(cursor.getString(cursor.getColumnIndexOrThrow("fechaInicioParidera")));
            p.setFechaFinParidera(cursor.getString(cursor.getColumnIndexOrThrow("fechaFinParidera")));
            p.setNacidosVivos(cursor.getInt(cursor.getColumnIndexOrThrow("nacidosVivos")));
            p.setnParidas(cursor.getInt(cursor.getColumnIndexOrThrow("nParidas")));
            p.setnVacias(cursor.getInt(cursor.getColumnIndexOrThrow("nVacias")));
            p.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
            p.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));

            lista.add(p);
        }

        cursor.close();
        return lista;
    }

    public void insertarOActualizarParidera(Parideras p) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM parideras WHERE id = ?", new String[]{p.getId()});

        ContentValues values = new ContentValues();
        values.put("id", p.getId());
        values.put("cod_paridera", p.getCod_paridera());
        values.put("id_explotacion", p.getId_explotacion());
        values.put("id_lote", p.getid_lote());
        values.put("fechaInicioParidera", p.getFechaInicioParidera());
        values.put("fechaFinParidera", p.getFechaFinParidera());
        values.put("nacidosVivos", p.getNacidosVivos());
        values.put("nParidas", p.getnParidas());
        values.put("nVacias", p.getnVacias());
        values.put("sincronizado", 1); // Ya viene desde Supabase
        values.put("fecha_actualizacion", p.getFechaActualizacion());

        if (cursor.moveToFirst()) {
            db.update("parideras", values, "id = ?", new String[]{p.getId()});
            Log.d(TAG, "🔄 Paridera actualizada: " + p.getId());
        } else {
            db.insert("parideras", null, values);
            Log.d(TAG, "✅ Paridera insertada: " + p.getId());
        }

        cursor.close();
    }

    public void subirPariderasNoSincronizadas(String authHeader, String apiKey) {
        List<Parideras> lista = obtenerPariderasNoSincronizadas();

        Log.d(TAG, "📤 Parideras no sincronizadas: " + lista.size());

        if (lista.isEmpty()) {
            Log.d(TAG, "📭 No hay parideras para subir.");
            return;
        }

        ParideraService service = ApiClient.getClient().create(ParideraService.class);

        for (Parideras p : lista) {
            final Parideras paridera = p;

            Call<Void> call = service.insertarParidera(
                    paridera,
                    authHeader,
                    apiKey
            );

            Log.d(TAG, "🔄 Enviando paridera al servidor: " + p.getCod_paridera());

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        marcarParideraComoSincronizada(paridera.getId(), paridera.getFechaActualizacion());
                        Log.d(TAG, "🚀 Paridera subida: " + paridera.getCod_paridera());
                    } else {
                        Log.e(TAG, "❌ Fallo al subir paridera " + paridera.getCod_paridera() + " | Código: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "❌ Error de red al subir paridera " + paridera.getCod_paridera(), t);
                }
            });
        }
    }

    public void marcarParideraComoSincronizada(String id, String fechaActualizacion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        values.put("fecha_actualizacion", fechaActualizacion);
        db.update("parideras", values, "id = ?", new String[]{id});
    }
}
