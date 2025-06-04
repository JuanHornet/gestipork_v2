package com.example.gestipork_v2.sync;

import android.content.Context;

public class SincronizadorGeneral {

    private final Context context;

    public SincronizadorGeneral(Context context) {
        this.context = context;
    }

    public void sincronizarTodo() {
        new SincronizadorLotes(context).sincronizarLotes();  // más adelante añadimos otras
    }
}
