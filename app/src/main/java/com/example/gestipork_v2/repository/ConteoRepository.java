package com.example.gestipork_v2.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.Conteo;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.ConteoService;
import com.example.gestipork_v2.network.SupabaseConfig;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConteoRepository {

    private final DBHelper dbHelper;
    private final Context context;

    public ConteoRepository(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    public List<Conteo> obtenerConteosNoSincronizados() {
        List<Conteo> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM contar WHERE sincronizado = 0", null);

        while (cursor.moveToNext()) {
            Conteo c = new Conteo();
            c.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            c.setId_explotacion(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")));
            c.setId_lote(cursor.getString(cursor.getColumnIndexOrThrow("id_lote")));
            c.setnAnimales(cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales")));
            c.setObservaciones(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));
            c.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
            c.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
            c.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
            lista.add(c);
        }

        cursor.close();
        return lista;
    }

    public void insertarOActualizarConteo(Conteo conteo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM contar WHERE id = ?", new String[]{String.valueOf(conteo.getId())});
        boolean existe = cursor.moveToFirst();
        cursor.close();

        ContentValues valores = new ContentValues();
        valores.put("id", conteo.getId());
        valores.put("id_explotacion", conteo.getId_explotacion());
        valores.put("id_lote", conteo.getId_lote());
        valores.put("nAnimales", conteo.getnAnimales());
        valores.put("observaciones", conteo.getObservaciones());
        valores.put("fecha", conteo.getFecha());
        valores.put("sincronizado", 1);
        valores.put("fecha_actualizacion", conteo.getFechaActualizacion());

        if (existe) {
            db.update("contar", valores, "id = ?", new String[]{String.valueOf(conteo.getId())});
        } else {
            db.insert("contar", null, valores);
        }
    }

    public void marcarConteoComoSincronizado(String id, String fechaActualizacion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("sincronizado", 1);
        valores.put("fecha_actualizacion", fechaActualizacion);
        db.update("contar", valores, "id = ?", new String[]{String.valueOf(id)});
    }

    public List<Conteo> obtenerConteosNoSincronizadosPorExplotacion(String idExplotacion) {
        List<Conteo> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM contar WHERE sincronizado = 0 AND eliminado = 0 AND id_explotacion = ?",
                new String[]{idExplotacion}
        );

        while (cursor.moveToNext()) {
            Conteo conteo = new Conteo(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_lote")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales")),
                    cursor.getString(cursor.getColumnIndexOrThrow("observaciones")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado"))
            );
            lista.add(conteo);
        }

        cursor.close();
        return lista;
    }

}
