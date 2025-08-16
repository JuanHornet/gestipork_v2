package com.example.gestipork_v2.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.EliminacionPendiente;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EliminacionRepository {

    private final DBHelper dbHelper;

    public EliminacionRepository(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Insertar una nueva eliminación pendiente
    public void insertarEliminacionPendiente(String idRegistro, String tabla, String fechaEliminado) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", UUID.randomUUID().toString());
        values.put("id_registro", idRegistro);
        values.put("tabla", tabla);
        values.put("fecha_eliminado", fechaEliminado);
        values.put("sincronizado", 0);
        db.insert("eliminaciones_pendientes", null, values);
    }

    // Obtener todas las eliminaciones no sincronizadas
    public List<EliminacionPendiente> obtenerEliminacionesNoSincronizadas() {
        List<EliminacionPendiente> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM eliminaciones_pendientes WHERE sincronizado = 0", null);

        if (cursor.moveToFirst()) {
            do {
                EliminacionPendiente e = new EliminacionPendiente();
                e.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                e.setIdRegistro(cursor.getString(cursor.getColumnIndexOrThrow("id_registro")));
                e.setTabla(cursor.getString(cursor.getColumnIndexOrThrow("tabla")));
                e.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));
                e.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                lista.add(e);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }


    public void eliminarEliminacionPendiente(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete("eliminaciones_pendientes", "id = ?", new String[]{id});
        if (filas > 0) {
            Log.d("SYNC_ELIM", "🗑️ Eliminación pendiente eliminada de la tabla: " + id);
        } else {
            Log.e("SYNC_ELIM", "⚠️ No se encontró registro para eliminar: " + id);
        }
    }
    // Obtener eliminaciones no sincronizadas para una tabla específica
    public List<EliminacionPendiente> obtenerPorTabla(String tabla) {
        List<EliminacionPendiente> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM eliminaciones_pendientes WHERE tabla = ? AND sincronizado = 0", new String[]{tabla});

        if (cursor.moveToFirst()) {
            do {
                EliminacionPendiente e = new EliminacionPendiente();
                e.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                e.setIdRegistro(cursor.getString(cursor.getColumnIndexOrThrow("id_registro")));
                e.setTabla(cursor.getString(cursor.getColumnIndexOrThrow("tabla")));
                e.setFechaEliminado(cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado")));
                e.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
                lista.add(e);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

}
