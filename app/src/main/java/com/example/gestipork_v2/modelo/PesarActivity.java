package com.example.gestipork_v2.modelo;

import android.database.Cursor;
import android.os.Bundle;
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

public class PesarActivity extends AppCompatActivity {

    private String idExplotacion, idLote;
    private DBHelper dbHelper;
    private PesarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesar);

        // Recibir UUIDs
        idExplotacion = getIntent().getStringExtra("cod_explotacion");
        idLote = getIntent().getStringExtra("id_lote");

        dbHelper = new DBHelper(this);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_pesar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Histórico de Pesajes");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_pesajes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Obtener fechas de pesajes
        List<String> fechas = obtenerFechasPesajes();

        // Adaptador con UUIDs
        adapter = new PesarAdapter(this, idExplotacion, idLote, fechas);
        recyclerView.setAdapter(adapter);

        TextView textEmpty = findViewById(R.id.textEmptyPesajes);

        if (fechas.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            textEmpty.setVisibility(View.GONE);
        }
    }

    private List<String> obtenerFechasPesajes() {
        List<String> fechas = new ArrayList<>();
        Cursor cursor = dbHelper.obtenerFechasPesajes(idExplotacion, idLote);
        if (cursor.moveToFirst()) {
            do {
                fechas.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return fechas;
    }
}
