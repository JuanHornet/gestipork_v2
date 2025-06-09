package com.example.gestipork_v2.data;


public class ConstantesDB {

    // Tabla usuarios
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String COL_USUARIO_ID = "id";
    public static final String COL_USUARIO_EMAIL = "email";
    public static final String COL_USUARIO_PASSWORD = "password";

    // Tabla explotaciones
    public static final String TABLA_EXPLOTACIONES = "explotaciones";
    public static final String COL_EXPLOTACION_ID = "id";
    public static final String COL_EXPLOTACION_ID_USUARIO = "id_usuario";
    public static final String COL_EXPLOTACION_COD = "cod_explotacion";
    public static final String COL_EXPLOTACION_NOMBRE = "nombre";
    public static final String COL_EXPLOTACION_SINCRONIZADO = "sincronizado";
    public static final String COL_EXPLOTACION_FECHA_ACT = "fecha_actualizacion";

    // Aquí puedes seguir añadiendo constantes para otras tablas (lotes, acciones, etc.)
}
