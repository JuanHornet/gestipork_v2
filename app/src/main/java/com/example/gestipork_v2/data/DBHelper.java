package com.example.gestipork_v2.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.gestipork_v2.modelo.Conteo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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

    // TABLA ALIMENTACION
    private static final String CREATE_TABLE_ALIMENTACION = "CREATE TABLE alimentacion (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "tipoAlimentacion TEXT, " +
            "nAnimales INTEGER, " +
            "fechaInicioAlimentacion TEXT, " +
            "cod_lote TEXT, " +
            "cod_explotacion TEXT," +
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
            "observacion TEXT" +
            ")";

    // TABLA CONTEOS

    private static final String CREATE_TABLE_CONTAR = "CREATE TABLE contar (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_explotacion TEXT NOT NULL, " +
            "cod_lote TEXT NOT NULL, " +
            "nAnimales INTEGER NOT NULL, " +
            "fecha TEXT NOT NULL, " + // guardaremos fecha como String (yyyy-MM-dd)
            "observaciones TEXT" +
            ")";

    // TABLA PESAR
    private static final String CREATE_TABLE_PESAR = "CREATE TABLE pesar (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_explotacion TEXT NOT NULL, " +
            "cod_lote TEXT NOT NULL, " +
            "peso INTEGER NOT NULL, " +
            "fecha TEXT NOT NULL)";

    //TABLA NOTAS
    private static final String CREATE_TABLE_NOTAS = "CREATE TABLE notas (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cod_lote TEXT NOT NULL, " +
            "cod_explotacion TEXT NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "observacion TEXT NOT NULL)";

    //TABLA AFOROS
    private static final String CREATE_TABLE_AFORO = "CREATE TABLE IF NOT EXISTS aforo_explotacion (" +
            "cod_explotacion TEXT PRIMARY KEY, " +
            "aforo_maximo INTEGER)";



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

    // Obtener todas las explotaciones de un usuario
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
        itaca.put("DCER", "");
        itaca.put("cod_lote", codLote);
        itaca.put("cod_explotacion", codExplotacion);
        itaca.put("raza", raza);
        itaca.put("nAnimales", 0);
        itaca.put("nMadres", 0);
        itaca.put("nPadres", 0);
        itaca.put("fechaPNacimiento", "");
        itaca.put("fechaUltNacimiento", "");
        itaca.put("color", "Seleccione color de crotal");    // 👈 CAMBIO CLAVE
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


}
