package com.example.proyecto_gestipork.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "gestipork.db";
    private static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear todas las tablas
        db.execSQL(CREATE_TABLE_USUARIOS);
        db.execSQL(CREATE_TABLE_EXPLOTACIONES);
        db.execSQL(CREATE_TABLE_LOTES);
        db.execSQL(CREATE_TABLE_ITACA);
        // Aquí irás añadiendo más db.execSQL para nuevas tablas
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Lógica de actualización de esquema si cambias versiones
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS explotaciones");
        db.execSQL("DROP TABLE IF EXISTS itaca");
        db.execSQL("DROP TABLE IF EXISTS lotes");
        onCreate(db);
    }


    // TABLA USUARIOS

    private static final String CREATE_TABLE_USUARIOS = "CREATE TABLE IF NOT EXISTS usuarios (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "email TEXT UNIQUE, " +
            "password TEXT)";

    //TABLA EXPLOTACIONES

    private static final String CREATE_TABLE_EXPLOTACIONES = "CREATE TABLE IF NOT EXISTS explotaciones (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_explotacion TEXT, " +
            "nombre TEXT, " +
            "iduser INTEGER)";

    // TABLA LOTES
    private static final String CREATE_TABLE_LOTES = "CREATE TABLE lotes (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_explotacion INTEGER, " +
            "nDisponibles INTEGER, " +
            "nIniciales INTEGER, " +
            "cod_lote TEXT, " +
            "cod_paridera TEXT, " +
            "cod_cubricion TEXT, " +
            "cod_itaca TEXT, " +
            "raza TEXT, " +
            "estado INTEGER" +
            ")";

    // TABLA ITACA
    private static final String CREATE_TABLE_ITACA = "CREATE TABLE itaca (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_itaca TEXT UNIQUE NOT NULL, " +
            "nAnimales INTEGER, " +
            "nMadres INTEGER, " +
            "nPadres INTEGER, " +
            "fechaPNacimiento TEXT, " +
            "fechaUltNacimiento TEXT, " +
            "raza TEXT, " +
            "color TEXT, " +
            "crotalesSolicitados INTEGER, " +
            "cod_lote TEXT, " +
            "FOREIGN KEY (cod_lote) REFERENCES lotes(cod_lote)" +
            ")";


    public boolean registrarUsuario(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        String hashedPassword = hashPassword(password);
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", hashedPassword);

        long result = db.insert("usuarios", null, values);
        return result != -1;
    }

    public boolean validarUsuario(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);
        Cursor cursor = db.rawQuery("SELECT id FROM usuarios WHERE email = ? AND password = ?",
                new String[]{email, hashedPassword});

        boolean valido = cursor.moveToFirst();
        cursor.close();
        return valido;
    }

    public int obtenerIdUsuarioDesdeEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM usuarios WHERE email = ?", new String[]{email});

        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }



    public boolean insertarExplotacion(String nombre, int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Insertar sin cod_explotacion aún
        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("iduser", idUsuario);
        long nuevaId = db.insert("explotaciones", null, values);

        if (nuevaId == -1) return false;

        // Generar y actualizar cod_explotacion
        String codExplotacion = "E" + idUsuario + nuevaId;
        ContentValues updateValues = new ContentValues();
        updateValues.put("cod_explotacion", codExplotacion);

        db.update("explotaciones", updateValues, "id = ?", new String[]{String.valueOf(nuevaId)});

        return true;
    }

    // (Opcional) Obtener todas las explotaciones de un usuario
    public Cursor obtenerExplotacionesDeUsuario(int idUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT nombre FROM explotaciones WHERE iduser = ?", new String[]{String.valueOf(idUsuario)});
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean actualizarNombreExplotacion(String nombreViejo, String nombreNuevo, int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", nombreNuevo);

        int filas = db.update("explotaciones", values,
                "nombre = ? AND iduser = ?", new String[]{nombreViejo, String.valueOf(idUsuario)});

        return filas > 0;
    }
    public boolean eliminarExplotacionPorNombre(String nombre, int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("explotaciones", "nombre = ? AND iduser = ?", new String[]{nombre, String.valueOf(idUsuario)});
        return filas > 0;
    }

    // ---------------------------------------------------------------------------------------------
    // Más tablas en el futuro...
    // ---------------------------------------------------------------------------------------------
    // Aquí podrás añadir futuras secciones como:
    // - TABLA LOTES
    // - TABLA REPRODUCTORES
    // - etc.


}
