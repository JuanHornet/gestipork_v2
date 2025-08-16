package com.example.gestipork_v2.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.tabs.Accion;
import com.example.gestipork_v2.network.AccionService;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.SupabaseConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccionRepository {

    private final DBHelper dbHelper;
    private final AccionService accionService;

    public AccionRepository(Context context) {
        this.dbHelper = new DBHelper(context);
        this.accionService = ApiClient.getClient().create(AccionService.class);
    }

    public List<Accion> obtenerAccionesNoSincronizadasPorExplotacion(String idExplotacion) {
        List<Accion> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM acciones WHERE sincronizado = 0 AND eliminado = 0 AND id_explotacion = ?",
                new String[]{idExplotacion}
        );

        while (cursor.moveToNext()) {
            Accion accion = new Accion(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_lote")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("tipoAccion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fechaAccion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales")),
                    cursor.getString(cursor.getColumnIndexOrThrow("observacion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado"))
            );
            lista.add(accion);
        }

        cursor.close();
        return lista;
    }

    public void insertarOActualizarAccion(Accion accion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM acciones WHERE id = ?", new String[]{accion.getId()});
        boolean existe = cursor.moveToFirst();
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("id", accion.getId());
        values.put("id_lote", accion.getId_lote());
        values.put("id_explotacion", accion.getId_explotacion());
        values.put("tipoAccion", accion.getTipo());
        values.put("fechaAccion", accion.getFecha());
        values.put("nAnimales", accion.getCantidad());
        values.put("observacion", accion.getObservaciones());
        values.put("sincronizado", 1); // viene desde Supabase
        values.put("fecha_actualizacion", accion.getFechaActualizacion());
        values.put("eliminado", accion.getEliminado());
        values.put("fecha_eliminado", accion.getFechaEliminado());

        if (existe) {
            db.update("acciones", values, "id = ?", new String[]{accion.getId()});
        } else {
            db.insert("acciones", null, values);
        }
    }

    public void marcarAccionComoSincronizada(String id, String fechaActualizacion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("sincronizado", 1);
        valores.put("fecha_actualizacion", fechaActualizacion);
        db.update("acciones", valores, "id = ?", new String[]{id});
    }

    public void subirAccionesNoSincronizadas() {
        List<Accion> noSincronizadas = obtenerAccionesNoSincronizadasPorExplotacion(null); // cuidado si usas null
        if (noSincronizadas.isEmpty()) return;

        accionService.insertarAcciones(
                noSincronizadas,
                SupabaseConfig.getAuthHeader(),

                SupabaseConfig.getApiKey(),
                SupabaseConfig.getContentType()
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    for (Accion accion : noSincronizadas) {
                        marcarAccionComoSincronizada(accion.getId(), accion.getFechaActualizacion());
                    }
                    Log.d("SYNC_ACCIONES", "✅ Acciones subidas correctamente");
                } else {
                    Log.e("SYNC_ACCIONES", "❌ Error al subir acciones: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("SYNC_ACCIONES", "❌ Error de red al subir acciones", t);
            }
        });

    }
    public void eliminarAccionEnSupabase(String idAccion) {
        String url = "acciones?id=eq." + idAccion;
        accionService.eliminarAccion(SupabaseConfig.getBaseUrl() + "/" + url,
                SupabaseConfig.getAuthHeader(),
                SupabaseConfig.getApiKey(),
                SupabaseConfig.getContentType()
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("ACCION_REPO", "✅ Acción eliminada en Supabase: " + idAccion);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ACCION_REPO", "❌ Error al eliminar acción en Supabase", t);
            }
        });
    }


}
