package com.example.gestipork_v2.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.Itaca;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.ItacaService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ItacaRepository {

    private final Context context;
    private final DBHelper dbHelper;
    private static final String TAG = "ITACA_REPO";

    public ItacaRepository(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    public List<Itaca> obtenerItacasNoSincronizadas() {
        List<Itaca> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM itaca WHERE sincronizado = 0", null);
        while (cursor.moveToNext()) {
            Itaca i = new Itaca(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("cod_itaca")),
                    cursor.getString(cursor.getColumnIndexOrThrow("DCER")),
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
            lista.add(i);
        }

        cursor.close();
        db.close();
        return lista;
    }

    public void subirItacasNoSincronizadas(String authHeader, String apiKey) {
        List<Itaca> pendientes = obtenerItacasNoSincronizadas();
        ItacaService service = ApiClient.getClient().create(ItacaService.class);

        for (Itaca i : pendientes) {
            Call<Void> call = service.insertarItaca(i, authHeader, apiKey);
            try {
                Response<Void> response = call.execute();
                if (response.isSuccessful()) {
                    marcarItacaComoSincronizada(i.getId());
                    Log.d(TAG, "🚀 Itaca subida: " + i.getCod_itaca());
                } else {
                    Log.e(TAG, "❌ Fallo al subir itaca " + i.getCod_itaca() + " | Código: " + response.code());
                }
            } catch (IOException e) {
                Log.e(TAG, "❌ Error red al subir itaca " + i.getCod_itaca(), e);
            }
        }
    }

    public void marcarItacaComoSincronizada(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE itaca SET sincronizado = 1 WHERE id = ?", new String[]{id});
        db.close();
    }

    public void insertarOActualizarItaca(Itaca i) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM itaca WHERE id = ?", new String[]{i.getId()});
        boolean existe = cursor.moveToFirst();
        cursor.close();

        if (existe) {
            db.execSQL("UPDATE itaca SET cod_itaca = ?, DCER = ?, nAnimales = ?, nMadres = ?, nPadres = ?, " +
                            "fechaPNacimiento = ?, fechaUltNacimiento = ?, raza = ?, color = ?, crotalesSolicitados = ?, " +
                            "id_lote = ?, id_explotacion = ?, sincronizado = 1, fecha_actualizacion = ? WHERE id = ?",
                    new Object[]{
                            i.getCod_itaca(), i.getDCER(), i.getnAnimales(), i.getnMadres(), i.getnPadres(),
                            i.getFechaPNacimiento(), i.getFechaUltNacimiento(), i.getRaza(), i.getColor(), i.getCrotalesSolicitados(),
                            i.getid_lote(), i.getId_explotacion(), i.getFechaActualizacion(), i.getId()
                    });
        } else {
            db.execSQL("INSERT INTO itaca (id, cod_itaca, DCER, nAnimales, nMadres, nPadres, fechaPNacimiento, fechaUltNacimiento, raza, color, crotalesSolicitados, id_lote, id_explotacion, sincronizado, fecha_actualizacion) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, ?)",
                    new Object[]{
                            i.getId(), i.getCod_itaca(), i.getDCER(), i.getnAnimales(), i.getnMadres(), i.getnPadres(),
                            i.getFechaPNacimiento(), i.getFechaUltNacimiento(), i.getRaza(), i.getColor(), i.getCrotalesSolicitados(),
                            i.getid_lote(), i.getId_explotacion(), i.getFechaActualizacion()
                    });
        }

        db.close();
    }

    public void descargarItacasDesdeSupabase(String filtroFecha, String authHeader, String apiKey) {
        ItacaService service = ApiClient.getClient().create(ItacaService.class);
        Call<List<Itaca>> call = service.getItacasModificadas(
                filtroFecha,
                "fecha_actualizacion.asc",
                authHeader,
                apiKey,
                "application/json"
        );

        try {
            Response<List<Itaca>> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                for (Itaca i : response.body()) {
                    insertarOActualizarItaca(i);
                    Log.d(TAG, "⬇️ Itaca descargada: " + i.getCod_itaca());
                }
            } else {
                Log.e(TAG, "❌ Error al descargar itacas | Código: " + response.code());
            }
        } catch (IOException e) {
            Log.e(TAG, "❌ Excepción al descargar itacas", e);
        }
    }

}
