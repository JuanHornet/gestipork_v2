package com.example.gestipork_v2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.gestipork_v2.base.FechaUtils;
import com.example.gestipork_v2.login.Usuario;
import com.example.gestipork_v2.modelo.Conteo;
import com.example.gestipork_v2.modelo.Explotacion;
import com.example.gestipork_v2.modelo.Nota;
import com.example.gestipork_v2.modelo.SalidasExplotacion;
import com.example.gestipork_v2.modelo.tabs.Accion;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.ExplotacionService;
import com.example.gestipork_v2.network.SupabaseConfig;
import com.example.gestipork_v2.network.UsuarioService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "gestipork.db";
    private static final int DB_VERSION = 5;

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
        db.execSQL(CREATE_TABLE_ACCIONES);
        db.execSQL(CREATE_TABLE_SALIDAS);
        db.execSQL(CREATE_TABLE_ALIMENTACION);
        db.execSQL(CREATE_TABLE_CONTAR);
        db.execSQL(CREATE_TABLE_PESAR);
        db.execSQL(CREATE_TABLE_NOTAS);
        db.execSQL(CREATE_TABLE_AFORO);
        db.execSQL(CREATE_TABLE_ELIMINACIONES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Lógica de actualización de esquema cuando cambio versiones
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS explotaciones");
        db.execSQL("DROP TABLE IF EXISTS itaca");
        db.execSQL("DROP TABLE IF EXISTS lotes");
        db.execSQL("DROP TABLE IF EXISTS parideras");
        db.execSQL("DROP TABLE IF EXISTS cubriciones");
        db.execSQL("DROP TABLE IF EXISTS salidas");
        db.execSQL("DROP TABLE IF EXISTS acciones");
        db.execSQL("DROP TABLE IF EXISTS alimentacion");
        db.execSQL("DROP TABLE IF EXISTS contar");
        db.execSQL("DROP TABLE IF EXISTS pesar");
        db.execSQL("DROP TABLE IF EXISTS notas");
        db.execSQL("DROP TABLE IF EXISTS aforo_explotacion");
        db.execSQL("DROP TABLE IF EXISTS eliminaciones_pendientes");
        onCreate(db);
    }


    // TABLA USUARIOS

    private static final String CREATE_TABLE_USUARIOS = "CREATE TABLE IF NOT EXISTS usuarios (" +
            "id TEXT PRIMARY KEY, " +  // UUID generado en Java
            "email TEXT UNIQUE, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "password TEXT)";

    //TABLA EXPLOTACIONES

    private static final String CREATE_TABLE_EXPLOTACIONES = "CREATE TABLE IF NOT EXISTS explotaciones (" +
            "id TEXT PRIMARY KEY, " +  // UUID
            "id_usuario TEXT, " +      // UUID del usuario
            "nombre TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT)";


    // TABLA LOTES
    private static final String CREATE_TABLE_LOTES = "CREATE TABLE lotes (" +
            "id TEXT PRIMARY KEY, " +
            "id_explotacion TEXT, " +
            "nDisponibles INTEGER, " +
            "nIniciales INTEGER, " +
            "nombre_lote TEXT, " +
            "cod_paridera TEXT, " +
            "cod_cubricion TEXT, " +
            "cod_itaca TEXT, " +
            "raza TEXT, " +
            "estado INTEGER, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "color TEXT" +
            ")";

    // TABLA ITACA
    private static final String CREATE_TABLE_ITACA = "CREATE TABLE itaca (" +
            "id TEXT PRIMARY KEY, " +
            "cod_itaca TEXT UNIQUE NOT NULL, " +
            "DCER TEXT, " +
            "nAnimales INTEGER, " +
            "nMadres INTEGER, " +
            "nPadres INTEGER, " +
            "fechaPNacimiento TEXT, " +
            "fechaUltNacimiento TEXT, " +
            "raza TEXT, " +
            "color TEXT, " +
            "crotalesSolicitados INTEGER, " +
            "id_lote TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "id_explotacion TEXT" +
            ")";


    // TABLA CUBRICIONES
    private static final String CREATE_TABLE_CUBRICIONES = "CREATE TABLE cubriciones (" +
            "id TEXT PRIMARY KEY, " +
            "cod_cubricion TEXT, " +
            "nMadres INTEGER, " +
            "nPadres INTEGER, " +
            "fechaInicioCubricion TEXT, " +  // Usamos TEXT para fechas
            "fechaFinCubricion TEXT, " +
            "id_explotacion TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "id_lote TEXT" +
            ")";

    // TABLA PARIDERAS
    private static final String CREATE_TABLE_PARIDERAS = "CREATE TABLE parideras (" +
            "id TEXT PRIMARY KEY, " +
            "cod_paridera TEXT, " +
            "fechaInicioParidera TEXT, " +
            "fechaFinParidera TEXT, " +
            "nacidosVivos INTEGER, " +
            "nParidas INTEGER, " +
            "nVacias INTEGER, " +
            "id_lote TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "id_explotacion TEXT" +
            ")";

    // TABLA ALIMENTACION
    private static final String CREATE_TABLE_ALIMENTACION = "CREATE TABLE alimentacion (" +
            "id TEXT PRIMARY KEY, " +
            "tipoAlimentacion TEXT, " +
            "nAnimales INTEGER, " +
            "fechaInicioAlimentacion TEXT, " +
            "id_lote TEXT, " +
            "id_explotacion TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "UNIQUE(id_lote, id_explotacion, tipoAlimentacion)" +
            ")";

    // TABLA ACCIONES
    private static final String CREATE_TABLE_ACCIONES = "CREATE TABLE acciones (" +
            "id TEXT PRIMARY KEY, " + // UUID
            "id_lote TEXT, " +       // UUID del lote
            "id_explotacion TEXT, " + // UUID de la explotación
            "tipoAccion TEXT, " +
            "nAnimales INTEGER, " +
            "fechaAccion TEXT, " +
            "observacion TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "estado INTEGER DEFAULT 1, " +
            "eliminado INTEGER DEFAULT 0, " +
            "fecha_eliminado TEXT" +
            ")";


    // TABLA SALIDAS
    private static final String CREATE_TABLE_SALIDAS = "CREATE TABLE salidas (" +
            "id TEXT PRIMARY KEY, " +
            "tipoSalida TEXT, " +
            "nAnimales INTEGER, " +
            "fechaSalida TEXT, " +
            "id_lote TEXT, " +
            "id_explotacion TEXT, " +
            "tipoAlimentacion TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "observacion TEXT, " +
            "eliminado INTEGER DEFAULT 0, " +
            "fecha_eliminado TEXT" +
            ")";


    // TABLA CONTEOS

    private static final String CREATE_TABLE_CONTAR = "CREATE TABLE contar (" +
            "id TEXT PRIMARY KEY, " +
            "id_explotacion TEXT NOT NULL, " +
            "id_lote TEXT NOT NULL, " +
            "nAnimales INTEGER NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +// guardaremos fecha como String (yyyy-MM-dd)
            "observaciones TEXT," +
            "eliminado INTEGER DEFAULT 0, " +
            "fecha_eliminado TEXT" +
            ")";

    // TABLA PESAR
    private static final String CREATE_TABLE_PESAR = "CREATE TABLE pesar (" +
            "id TEXT PRIMARY KEY, " +
            "id_explotacion TEXT NOT NULL, " +
            "id_lote TEXT NOT NULL, " +
            "peso INTEGER NOT NULL, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "fecha TEXT NOT NULL)";

    //TABLA NOTAS
    private static final String CREATE_TABLE_NOTAS = "CREATE TABLE IF NOT EXISTS notas (" +
            "id TEXT PRIMARY KEY, " +
            "id_lote TEXT NOT NULL, " +
            "id_explotacion TEXT NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "observacion TEXT NOT NULL, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "eliminado INTEGER DEFAULT 0, " +
            "fecha_eliminado TEXT" +
            ")";


    //TABLA AFOROS
    private static final String CREATE_TABLE_AFORO = "CREATE TABLE IF NOT EXISTS aforo_explotacion (" +
            "Id_explotacion TEXT PRIMARY KEY, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "aforo_maximo INTEGER)";



    private static final String CREATE_TABLE_ELIMINACIONES = "CREATE TABLE IF NOT EXISTS eliminaciones_pendientes (" +
            "id TEXT PRIMARY KEY, " +
            "id_registro TEXT NOT NULL, " +
            "tabla TEXT NOT NULL, " +
            "fecha_eliminado TEXT NOT NULL, " +
            "sincronizado INTEGER DEFAULT 0)";


    public boolean registrarUsuarioConUUID(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", usuario.getId());
        values.put("email", usuario.getEmail());
        values.put("password", usuario.getPassword());
        values.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());
        values.put("sincronizado", 1); // ya está en Supabase

        long result = db.insert("usuarios", null, values);
        return result != -1;
    }


    public static String obtenerFechaActual() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }


    public String validarYObtenerUUID(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);

        Cursor cursor = db.rawQuery(
                "SELECT id FROM usuarios WHERE email = ? AND password = ?",
                new String[]{email, hashedPassword}
        );

        String uuid = null;
        if (cursor.moveToFirst()) {
            uuid = cursor.getString(0);
        }
        cursor.close();
        return uuid;
    }



    public boolean insertarExplotacionNueva(String nombre, String uuidUsuario, String uuidExplotacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", uuidExplotacion);
        values.put("nombre", nombre);
        values.put("id_usuario", uuidUsuario);
        values.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());
        values.put("sincronizado", 0);

        long resultado = db.insert("explotaciones", null, values);
        return resultado != -1;
    }




    // Obtener todas las explotaciones de un usuario
    public Cursor obtenerExplotacionesDeUsuario(String uuidUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT nombre FROM explotaciones WHERE id_usuario = ?", new String[]{uuidUsuario});
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



    public void insertarRegistrosRelacionadosLote(Context context, SQLiteDatabase db, String uuidLote, String idExplotacion, String raza) {

        // 1. Insertar en parideras
        String codParidera = "P" + uuidLote + idExplotacion;
        ContentValues paridera = new ContentValues();
        paridera.put("id", java.util.UUID.randomUUID().toString());
        paridera.put("cod_paridera", codParidera);
        paridera.put("id_lote", uuidLote);
        paridera.put("id_explotacion", idExplotacion);
        paridera.put("fechaInicioParidera", "");
        paridera.put("fechaFinParidera", "");
        paridera.put("nacidosVivos", 0);
        paridera.put("nParidas", 0);
        paridera.put("nVacias", 0);
        paridera.put("sincronizado", 0);
        paridera.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());

        long resultadoParidera = db.insert("parideras", null, paridera);
        if (resultadoParidera == -1) {
            Toast.makeText(context, "Lote guardado, pero error en parideras", Toast.LENGTH_SHORT).show();
        }

        // 2. Insertar en cubriciones
        String codCubricion = "C" + uuidLote + idExplotacion;
        ContentValues cubricion = new ContentValues();
        cubricion.put("id", java.util.UUID.randomUUID().toString());
        cubricion.put("cod_cubricion", codCubricion);
        cubricion.put("id_lote", uuidLote);
        cubricion.put("id_explotacion", idExplotacion);
        cubricion.put("nMadres", 0);
        cubricion.put("nPadres", 0);
        cubricion.put("fechaInicioCubricion", "");
        cubricion.put("fechaFinCubricion", "");
        cubricion.put("sincronizado", 0);
        cubricion.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());

        long resultadoCubricion = db.insert("cubriciones", null, cubricion);
        if (resultadoCubricion == -1) {
            Toast.makeText(context, "Lote guardado, pero error en cubriciones", Toast.LENGTH_SHORT).show();
        }

        // 3. Insertar en itaca
        String codItaca = "I" + uuidLote + idExplotacion;
        ContentValues itaca = new ContentValues();
        itaca.put("id", java.util.UUID.randomUUID().toString());
        itaca.put("cod_itaca", codItaca);
        itaca.put("id_lote", uuidLote);
        itaca.put("id_explotacion", idExplotacion);
        itaca.put("raza", raza);
        itaca.put("color", "");
        itaca.put("nMadres", 0);
        itaca.put("nPadres", 0);
        itaca.put("nAnimales", 0);
        itaca.put("fechaPNacimiento", "");
        itaca.put("fechaUltNacimiento", "");
        itaca.put("crotalesSolicitados", 0);
        itaca.put("sincronizado", 0);
        itaca.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());

        long resultadoItaca = db.insert("itaca", null, itaca);
        if (resultadoItaca == -1) {
            Toast.makeText(context, "Lote guardado, pero error en Itaca", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean eliminarLoteConRelaciones(String idLote, String idExplotacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String[] args = new String[]{idLote, idExplotacion};

            // Eliminar registros relacionados en las tablas hijas
            db.delete("acciones", "id_lote = ? AND id_explotacion = ?", args);
            db.delete("salidas", "id_lote = ? AND id_explotacion = ?", args);
            db.delete("alimentacion", "id_lote = ? AND id_explotacion = ?", args);
            db.delete("parideras", "id_lote = ? AND id_explotacion = ?", args);
            db.delete("cubriciones", "id_lote = ? AND id_explotacion = ?", args);
            db.delete("itaca", "id_lote = ? AND id_explotacion = ?", args);
            db.delete("contar", "id_lote = ? AND id_explotacion = ?", args);
            db.delete("pesar", "id_lote = ? AND id_explotacion = ?", args);
            db.delete("notas", "id_lote = ? AND id_explotacion = ?", args);

            // Finalmente eliminar el lote
            db.delete("lotes", "id = ? AND id_explotacion = ?", args);

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }



    public Cursor obtenerAcciones(String idLote, String idExplotacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM acciones WHERE id_lote = ? AND id_explotacion = ?",
                new String[]{idLote, idExplotacion});
    }


    public void insertarAccion(String tipo, int cantidad, String fecha, String idLote, String idExplotacion, String observacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        String uuid = java.util.UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put("id", uuid);
        values.put("id_lote", idLote);
        values.put("id_explotacion", idExplotacion);
        values.put("tipoAccion", tipo);
        values.put("nAnimales", cantidad);
        values.put("fechaAccion", fecha);
        values.put("observacion", observacion);
        values.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());
        values.put("estado", 1);

        db.insert("acciones", null, values);

        if (tipo.equalsIgnoreCase("Destete")) {
            // 1. Actualizar lote
            ContentValues loteUpdate = new ContentValues();
            loteUpdate.put("nDisponibles", cantidad);
            loteUpdate.put("nIniciales", cantidad);
            loteUpdate.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());
            db.update("lotes", loteUpdate, "id = ?", new String[]{idLote});
            marcarLoteComoNoSincronizado(idLote);


            // 2. Insertar alimentación
            String[] tipos = {"Bellota", "Cebo Campo", "Cebo"};
            for (String tipoAlim : tipos) {
                int nAnimales = tipoAlim.equals("Cebo") ? cantidad : 0;

                ContentValues alim = new ContentValues();
                alim.put("id", java.util.UUID.randomUUID().toString());
                alim.put("id_lote", idLote);
                alim.put("id_explotacion", idExplotacion);
                alim.put("tipoAlimentacion", tipoAlim);
                alim.put("nAnimales", nAnimales);
                alim.put("fechaInicioAlimentacion", fecha);
                alim.put("sincronizado", 0);
                alim.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());

                db.insertWithOnConflict("alimentacion", null, alim, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }
    public void marcarLoteComoNoSincronizado(String idLote) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 0);
        db.update("lotes", values, "id = ?", new String[]{idLote});
    }




    public void actualizarAccion(String id, String tipo, int cantidad, String fecha, String observacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tipoAccion", tipo);
        values.put("nAnimales", cantidad);
        values.put("fechaAccion", fecha);
        values.put("observacion", observacion);
        values.put("fecha_actualizacion", com.example.gestipork_v2.base.FechaUtils.obtenerFechaActual());
        db.update("acciones", values, "id = ?", new String[]{id});
    }


    public boolean eliminarAccion(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Si es tipo Destete, actualiza el lote
        Cursor c = db.rawQuery("SELECT tipoAccion, id_lote FROM acciones WHERE id = ?", new String[]{id});
        if (c.moveToFirst()) {
            String tipo = c.getString(0);
            String idLote = c.getString(1);

            if (tipo.equalsIgnoreCase("Destete")) {
                ContentValues values = new ContentValues();
                values.put("nDisponibles", 0);
                values.put("nIniciales", 0);
                values.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());
                db.update("lotes", values, "id = ?", new String[]{idLote});
            }
        }
        c.close();

        // Eliminar físicamente
        int filas = db.delete("acciones", "id = ?", new String[]{id});
        return filas > 0;
    }


    public void marcarAccionComoSincronizada(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        db.update("acciones", values, "id = ?", new String[]{id});
    }
    public void insertarOActualizarAccion(Accion accion) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", accion.getId());
        values.put("id_lote", accion.getId_lote());
        values.put("id_explotacion", accion.getId_explotacion());
        values.put("tipoAccion", accion.getTipo());
        values.put("fechaAccion", accion.getFecha());
        values.put("nAnimales", accion.getCantidad());
        values.put("observacion", accion.getObservaciones());
        values.put("fecha_actualizacion", accion.getFechaActualizacion());
        values.put("sincronizado", 1); // ya viene sincronizada desde Supabase
        values.put("estado", 1); // activa
        values.put("eliminado", accion.getEliminado());
        values.put("fecha_eliminado", accion.getFechaEliminado());

        db.insertWithOnConflict("acciones", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }



    public void insertarSalida(String tipoSalida, String tipoAlimentacion, int nAnimales,
                               String fechaSalida, String idLote, String idExplotacion, String observacion) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String idSalida = UUID.randomUUID().toString();
        values.put("id", idSalida);

        values.put("tipoSalida", tipoSalida);
        values.put("tipoAlimentacion", tipoAlimentacion);
        values.put("nAnimales", nAnimales);
        values.put("fechaSalida", fechaSalida);
        values.put("id_lote", idLote);
        values.put("id_explotacion", idExplotacion);
        values.put("observacion", observacion);
        values.put("sincronizado", 0);
        values.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());
        values.put("eliminado", 0);
        values.put("fecha_eliminado", (String) null);

        db.insert("salidas", null, values);

        // restar animales al lote y alimentación
        int disponibles = obtenerAnimalesDisponiblesLote(idLote, idExplotacion);
        int nuevosDisponibles = Math.max(0, disponibles - nAnimales);

        ContentValues loteUpdate = new ContentValues();
        loteUpdate.put("nDisponibles", nuevosDisponibles);
        db.update("lotes", loteUpdate,
                "id = ? AND id_explotacion = ?",
                new String[]{idLote, idExplotacion});

        // Restar animales en alimentación
        restarAnimalesAlimentacion(idLote, idExplotacion, tipoAlimentacion, nAnimales);

        db.close();
    }




    public void actualizarSalida(String id, String tipoSalida, String tipoAlimentacion, int nAnimales,
                                 String fechaSalida, String observacion) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Recuperar la salida anterior
        Cursor cursor = db.rawQuery("SELECT nAnimales, id_lote, id_explotacion, tipoAlimentacion FROM salidas WHERE id = ?",
                new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            int cantidadAnterior = cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales"));
            String idLote = cursor.getString(cursor.getColumnIndexOrThrow("id_lote"));
            String idExplotacion = cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion"));
            String alimentacionAnterior = cursor.getString(cursor.getColumnIndexOrThrow("tipoAlimentacion"));

            // Revertir la salida anterior
            sumarAnimalesAlimentacion(idLote, idExplotacion, alimentacionAnterior, cantidadAnterior);

            int disponiblesActuales = obtenerAnimalesDisponiblesLote(idLote, idExplotacion);
            ContentValues loteUpdate = new ContentValues();
            loteUpdate.put("nDisponibles", disponiblesActuales + cantidadAnterior);
            db.update("lotes", loteUpdate, "id = ? AND id_explotacion = ?",
                    new String[]{idLote, idExplotacion});

            // Actualizar la salida
            ContentValues values = new ContentValues();
            values.put("tipoSalida", tipoSalida);
            values.put("tipoAlimentacion", tipoAlimentacion);
            values.put("nAnimales", nAnimales);
            values.put("fechaSalida", fechaSalida);
            values.put("observacion", observacion);
            values.put("sincronizado", 0); // se modificó localmente
            values.put("fecha_actualizacion", FechaUtils.obtenerFechaActual()); // usa tu función base

            db = this.getWritableDatabase();
            db.update("salidas", values, "id = ?", new String[]{id});

            // Aplicar nueva salida
            restarAnimalesAlimentacion(idLote, idExplotacion, tipoAlimentacion, nAnimales);
            int disponiblesFinal = obtenerAnimalesDisponiblesLote(idLote, idExplotacion);
            ContentValues loteUpdateFinal = new ContentValues();
            loteUpdateFinal.put("nDisponibles", Math.max(0, disponiblesFinal - nAnimales));
            db.update("lotes", loteUpdateFinal, "id = ? AND id_explotacion = ?",
                    new String[]{idLote, idExplotacion});
        }

        cursor.close();
        db.close();
    }

    public Cursor obtenerSalidas(String idLote, String idExplotacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM salidas WHERE id_lote = ? AND id_explotacion = ?",
                new String[]{idLote, idExplotacion});
    }


    // Obtener animales actuales en un tipo de alimentación
    public int obtenerAnimalesAlimentacion(String idLote, String idExplotacion, String tipoAlimentacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT nAnimales FROM alimentacion WHERE id_lote = ? AND id_explotacion = ? AND tipoAlimentacion = ?",
                new String[]{idLote, idExplotacion, tipoAlimentacion});

        int cantidad = 0;
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }
        cursor.close();
        return cantidad;
    }


    // Restar animales de un tipo de alimentación
    public void restarAnimalesAlimentacion(String idLote, String idExplotacion, String tipoAlimentacion, int cantidad) {
        int actuales = obtenerAnimalesAlimentacion(idLote, idExplotacion, tipoAlimentacion);
        int nuevos = Math.max(0, actuales - cantidad);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nAnimales", nuevos);
        db.update("alimentacion", values, "id_lote = ? AND id_explotacion = ? AND tipoAlimentacion = ?",
                new String[]{idLote, idExplotacion, tipoAlimentacion});
    }


    // Sumar animales a un tipo de alimentación
    public void sumarAnimalesAlimentacion(String idLote, String idExplotacion, String tipoAlimentacion, int cantidad) {
        int actuales = obtenerAnimalesAlimentacion(idLote, idExplotacion, tipoAlimentacion);
        int nuevos = actuales + cantidad;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nAnimales", nuevos);
        db.update("alimentacion", values, "id_lote = ? AND id_explotacion = ? AND tipoAlimentacion = ?",
                new String[]{idLote, idExplotacion, tipoAlimentacion});
    }

    public void sumarAnimalesAlimentacionConFecha(String idLote, String idExplotacion, String tipoAlimentacion, int cantidad, String fecha) {
        int actuales = obtenerAnimalesAlimentacion(idLote, idExplotacion, tipoAlimentacion);
        int nuevos = actuales + cantidad;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nAnimales", nuevos);
        values.put("fechaInicioAlimentacion", fecha);
        db.update("alimentacion", values, "id_lote = ? AND id_explotacion = ? AND tipoAlimentacion = ?",
                new String[]{idLote, idExplotacion, tipoAlimentacion});
    }

    public List<Conteo> obtenerConteosLista(String idExplotacion, String idLote) {
        List<Conteo> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, id_explotacion, id_lote, nAnimales, observaciones, fecha " +
                        "FROM contar WHERE id_explotacion = ? AND id_lote = ? ORDER BY id DESC",
                new String[]{idExplotacion, idLote});

        if (cursor.moveToFirst()) {
            do {
                Conteo conteo = new Conteo();
                conteo.setId(cursor.getString(0));
                conteo.setId_explotacion(cursor.getString(1));
                conteo.setId_lote(cursor.getString(2));
                conteo.setnAnimales(cursor.getInt(3));
                conteo.setObservaciones(cursor.getString(4));
                conteo.setFecha(cursor.getString(5));

                lista.add(conteo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }



    public int obtenerAnimalesDisponiblesLote(String idLote, String idExplotacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nDisponibles FROM lotes WHERE id = ? AND id_explotacion = ?",
                new String[]{idLote, idExplotacion});
        int disponibles = 0;
        if (cursor.moveToFirst()) {
            disponibles = cursor.getInt(0);
        }
        cursor.close();
        return disponibles;
    }


    public Cursor obtenerFechasPesajes(String idExplotacion, String idLote) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT DISTINCT fecha FROM pesar WHERE id_explotacion = ? AND id_lote = ? ORDER BY fecha DESC",
                new String[]{idExplotacion, idLote});
    }


    // Insertar nueva nota
    public void insertarNota(String idLote, String idExplotacion, String fecha, String observacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        String uuid = UUID.randomUUID().toString();  // Generar UUID para id de la nota
        ContentValues values = new ContentValues();
        values.put("id", uuid);  // Aquí insertamos el UUID generado
        values.put("id_lote", idLote);
        values.put("id_explotacion", idExplotacion);
        values.put("fecha", fecha);
        values.put("observacion", observacion);
        db.insert("notas", null, values);
    }

    // Obtener notas de un lote
    public Cursor obtenerNotas(String idExplotacion, String idLote) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT id, id_lote, id_explotacion, fecha, observacion FROM notas WHERE id_explotacion = ? AND id_lote = ? ORDER BY id DESC",
                new String[]{idExplotacion, idLote});
    }




    // Devuelve todos los lotes activos de una explotación (para poblar Spinner)
    public List<String> obtenerLotesActivos(String idExplotacion) {
        List<String> lotes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM lotes WHERE estado = 1 AND id_explotacion = ? ORDER BY id ASC",
                new String[]{idExplotacion}
        );

        if (cursor.moveToFirst()) {
            do {
                lotes.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lotes;
    }

    public void guardarAforo(String idExplotacion, int aforoMaximo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_explotacion", idExplotacion);
        values.put("aforo_maximo", aforoMaximo);
        db.insertWithOnConflict("aforo_explotacion", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    public int obtenerAforo(String idExplotacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT aforo_maximo FROM aforo_explotacion WHERE id_explotacion = ?",
                new String[]{idExplotacion});

        int aforo = 0;
        if (cursor.moveToFirst()) {
            aforo = cursor.getInt(0);
        }
        cursor.close();
        return aforo;
    }


    public int obtenerAnimalesPorRaza(String idExplotacion, String raza) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(nDisponibles) FROM lotes WHERE id_explotacion = ? AND raza = ? AND estado = 1",
                new String[]{idExplotacion, raza}
        );
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }


    public boolean actualizarNombreExplotacionPorUUID(String nombreViejo, String nombreNuevo, String uuidUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", nombreNuevo);

        int filas = db.update("explotaciones", values,
                "nombre = ? AND id_usuario = ?", new String[]{nombreViejo, uuidUsuario});

        return filas > 0;
    }
    public boolean eliminarExplotacionPorNombreYUUID(String nombre, String uuidUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("explotaciones", "nombre = ? AND id_usuario = ?", new String[]{nombre, uuidUsuario});
        return filas > 0;
    }

    public void marcarExplotacionSincronizada(String uuidExplotacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        db.update("explotaciones", values, "id = ?", new String[]{uuidExplotacion});
    }

    public interface LoginCallback {
        void onResultado(String uuid); // uuid != null si login válido
    }
    public void validarOnlineYGuardarSiExitoso(String email, String password, Context context, LoginCallback callback) {
        String hashedPassword = hashPassword(password);

        UsuarioService service = ApiClient.getClient().create(UsuarioService.class);
        Call<List<Usuario>> call = service.obtenerUsuarioPorCredenciales(
                "eq." + email,
                "eq." + hashedPassword,
                SupabaseConfig.getAuthHeader(),
                SupabaseConfig.getApiKey(),
                SupabaseConfig.getContentType()
        );

        call.enqueue(new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    Usuario usuario = response.body().get(0);
                    registrarUsuarioConUUID(usuario); // Guardar en SQLite como sincronizado
                    callback.onResultado(usuario.getId());
                } else {
                    callback.onResultado(null);
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                callback.onResultado(null);
            }
        });
    }

    public void guardarExplotacionesImportadas(List<Explotacion> lista) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Explotacion e : lista) {
            ContentValues values = new ContentValues();
            values.put("id", e.getId());
            values.put("nombre", e.getNombre());
            values.put("id_usuario", e.getIduser());
            values.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());
            values.put("sincronizado", 1); // ya viene de la nube
            db.insert("explotaciones", null, values);
        }
    }

    public void importarExplotacionesSiNoExisten(String uuidUsuario, Context context, Runnable onFinish) {
            Cursor cursor = obtenerExplotacionesDeUsuario(uuidUsuario);
            if (cursor.getCount() > 0) {
                cursor.close();
                onFinish.run(); // Ya hay explotaciones
                return;
            }
            cursor.close();

            ExplotacionService service = ApiClient.getClient().create(ExplotacionService.class);
            Call<List<Explotacion>> call = service.obtenerExplotacionesPorUsuario(
                    "eq." + uuidUsuario,
                    SupabaseConfig.getAuthHeader(),
                    SupabaseConfig.getApiKey(),
                    SupabaseConfig.getContentType()
            );

            call.enqueue(new Callback<List<Explotacion>>() {
                @Override
                public void onResponse(Call<List<Explotacion>> call, Response<List<Explotacion>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        guardarExplotacionesImportadas(response.body());
                    }
                    onFinish.run();
                }

                @Override
                public void onFailure(Call<List<Explotacion>> call, Throwable t) {
                    onFinish.run(); // continuar aunque haya fallo
                }
            });
        }

    public void marcarParideraComoSincronizada(SQLiteDatabase db, String id) {
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        db.update("parideras", values, "id = ?", new String[]{id});
    }
    public void marcarItacaComoSincronizada(SQLiteDatabase db, String id) {
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        db.update("itaca", values, "id = ?", new String[]{id});
    }




    public Cursor obtenerLotesActivosConUUID(String idExplotacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id FROM lotes WHERE id_explotacion = ? AND estado = 1",
                new String[]{idExplotacion}
        );
    }
    public void insertarPeso(String idExplotacion, String idLote, int pesoKg, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_explotacion", idExplotacion);
        values.put("id_lote", idLote);
        values.put("peso", pesoKg);
        values.put("fecha", fecha);
        values.put("fecha_actualizacion", FechaUtils.obtenerFechaActual());
        values.put("sincronizado", 0);  // se marca como no sincronizado
        db.insert("pesar", null, values);
    }
    public Cursor obtenerPesosPorLoteYFecha(String idExplotacion, String idLote, String fecha) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, peso FROM pesar WHERE id_explotacion = ? AND id_lote = ? AND fecha = ? ORDER BY id DESC",
                new String[]{idExplotacion, idLote, fecha}
        );
    }
    public void eliminarPesoPorId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("pesar", "id = ?", new String[]{String.valueOf(id)});
    }

    public boolean eliminarExplotacionPorUUID(String uuidExplotacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("explotaciones", "id = ?", new String[]{uuidExplotacion});
        return filas > 0;
    }

    public List<Nota> obtenerNotasNoSincronizadas() {
        List<Nota> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notas WHERE sincronizado = 0", null);
        if (cursor.moveToFirst()) {
            do {
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
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public void marcarNotaComoSincronizada(String idNota) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        db.update("notas", values, "id = ?", new String[]{idNota});
    }
    public void insertarOActualizarNota(Nota nota) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM notas WHERE id = ?", new String[]{nota.getId()});
        boolean existe = cursor.moveToFirst();
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("id", nota.getId());
        values.put("id_lote", nota.getId_lote());
        values.put("id_explotacion", nota.getId_explotacion());
        values.put("fecha", nota.getFecha());
        values.put("observacion", nota.getObservacion());
        values.put("sincronizado", 1); // ya viene de Supabase
        values.put("fecha_actualizacion", nota.getFechaActualizacion());

        if (existe) {
            db.update("notas", values, "id = ?", new String[]{nota.getId()});
        } else {
            db.insert("notas", null, values);
        }
    }

    public Conteo obtenerConteoPorId(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM contar WHERE id = ?", new String[]{String.valueOf(id)});
        Conteo c = null;
        if (cursor.moveToFirst()) {
            c = new Conteo();
            c.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
            c.setId_explotacion(cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")));
            c.setId_lote(cursor.getString(cursor.getColumnIndexOrThrow("id_lote")));
            c.setnAnimales(cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales")));
            c.setObservaciones(cursor.getString(cursor.getColumnIndexOrThrow("observaciones")));
            c.setFecha(cursor.getString(cursor.getColumnIndexOrThrow("fecha")));
            c.setSincronizado(cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")));
            c.setFechaActualizacion(cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")));
        }
        cursor.close();
        return c;
    }

    public void eliminarConteoLocalmente(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("contar", "id = ?", new String[]{id});
        db.close();
    }

    public List<Accion> obtenerAccionesNoSincronizadas() {
        List<Accion> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM acciones WHERE sincronizado = 0 AND estado = 1",
                null
        );

        while (cursor.moveToNext()) {
            Accion a = new Accion(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_lote")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("tipoAccion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fechaAccion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales")),
                    cursor.getString(cursor.getColumnIndexOrThrow("observacion")),
                    0,
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")),
                    0,
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado"))

            );
            lista.add(a);
        }

        cursor.close();
        return lista;
    }

    public List<SalidasExplotacion> obtenerSalidasNoSincronizadas() {
        List<SalidasExplotacion> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM salidas WHERE sincronizado = 0 AND eliminado = 0", null);

        while (cursor.moveToNext()) {
            SalidasExplotacion salida = new SalidasExplotacion(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_lote")),
                    cursor.getString(cursor.getColumnIndexOrThrow("id_explotacion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("tipoSalida")),
                    cursor.getString(cursor.getColumnIndexOrThrow("tipoAlimentacion")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fechaSalida")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales")),
                    cursor.getString(cursor.getColumnIndexOrThrow("observacion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("sincronizado")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_actualizacion")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("eliminado")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fecha_eliminado"))
            );
            lista.add(salida);
        }

        cursor.close();
        return lista;
    }

    public void marcarSalidaComoSincronizada(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        db.update("salidas", values, "id = ?", new String[]{id});
    }

    public void insertarOActualizarSalidaDesdeServidor(SalidasExplotacion salida) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM salidas WHERE id = ?", new String[]{salida.getId()});

        ContentValues values = new ContentValues();
        values.put("id", salida.getId());
        values.put("id_lote", salida.getId_lote());
        values.put("id_explotacion", salida.getId_explotacion());
        values.put("tipoSalida", salida.getTipoSalida());
        values.put("tipoAlimentacion", salida.getTipoAlimentacion());
        values.put("fechaSalida", salida.getFechaSalida());
        values.put("nAnimales", salida.getnAnimales());
        values.put("observacion", salida.getObservacion());
        values.put("fecha_actualizacion", salida.getFechaActualizacion());
        values.put("sincronizado", 1);
        values.put("eliminado", salida.getEliminado());
        values.put("fecha_eliminado", salida.getFechaEliminado());

        if (cursor.moveToFirst()) {
            db.update("salidas", values, "id = ?", new String[]{salida.getId()});
        } else {
            db.insert("salidas", null, values);
        }

        cursor.close();
    }
    public String obtenerUltimaFechaActualizacion(String tabla) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fecha = "2000-01-01T00:00:00";

        Cursor cursor = db.rawQuery(
                "SELECT MAX(fecha_actualizacion) as ultima_fecha FROM " + tabla + " WHERE sincronizado = 1",
                null
        );

        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            fecha = cursor.getString(0);
        }

        cursor.close();
        return fecha;
    }


}




