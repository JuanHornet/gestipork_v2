package com.example.gestipork_v2.sync;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.gestipork_v2.data.ConstantesPrefs;

public class SincronizadorGeneral {

    private final Context context;

    public SincronizadorGeneral(Context context) {
        this.context = context;
    }

    public void sincronizarTodo() {
        // Obtener token y apikey desde SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(ConstantesPrefs.PREFS_LOGIN, Context.MODE_PRIVATE);
        String token = prefs.getString(ConstantesPrefs.PREFS_SUPABASE_TOKEN, null);
        String apiKey = prefs.getString(ConstantesPrefs.PREFS_SUPABASE_APIKEY, null);

        if (token == null || apiKey == null) {
            return; // No hay credenciales, no se puede sincronizar
        }

        String authHeader = "Bearer " + token;

        // Lotes tiene su propia clase con instancia
        new SincronizadorLotes(context).sincronizarLotes();

        // Resto sincronizadores
        SincronizadorCubriciones.sincronizar(context, authHeader, apiKey);
        SincronizadorParideras.sincronizar(context, authHeader, apiKey);
        SincronizadorItaca.sincronizar(context, authHeader, apiKey);
    }
}

