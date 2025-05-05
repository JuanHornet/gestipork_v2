package com.example.proyecto_gestipork.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "gestipork.db";
    private static final int DB_VERSION = 3;

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
        db.execSQL(CREATE_TABLE_PARIDERAS);
        db.execSQL(CREATE_TABLE_CUBRICIONES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Lógica de actualización de esquema si cambias versiones
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS explotaciones");
        db.execSQL("DROP TABLE IF EXISTS itaca");
        db.execSQL("DROP TABLE IF EXISTS lotes");
        db.execSQL("DROP TABLE IF EXISTS parideras");
        db.execSQL("DROP TABLE IF EXISTS cubriciones");
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
            "cod_explotacion TEXT, " +
            "nDisponibles INTEGER, " +
            "nIniciales INTEGER, " +
            "cod_lote TEXT, " +
            "cod_paridera TEXT, " +
            "cod_cubricion TEXT, " +
            "cod_itaca TEXT, " +
            "raza TEXT, " +
            "estado INTEGER, " +
            "color TEXT" +
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
            "cod_explotacion TEXT " +
            ")";


    // TABLA CUBRICIONES
    private static final String CREATE_TABLE_CUBRICIONES = "CREATE TABLE cubriciones (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_cubricion TEXT, " +
            "nMadres INTEGER, " +
            "nPadres INTEGER, " +
            "fechaInicioCubricion TEXT, " +  // Usamos TEXT para fechas
            "fechaFinCubricion TEXT, " +
            "cod_explotacion TEXT, " +
            "cod_lote TEXT" +
            ")";

    // TABLA PARIDERAS
    private static final String CREATE_TABLE_PARIDERAS = "CREATE TABLE parideras (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_paridera TEXT, " +
            "fechaInicioParidera TEXT, " +
            "fechaFinParidera TEXT, " +
            "nacidosVivos INTEGER, " +
            "nParidas INTEGER, " +
            "nVacias INTEGER, " +
            "cod_lote TEXT, " +
            "cod_explotacion TEXT" +
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
    public void insertarRegistrosRelacionadosLote(Context context, SQLiteDatabase db, String codLote, String codExplotacion, String raza) {
        // 1. Insertar en parideras
        String codParidera = "P" + codLote + codExplotacion;
        ContentValues paridera = new ContentValues();
        paridera.put("cod_paridera", codParidera);
        paridera.put("cod_lote", codLote);
        paridera.put("cod_explotacion", codExplotacion);
        paridera.put("fechaInicioParidera", "");
        paridera.put("fechaFinParidera", "");
        paridera.put("nacidosVivos", 0);
        paridera.put("nParidas", 0);
        paridera.put("nVacias", 0);

        long resultadoParidera = db.insert("parideras", null, paridera);
        if (resultadoParidera == -1) {
            Toast.makeText(context, "Lote guardado, pero error en parideras", Toast.LENGTH_SHORT).show();
        }

        // 2. Insertar en cubriciones
        String codCubricion = "C" + codLote + codExplotacion;
        ContentValues cubricion = new ContentValues();
        cubricion.put("cod_cubricion", codCubricion);
        cubricion.put("cod_lote", codLote);
        cubricion.put("cod_explotacion", codExplotacion);
        cubricion.put("nMadres", 0);
        cubricion.put("nPadres", 0);
        cubricion.put("fechaInicioCubricion", "");
        cubricion.put("fechaFinCubricion", "");

        long resultadoCubricion = db.insert("cubriciones", null, cubricion);
        if (resultadoCubricion == -1) {
            Toast.makeText(context, "Lote guardado, pero error en cubriciones", Toast.LENGTH_SHORT).show();
        }

        // 3. Insertar en itaca
        String codItaca = "I" + codLote + codExplotacion;
        ContentValues itaca = new ContentValues();
        itaca.put("cod_itaca", codItaca);
        itaca.put("cod_lote", codLote);
        itaca.put("cod_explotacion", codExplotacion);
        itaca.put("raza", raza);
        itaca.put("nAnimales", 0);
        itaca.put("nMadres", 0);
        itaca.put("nPadres", 0);
        itaca.put("fechaPNacimiento", "");
        itaca.put("fechaUltNacimiento", "");
        itaca.put("color", "#CCCCCC");
        itaca.put("crotalesSolicitados", 0);

        long resultadoItaca = db.insert("itaca", null, itaca);
        if (resultadoItaca == -1) {
            Toast.makeText(context, "Lote guardado, pero error en itaca", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean eliminarLoteConRelaciones(String codLote, String codExplotacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Eliminar primero las relaciones
        int filasItaca = db.delete("itaca", "cod_lote = ? AND cod_explotacion = ?", new String[]{codLote, codExplotacion});
        int filasParideras = db.delete("parideras", "cod_lote = ? AND cod_explotacion = ?", new String[]{codLote, codExplotacion});
        int filasCubriciones = db.delete("cubriciones", "cod_lote = ? AND cod_explotacion = ?", new String[]{codLote, codExplotacion});

        // Luego eliminar el lote
        int filasLotes = db.delete("lotes", "cod_lote = ? AND cod_explotacion = ?", new String[]{codLote, codExplotacion});

        return filasLotes > 0;
    }



    // ---------------------------------------------------------------------------------------------
    // Más tablas en el futuro...
    // ---------------------------------------------------------------------------------------------
    // Aquí podrás añadir futuras secciones como:
    // - TABLA LOTES
    // - TABLA REPRODUCTORES
    // - etc.


}
