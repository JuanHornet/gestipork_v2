package com.example.proyecto_gestipork.base;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.MenuRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.proyecto_gestipork.R;
import com.google.android.material.appbar.MaterialToolbar;


//esta clase hereda de AppCompatActivity
public abstract class BaseActivity extends AppCompatActivity {

    protected MaterialToolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Configura la Toolbar reutilizable con título, icono atrás y menú.
     *
     * @param toolbar         Referencia a la Toolbar (usualmente id: toolbar_estandar)
     * @param titulo          Título que se muestra
     * @param menuResId       ID del menú XML a inflar (puede ser 0 si no quieres menú)
     * @param mostrarAtras    Si true, muestra la flecha de retroceso
     */
    protected void configurarToolbar(MaterialToolbar toolbar, String titulo, @MenuRes int menuResId, boolean mostrarAtras) {
        this.toolbar = toolbar;
        toolbar.setTitle(titulo);
        setSupportActionBar(toolbar);

        // Icono overflow blanco (tres puntos)
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert));

        // Menú si lo hay
        if (menuResId != 0) {
            toolbar.getMenu().clear();
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(menuResId, toolbar.getMenu());
        }

        if (mostrarAtras) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }
}
