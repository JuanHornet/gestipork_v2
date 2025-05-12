package com.example.proyecto_gestipork.modelo;


import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PesarActivity extends AppCompatActivity {

    private String codExplotacion, codLote;
    private DBHelper dbHelper;
    private PesarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesar);

        codExplotacion = getIntent().getStringExtra("cod_explotacion");
        codLote = getIntent().getStringExtra("cod_lote");

        dbHelper = new DBHelper(this);

        RecyclerView recyclerView = findViewById(R.id.recycler_pesajes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PesarAdapter(this, codExplotacion, codLote, obtenerFechasPesajes());
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab_add_peso);
        fab.setOnClickListener(v -> {
            PesarDialogFragment dialog = PesarDialogFragment.newInstance(codExplotacion, codLote);
            dialog.show(getSupportFragmentManager(), "PesarDialog");
        });
        MaterialToolbar toolbar = findViewById(R.id.toolbar_pesar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Histórico de Pesos");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

    }

    private List<String> obtenerFechasPesajes() {
        List<String> fechas = new ArrayList<>();
        Cursor cursor = dbHelper.obtenerPesosPorLote(codExplotacion, codLote);
        if (cursor.moveToFirst()) {
            do {
                fechas.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return fechas;
    }
}
