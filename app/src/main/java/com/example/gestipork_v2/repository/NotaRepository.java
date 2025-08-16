package com.example.gestipork_v2.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.Nota;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.NotasService;
import com.example.gestipork_v2.network.SupabaseConfig;
import com.example.gestipork_v2.sync.SincronizadorEliminaciones;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;
public class NotaRepository {

    private final DBHelper dbHelper;
    private final Context context;


    public NotaRepository(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    public List<Nota> obtenerNotasNoSincronizadas() {
        List<Nota> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM notas WHERE sincronizado = 0 AND eliminado = 0", null);

        while (cursor.moveToNext()) {
            Nota nota = new Nota(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_lote")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    cursor.getString(cursor.getColumnIndexOrThrow("observacion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion"))
            );
            lista.add(nota);
        }
        cursor.close();
        return lista;
    }

    public void marcarNotaComoSincronizada(String id, String fechaActualizacion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("sincronizado", 1);
        valores.put("fecha_actualizacion", fechaActualizacion);
        db.update("notas", valores, "id = ?", new String[]{id});
    }

    public void insertarOActualizarNota(Nota nota) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM notas WHERE id = ?", new String[]{nota.getId()});
        boolean existe = cursor.moveToFirst();
        cursor.close();

        ContentValues valores = new ContentValues();
        valores.put("id", nota.getId());
        valores.put("id_lote", nota.getId_lote());
        valores.put("id_explotacion", nota.getId_explotacion());
        valores.put("fecha", nota.getFecha());
        valores.put("observacion", nota.getObservacion());
        valores.put("sincronizado", 1);
        valores.put("fecha_actualizacion", nota.getFechaActualizacion());

        if (existe) {
            db.update("notas", valores, "id = ?", new String[]{nota.getId()});
        } else {
            db.insert("notas", null, valores);
        }
    }

    public void eliminarNotaRemotamente(String idNota) {
        NotasService service = ApiClient.getClient().create(NotasService.class);
        Call<Void> call = service.eliminarNota(
                "eq." + idNota,  // Muy importante: así se filtra por ID en Supabase
                SupabaseConfig.getAuthHeader(),
                SupabaseConfig.getApiKey()
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("NOTA_REPO", "✅ Nota eliminada remotamente: " + idNota);
                } else {
                    Log.e("NOTA_REPO", "❌ Error al eliminar nota remota: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("NOTA_REPO", "❌ Error de red al eliminar nota: " + t.getMessage());
            }
        });
    }

    public void marcarNotaComoEliminada(String idNota, String fechaEliminado) {
        if (!SupabaseConfig.hayConexionInternet(context)) {
            // Guardar en eliminaciones_pendientes
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues eliminacion = new ContentValues();
            eliminacion.put("id", UUID.randomUUID().toString());
            eliminacion.put("id_registro", idNota);
            eliminacion.put("tabla", "notas");
            eliminacion.put("fecha_eliminado", fechaEliminado);
            eliminacion.put("sincronizado", 0);
            db.insert("eliminaciones_pendientes", null, eliminacion);
        } else {
            // ✅ Hacer PATCH inmediato si hay conexión
            SincronizadorEliminaciones s = new SincronizadorEliminaciones(context);
            s.sincronizarEliminacionInmediata("notas", idNota, fechaEliminado);
        }
    }


    public void sincronizarEliminacionNota(Nota nota) {
        NotasService service = ApiClient.getClient().create(NotasService.class);

        Call<Void> call = service.insertarNota(  // Es un UPSERT
                nota,
                SupabaseConfig.getAuthHeader(),
                SupabaseConfig.getApiKey()
        );

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("NOTA_REPO", "✅ Eliminación lógica de nota sincronizada con éxito");
                    // Marcar como sincronizada localmente
                    marcarNotaComoSincronizada(nota.getId(), nota.getFechaEliminado());
                } else {
                    Log.e("NOTA_REPO", "❌ Error al sincronizar eliminación lógica de nota: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("NOTA_REPO", "❌ Error de red al sincronizar eliminación de nota: " + t.getMessage());
            }
        });
    }

    public List<Nota> obtenerNotasNoSincronizadasPorExplotacion(String idExplotacion) {
        List<Nota> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM notas WHERE sincronizado = 0 AND eliminado = 0 AND id_explotacion = ?",
                new String[]{idExplotacion}
        );

        while (cursor.moveToNext()) {
            Nota nota = new Nota(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_lote")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    cursor.getString(cursor.getColumnIndexOrThrow("observacion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado"))
            );
            lista.add(nota);
        }

        cursor.close();
        return lista;
    }


}
