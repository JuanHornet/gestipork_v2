package com.example.gestipork_v2.modelo;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class NotasActivity extends AppCompatActivity {

    private String idLote, idExplotacion;
    private NotasAdapter adapter;
    private DBHelper dbHelper;
    private List<Nota> listaNotas;
    private TextView textoVacioNotas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        idLote = getIntent().getStringExtra("id_lote");
        idExplotacion = getIntent().getStringExtra("id_explotacion");

        dbHelper = new DBHelper(this);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_notas);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Notas");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        RecyclerView recyclerView = findViewById(R.id.recycler_notas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textoVacioNotas = findViewById(R.id.texto_vacio_notas);

        listaNotas = cargarNotas();
        adapter = new NotasAdapter(listaNotas);
        recyclerView.setAdapter(adapter);

        comprobarNotasVacias();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_add_nota) {
            NotasDialogFragment dialog = NotasDialogFragment.newInstance(idLote, idExplotacion);
            dialog.setOnNotaGuardadaListener(() -> {
                listaNotas.clear();
                listaNotas.addAll(cargarNotas());
                adapter.notifyDataSetChanged();
                comprobarNotasVacias();
            });
            dialog.show(getSupportFragmentManager(), "NotasDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<Nota> cargarNotas() {
        List<Nota> notas = new ArrayList<>();
        Cursor cursor = dbHelper.obtenerNotas(idExplotacion, idLote);
        if (cursor.moveToFirst()) {
            do {
                Nota nota = new Nota(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                notas.add(nota);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notas;
    }

    public void comprobarNotasVacias() {
        if (listaNotas.isEmpty()) {
            textoVacioNotas.setVisibility(View.VISIBLE);
        } else {
            textoVacioNotas.setVisibility(View.GONE);
        }
    }
}
