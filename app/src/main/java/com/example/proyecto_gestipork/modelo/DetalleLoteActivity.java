package com.example.proyecto_gestipork.modelo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.base.BaseActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class DetalleLoteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_lote);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert));


        // Mostrar flecha hacia atrás
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle del Lote");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_lote, menu);
        return true;
    }


    // Acciones del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Flecha de retroceso
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        // Opciones del menú
        if (id == R.id.menu_editar_lote) {
            Toast.makeText(this, "Editar Lote", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_eliminar_lote) {
            Toast.makeText(this, "Eliminar Lote", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_ver_historial) {
            Toast.makeText(this, "Ver Historial", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }
}
