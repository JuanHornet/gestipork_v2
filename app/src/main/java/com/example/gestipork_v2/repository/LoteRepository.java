package com.example.gestipork_v2.repository;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.Lotes;

import java.util.ArrayList;
import java.util.List;

public class LoteRepository {

    private final DBHelper dbHelper;

    public LoteRepository(Context context) {
        dbHelper = new DBHelper(context);
    }

    public List<Lotes> obtenerLotesNoSincronizados() {
        List<Lotes> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM lotes WHERE sincronizado = 0", null);
        if (cursor.moveToFirst()) {
            do {
                Lotes lote = new Lotes();
                lote.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                lote.setCod_explotacion(cursor.getString(cursor.getColumnIndexOrThrow("cod_explotacion")));
                lote.setnDisponibles(cursor.getInt(cursor.getColumnIndexOrThrow("nDisponibles")));
                lote.setnIniciales(cursor.getInt(cursor.getColumnIndexOrThrow("nIniciales")));
                lote.setCod_lote(cursor.getString(cursor.getColumnIndexOrThrow("cod_lote")));
                lote.setCod_paridera(cursor.getString(cursor.getColumnIndexOrThrow("cod_paridera")));
                lote.setCod_cubricion(cursor.getString(cursor.getColumnIndexOrThrow("cod_cubricion")));
                lote.setCod_itaca(cursor.getString(cursor.getColumnIndexOrThrow("cod_itaca")));
                lote.setRaza(cursor.getString(cursor.getColumnIndexOrThrow("raza")));
                lote.setColor(cursor.getString(cursor.getColumnIndexOrThrow("color")));
                lote.setEstado(cursor.getInt(cursor.getColumnIndexOrThrow("estado")));
                lote.setSincronizado(0);
                lote.setFecha_actualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
                lista.add(lote);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    public void marcarLoteComoSincronizado(int id, String fechaActualizacion) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        values.put("fecha_actualizacion", fechaActualizacion);
        db.update("lotes", values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void insertarOActualizarLote(Lotes lote) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM lotes WHERE id = ?", new String[]{String.valueOf(lote.getId())});

        ContentValues values = new ContentValues();
        values.put("id", lote.getId());
        values.put("cod_explotacion", lote.getCod_explotacion());
        values.put("nDisponibles", lote.getnDisponibles());
        values.put("nIniciales", lote.getnIniciales());
        values.put("cod_lote", lote.getCod_lote());
        values.put("cod_paridera", lote.getCod_paridera());
        values.put("cod_cubricion", lote.getCod_cubricion());
        values.put("cod_itaca", lote.getCod_itaca());
        values.put("raza", lote.getRaza());
        values.put("color", lote.getColor());
        values.put("estado", lote.getEstado());
        values.put("sincronizado", 1);
        values.put("fecha_actualizacion", lote.getFecha_actualizacion());

        if (cursor.moveToFirst()) {
            db.update("lotes", values, "id = ?", new String[]{String.valueOf(lote.getId())});
        } else {
            db.insert("lotes", null, values);
        }

        cursor.close();
    }
}
