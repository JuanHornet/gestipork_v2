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
import com.example.gestipork_v2.modelo.Lotes;
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
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Header;




public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "gestipork.db";
    private static final int DB_VERSION = 4;

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
            "cod_explotacion TEXT, " + // Valor derivado
            "nombre TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT)";


    // TABLA LOTES
    private static final String CREATE_TABLE_LOTES = "CREATE TABLE lotes (" +
            "id TEXT PRIMARY KEY, " +
            "cod_explotacion TEXT, " +
            "nDisponibles INTEGER, " +
            "nIniciales INTEGER, " +
            "cod_lote TEXT, " +
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
            "cod_lote TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "cod_explotacion TEXT " +
            ")";


    // TABLA CUBRICIONES
    private static final String CREATE_TABLE_CUBRICIONES = "CREATE TABLE cubriciones (" +
            "id TEXT PRIMARY KEY, " +
            "cod_cubricion TEXT, " +
            "nMadres INTEGER, " +
            "nPadres INTEGER, " +
            "fechaInicioCubricion TEXT, " +  // Usamos TEXT para fechas
            "fechaFinCubricion TEXT, " +
            "cod_explotacion TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "cod_lote TEXT" +
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
            "cod_lote TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "cod_explotacion TEXT" +
            ")";

    // TABLA ALIMENTACION
    private static final String CREATE_TABLE_ALIMENTACION = "CREATE TABLE alimentacion (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "tipoAlimentacion TEXT, " +
            "nAnimales INTEGER, " +
            "fechaInicioAlimentacion TEXT, " +
            "cod_lote TEXT, " +
            "cod_explotacion TEXT," +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "UNIQUE(cod_lote, cod_explotacion, tipoAlimentacion)" +
            ")";

    // TABLA ACCIONES
    private static final String CREATE_TABLE_ACCIONES = "CREATE TABLE acciones (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "tipoAccion TEXT, " +
            "nAnimales INTEGER, " +
            "fechaAccion TEXT, " +
            "cod_lote TEXT, " +
            "cod_explotacion TEXT, " +
            "observacion TEXT, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "estado INTEGER" +
            ")";

    // TABLA SALIDAS
    private static final String CREATE_TABLE_SALIDAS = "CREATE TABLE salidas (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "tipoSalida TEXT, " +
            "nAnimales INTEGER, " +
            "fechaSalida TEXT, " +
            "cod_lote TEXT, " +
            "cod_explotacion TEXT," +
            "tipoAlimentacion TEXT," +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "observacion TEXT" +
            ")";

    // TABLA CONTEOS

    private static final String CREATE_TABLE_CONTAR = "CREATE TABLE contar (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_explotacion TEXT NOT NULL, " +
            "cod_lote TEXT NOT NULL, " +
            "nAnimales INTEGER NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +// guardaremos fecha como String (yyyy-MM-dd)
            "observaciones TEXT" +
            ")";

    // TABLA PESAR
    private static final String CREATE_TABLE_PESAR = "CREATE TABLE pesar (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_explotacion TEXT NOT NULL, " +
            "cod_lote TEXT NOT NULL, " +
            "peso INTEGER NOT NULL, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "fecha TEXT NOT NULL)";

    //TABLA NOTAS
    private static final String CREATE_TABLE_NOTAS = "CREATE TABLE notas (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_lote TEXT NOT NULL, " +
            "cod_explotacion TEXT NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "observacion TEXT NOT NULL)";

    //TABLA AFOROS
    private static final String CREATE_TABLE_AFORO = "CREATE TABLE IF NOT EXISTS aforo_explotacion (" +
            "cod_explotacion TEXT PRIMARY KEY, " +
            "sincronizado INTEGER DEFAULT 0, " +
            "fecha_actualizacion TEXT, " +
            "aforo_maximo INTEGER)";



    public boolean registrarUsuario(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        String id = UUID.randomUUID().toString();
        String hashedPassword = hashPassword(password);
        String fechaActualizacion = obtenerFechaActual(); // método auxiliar

        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("email", email);
        values.put("password", hashedPassword);
        values.put("fecha_actualizacion", fechaActualizacion);
        values.put("sincronizado", 0); // aún no sincronizado

        long result = db.insert("usuarios", null, values);
        return result != -1;
    }

    public boolean registrarUsuarioConUUID(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", usuario.getId());
        values.put("email", usuario.getEmail());
        values.put("password", usuario.getPassword());
        values.put("fecha_actualizacion", obtenerFechaActual());
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


    public String obtenerUuidUsuarioDesdeEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM usuarios WHERE email = ?", new String[]{email});
        if (cursor.moveToFirst()) {
            String uuid = cursor.getString(0);
            cursor.close();
            return uuid;
        }
        cursor.close();
        return null;
    }





    public boolean insertarExplotacionNueva(String nombre, String uuidUsuario, String uuidExplotacion, String codExplotacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", uuidExplotacion);
        values.put("nombre", nombre);
        values.put("id_usuario", uuidUsuario);
        values.put("cod_explotacion", codExplotacion);
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

    public boolean actualizarNombreExplotacion(String nombreViejo, String nombreNuevo, String uuidUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", nombreNuevo);

        int filas = db.update("explotaciones", values,
                "nombre = ? AND id_usuario = ?", new String[]{nombreViejo, uuidUsuario});

        return filas > 0;
    }

    public boolean eliminarExplotacionPorNombre(String nombre, String uuidUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        int filas = db.delete("explotaciones", "nombre = ? AND id_usuario = ?", new String[]{nombre, uuidUsuario});
        return filas > 0;
    }

    public void insertarRegistrosRelacionadosLote(Context context, SQLiteDatabase db, String uuidLote, String codExplotacion, String raza) {
        // 1. Insertar en parideras
        String codParidera = "P" + uuidLote + codExplotacion;
        ContentValues paridera = new ContentValues();
        paridera.put("id", java.util.UUID.randomUUID().toString());
        paridera.put("cod_paridera", codParidera);
        paridera.put("cod_lote", uuidLote);
        paridera.put("cod_explotacion", codExplotacion);
        paridera.put("fechaInicioParidera", "");
        paridera.put("fechaFinParidera", "");
        paridera.put("nacidosVivos", 0);
        paridera.put("nParidas", 0);
        paridera.put("nVacias", 0);
        paridera.put("sincronizado", 0);
        paridera.put("fecha_actualizacion", com.example.gestipork_v2.base.FechaUtils.obtenerFechaActual());

        long resultadoParidera = db.insert("parideras", null, paridera);
        if (resultadoParidera == -1) {
            Toast.makeText(context, "Lote guardado, pero error en parideras", Toast.LENGTH_SHORT).show();
        }

        // 2. Insertar en cubriciones
        String codCubricion = "C" + uuidLote + codExplotacion;
        ContentValues cubricion = new ContentValues();
        cubricion.put("id", java.util.UUID.randomUUID().toString());
        cubricion.put("cod_cubricion", codCubricion);
        cubricion.put("cod_lote", uuidLote);
        cubricion.put("cod_explotacion", codExplotacion);
        cubricion.put("nMadres", 0);
        cubricion.put("nPadres", 0);
        cubricion.put("fechaInicioCubricion", "");
        cubricion.put("fechaFinCubricion", "");
        cubricion.put("sincronizado", 0);
        cubricion.put("fecha_actualizacion", com.example.gestipork_v2.base.FechaUtils.obtenerFechaActual());

        long resultadoCubricion = db.insert("cubriciones", null, cubricion);
        if (resultadoCubricion == -1) {
            Toast.makeText(context, "Lote guardado, pero error en cubriciones", Toast.LENGTH_SHORT).show();
        }

        // 3. Insertar en itaca
        String codItaca = "I" + uuidLote + codExplotacion;
        ContentValues itaca = new ContentValues();
        itaca.put("id", java.util.UUID.randomUUID().toString());
        itaca.put("cod_itaca", codItaca);
        itaca.put("cod_lote", uuidLote);
        itaca.put("cod_explotacion", codExplotacion);
        itaca.put("raza", raza);
        itaca.put("color", "");
        itaca.put("nMadres", 0);
        itaca.put("nPadres", 0);
        itaca.put("nAnimales", 0);
        itaca.put("fechaPrimerNacimiento", "");
        itaca.put("fechaUltimoNacimiento", "");
        itaca.put("crotalesSolicitados", "");
        itaca.put("sincronizado", 0);
        itaca.put("fecha_actualizacion", com.example.gestipork_v2.base.FechaUtils.obtenerFechaActual());

        long resultadoItaca = db.insert("itaca", null, itaca);
        if (resultadoItaca == -1) {
            Toast.makeText(context, "Lote guardado, pero error en Itaca", Toast.LENGTH_SHORT).show();
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

    public Cursor obtenerAcciones(String codLote, String codExplotacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM acciones WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion});
    }
    public void insertarAccion(String tipo, int cantidad, String fecha, String codLote, String codExplotacion, String observacion) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("tipoAccion", tipo);
        values.put("nAnimales", cantidad);
        values.put("fechaAccion", fecha);
        values.put("cod_lote", codLote);
        values.put("cod_explotacion", codExplotacion);
        values.put("observacion", observacion);
        values.put("estado", 1);

        db.insert("acciones", null, values);

        // actualizar lote y alimentacion si es DESTETE
        if (tipo.equalsIgnoreCase("Destete")) {
            // 1. Actualizar lote
            ContentValues loteUpdate = new ContentValues();
            loteUpdate.put("nDisponibles", cantidad);
            loteUpdate.put("nIniciales", cantidad);

            db.update("lotes", loteUpdate,
                    "cod_lote = ? AND cod_explotacion = ?",
                    new String[]{codLote, codExplotacion});

            // 2. Insertar registros de alimentación
            String[] tipos = {"Bellota", "Cebo Campo", "Cebo"};
            for (String tipoAlim : tipos) {
                int nAnimales = tipoAlim.equals("Cebo") ? cantidad : 0;

                values = new ContentValues();
                values.put("tipoAlimentacion", tipoAlim);
                values.put("cod_lote", codLote);
                values.put("cod_explotacion", codExplotacion);
                values.put("nAnimales", nAnimales);
                values.put("fechaInicioAlimentacion", fecha);

                db.insertWithOnConflict("alimentacion", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }

        }

    }

    public void actualizarAccion(int id, String tipo, int cantidad, String fecha, String observacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tipoAccion", tipo);
        values.put("nAnimales", cantidad);
        values.put("fechaAccion", fecha);
        values.put("observacion", observacion);
        db.update("acciones", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public boolean eliminarAccion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Buscar tipoAccion, cod_lote y cod_explotacion antes de eliminar
        Cursor c = db.rawQuery("SELECT tipoAccion, cod_lote, cod_explotacion FROM acciones WHERE id = ?",
                new String[]{String.valueOf(id)});

        if (c.moveToFirst()) {
            String tipo = c.getString(0);
            String codLote = c.getString(1);
            String codExplotacion = c.getString(2);

            // Si es Destete, poner a cero los animales
            if (tipo.equalsIgnoreCase("Destete")) {
                ContentValues values = new ContentValues();
                values.put("nDisponibles", 0);
                values.put("nIniciales", 0);
                db.update("lotes", values, "cod_lote = ? AND cod_explotacion = ?", new String[]{codLote, codExplotacion});
            }
        }
        c.close();

        // eliminar la acción
        int filas = db.delete("acciones", "id = ?", new String[]{String.valueOf(id)});
        db.close();

        return filas > 0;
    }

    public void insertarSalida(String tipoSalida, String tipoAlimentacion, int nAnimales,
                               String fechaSalida, String codLote, String codExplotacion, String observacion) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("tipoSalida", tipoSalida);
        values.put("tipoAlimentacion", tipoAlimentacion);
        values.put("nAnimales", nAnimales);
        values.put("fechaSalida", fechaSalida);
        values.put("cod_lote", codLote);
        values.put("cod_explotacion", codExplotacion);
        values.put("observacion", observacion);

        db.insert("salidas", null, values);

        // restar animales al lote y alimentación
        int disponibles = obtenerAnimalesDisponiblesLote(codLote, codExplotacion);
        int nuevosDisponibles = Math.max(0, disponibles - nAnimales);

        ContentValues loteUpdate = new ContentValues();
        loteUpdate.put("nDisponibles", nuevosDisponibles);
        db.update("lotes", loteUpdate,
                "cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion});

        // Restar animales en alimentación
        restarAnimalesAlimentacion(codLote, codExplotacion, tipoAlimentacion, nAnimales);

        db.close();
    }



    public void actualizarSalida(int id, String tipoSalida, String tipoAlimentacion, int nAnimales,
                                 String fechaSalida, String observacion) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Recuperar la salida anterior para saber qué cantidad había
        Cursor cursor = db.rawQuery("SELECT nAnimales, cod_lote, cod_explotacion, tipoAlimentacion FROM salidas WHERE id = ?",
                new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            int cantidadAnterior = cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales"));
            String codLote = cursor.getString(cursor.getColumnIndexOrThrow("cod_lote"));
            String codExplotacion = cursor.getString(cursor.getColumnIndexOrThrow("cod_explotacion"));
            String alimentacionAnterior = cursor.getString(cursor.getColumnIndexOrThrow("tipoAlimentacion"));

            // Revertir la salida anterior (sumar de nuevo los animales que se quitaron)
            sumarAnimalesAlimentacion(codLote, codExplotacion, alimentacionAnterior, cantidadAnterior);
            int disponiblesActuales = obtenerAnimalesDisponiblesLote(codLote, codExplotacion);
            ContentValues loteUpdate = new ContentValues();
            loteUpdate.put("nDisponibles", disponiblesActuales + cantidadAnterior);
            db.update("lotes", loteUpdate, "cod_lote = ? AND cod_explotacion = ?",
                    new String[]{codLote, codExplotacion});

            // Actualizar la salida con los nuevos datos
            ContentValues values = new ContentValues();
            values.put("tipoSalida", tipoSalida);
            values.put("tipoAlimentacion", tipoAlimentacion);
            values.put("nAnimales", nAnimales);
            values.put("fechaSalida", fechaSalida);
            values.put("observacion", observacion);
            db.update("salidas", values, "id = ?", new String[]{String.valueOf(id)});

            // Aplicar nueva salida (restar animales de nuevo)
            restarAnimalesAlimentacion(codLote, codExplotacion, tipoAlimentacion, nAnimales);
            int disponiblesFinal = obtenerAnimalesDisponiblesLote(codLote, codExplotacion);
            ContentValues loteUpdateFinal = new ContentValues();
            loteUpdateFinal.put("nDisponibles", Math.max(0, disponiblesFinal - nAnimales));
            db.update("lotes", loteUpdateFinal, "cod_lote = ? AND cod_explotacion = ?",
                    new String[]{codLote, codExplotacion});
        }

        cursor.close();
        db.close();
    }

    public Cursor obtenerSalidas(String codLote, String codExplotacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM salidas WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion});
    }

    // Obtener número de animales disponibles en el lote
    public int obtenerAnimalesDisponibles(String codLote, String codExplotacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nDisponibles FROM lotes WHERE cod_lote = ? AND cod_explotacion = ?", new String[]{codLote, codExplotacion});
        int cantidad = 0;
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }
        cursor.close();
        return cantidad;
    }

    // Obtener animales actuales en un tipo de alimentación
    public int obtenerAnimalesAlimentacion(String codLote, String codExplotacion, String tipoAlimentacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nAnimales FROM alimentacion WHERE cod_lote = ? AND cod_explotacion = ? AND tipoAlimentacion = ?", new String[]{codLote, codExplotacion, tipoAlimentacion});
        int cantidad = 0;
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }
        cursor.close();
        return cantidad;
    }

    // Restar animales de un tipo de alimentación
    public void restarAnimalesAlimentacion(String codLote, String codExplotacion, String tipoAlimentacion, int cantidad) {
        int actuales = obtenerAnimalesAlimentacion(codLote, codExplotacion, tipoAlimentacion);
        int nuevos = Math.max(0, actuales - cantidad);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nAnimales", nuevos);
        db.update("alimentacion", values, "cod_lote = ? AND cod_explotacion = ? AND tipoAlimentacion = ?", new String[]{codLote, codExplotacion, tipoAlimentacion});
    }

    // Sumar animales a un tipo de alimentación
    public void sumarAnimalesAlimentacion(String codLote, String codExplotacion, String tipoAlimentacion, int cantidad) {
        int actuales = obtenerAnimalesAlimentacion(codLote, codExplotacion, tipoAlimentacion);
        int nuevos = actuales + cantidad;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nAnimales", nuevos);
        db.update("alimentacion", values, "cod_lote = ? AND cod_explotacion = ? AND tipoAlimentacion = ?", new String[]{codLote, codExplotacion, tipoAlimentacion});
    }
    public void sumarAnimalesAlimentacionConFecha(String codLote, String codExplotacion, String tipoAlimentacion, int cantidad, String fecha) {
        int actuales = obtenerAnimalesAlimentacion(codLote, codExplotacion, tipoAlimentacion);
        int nuevos = actuales + cantidad;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nAnimales", nuevos);
        values.put("fechaInicioAlimentacion", fecha);
        db.update("alimentacion", values, "cod_lote = ? AND cod_explotacion = ? AND tipoAlimentacion = ?",
                new String[]{codLote, codExplotacion, tipoAlimentacion});
    }
    public List<Conteo> obtenerConteosLista(String codExplotacion, String codLote) {
        List<Conteo> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT id, cod_explotacion, cod_lote, nAnimales, observaciones, fecha " +
                        "FROM contar WHERE cod_explotacion = ? AND cod_lote = ? ORDER BY id DESC",
                new String[]{codExplotacion, codLote});

        if (cursor.moveToFirst()) {
            do {
                Conteo conteo = new Conteo();
                conteo.setId(cursor.getInt(0));
                conteo.setCodExplotacion(cursor.getString(1));
                conteo.setCodLote(cursor.getString(2));
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

    public void insertarConteo(String codExplotacion, String codLote, int nAnimales, String observaciones, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cod_explotacion", codExplotacion);
        values.put("cod_lote", codLote);
        values.put("nAnimales", nAnimales);
        values.put("observaciones", observaciones);
        values.put("fecha", fecha);
        db.insert("contar", null, values);
    }

    public int obtenerAnimalesDisponiblesLote(String codLote, String codExplotacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nDisponibles FROM lotes WHERE cod_lote = ? AND cod_explotacion = ?", new String[]{codLote, codExplotacion});
        int disponibles = 0;
        if (cursor.moveToFirst()) {
            disponibles = cursor.getInt(0);
        }
        cursor.close();
        return disponibles;
    }
    public void insertarPeso(String codExplotacion, String codLote, int peso, String fecha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cod_explotacion", codExplotacion);
        values.put("cod_lote", codLote);
        values.put("peso", peso);
        values.put("fecha", fecha);
        db.insert("pesar", null, values);
    }
    public Cursor obtenerFechasPesajes(String codExplotacion, String codLote) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT DISTINCT fecha FROM pesar WHERE cod_explotacion = ? AND cod_lote = ? ORDER BY fecha DESC",
                new String[]{codExplotacion, codLote});
    }
    public Cursor obtenerPesosDeFecha(String codExplotacion, String codLote, String fecha) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT peso FROM pesar WHERE cod_explotacion = ? AND cod_lote = ? AND fecha = ?",
                new String[]{codExplotacion, codLote, fecha});
    }
    // Insertar nueva nota
    public void insertarNota(String codLote, String codExplotacion, String fecha, String observacion) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cod_lote", codLote);
        values.put("cod_explotacion", codExplotacion);
        values.put("fecha", fecha);
        values.put("observacion", observacion);
        db.insert("notas", null, values);
    }

    // Obtener notas de un lote
    public Cursor obtenerNotas(String codExplotacion, String codLote) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM notas WHERE cod_explotacion = ? AND cod_lote = ? ORDER BY id DESC",
                new String[]{codExplotacion, codLote});
    }
    public Cursor obtenerPesosPorLoteYFecha(String codExplotacion, String codLote, String fecha) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, peso FROM pesar WHERE cod_explotacion = ? AND cod_lote = ? AND fecha = ? ORDER BY id ASC",
                new String[]{codExplotacion, codLote, fecha}
        );
    }

    // Eliminar un peso concreto por ID
    public void eliminarPesoPorId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("pesar", "id = ?", new String[]{String.valueOf(id)});
    }
    // Devuelve todos los lotes activos de una explotación (para poblar Spinner)
    public List<String> obtenerLotesActivos(String codExplotacion) {
        List<String> lotes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT cod_lote FROM lotes WHERE estado = 1 AND cod_explotacion = ? ORDER BY cod_lote ASC",
                new String[]{codExplotacion}
        );

        if (cursor.moveToFirst()) {
            do {
                lotes.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lotes;
    }
    public void guardarAforo(String codExplotacion, int aforoMaximo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cod_explotacion", codExplotacion);
        values.put("aforo_maximo", aforoMaximo);
        db.insertWithOnConflict("aforo_explotacion", null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int obtenerAforo(String codExplotacion) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT aforo_maximo FROM aforo_explotacion WHERE cod_explotacion = ?",
                new String[]{codExplotacion});

        int aforo = 0;
        if (cursor.moveToFirst()) {
            aforo = cursor.getInt(0);
        }
        cursor.close();
        return aforo;
    }

    public int obtenerAnimalesPorRaza(String codExplotacion, String raza) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(nDisponibles) FROM lotes WHERE cod_explotacion = ? AND raza = ? AND estado = 1",
                new String[]{codExplotacion, raza}
        );
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }
    public List<Lotes> obtenerLotesNoSincronizados() {
        List<Lotes> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM lotes WHERE sincronizado = 0", null);

        if (cursor.moveToFirst()) {
            do {
                Lotes lote = new Lotes();
                lote.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
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
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sincronizado", 1);
        values.put("fecha_actualizacion", fechaActualizacion);
        db.update("lotes", values, "id = ?", new String[]{String.valueOf(id)});
    }
    public void insertarOActualizarLote(Lotes lote) {
        SQLiteDatabase db = this.getWritableDatabase();
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
        values.put("sincronizado", 1); // si viene de la nube, ya está sincronizado
        values.put("fecha_actualizacion", lote.getFecha_actualizacion());

        if (cursor.moveToFirst()) {
            db.update("lotes", values, "id = ?", new String[]{String.valueOf(lote.getId())});
        } else {
            db.insert("lotes", null, values);
        }

        cursor.close();
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
            values.put("cod_explotacion", e.getCod_explotacion());
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

    }




