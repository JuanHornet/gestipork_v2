package com.example.gestipork_v2.base;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FechaUtils {

    // Devuelve la fecha actual en formato ISO 8601 compatible con Supabase
    public static String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
