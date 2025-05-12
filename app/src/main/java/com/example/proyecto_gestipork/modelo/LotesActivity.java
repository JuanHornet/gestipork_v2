package com.example.proyecto_gestipork.modelo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.base.BaseActivity;
import com.example.proyecto_gestipork.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class LotesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LoteAdapter adapter;
    private List<Lotes> listaLotes;
    private DBHelper dbHelper;

    private TextView txtVacio;
    private String codExplotacionSeleccionada;
    private String codLoteSeleccionado = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lotes);

        codExplotacionSeleccionada = getIntent().getStringExtra("cod_explotacion");
        Log.d("LotesActivity", "Recibido cod_explotacion: " + codExplotacionSeleccionada);

        if (codExplotacionSeleccionada == null || codExplotacionSeleccionada.isEmpty()) {
            Toast.makeText(this, "No se recibió cod_explotacion", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_lotes);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // RecyclerView
        recyclerView = findViewById(R.id.recycler_lotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtVacio = findViewById(R.id.txt_vacio);

        dbHelper = new DBHelper(this);
        listaLotes = new ArrayList<>();
        cargarLotes();

        adapter = new LoteAdapter(this, listaLotes);
        recyclerView.setAdapter(adapter);
        adapter.setOnLoteClickListener(lote -> codLoteSeleccionado = lote.getCod_lote());

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        int id = item.getItemId();

        View parentLayout = findViewById(android.R.id.content); // ✅ necesario para Snackbar

        if (id == R.id.nav_contar) {
            if (codExplotacionSeleccionada != null && codLoteSeleccionado != null) {
                Intent intent = new Intent(this, ContarActivity.class);
                intent.putExtra("cod_explotacion", codExplotacionSeleccionada);
                intent.putExtra("cod_lote", codLoteSeleccionado);
                startActivity(intent);
            } else {
                Snackbar.make(parentLayout,
                        "Debes seleccionar primero un lote (mantén pulsado sobre uno)",
                        Snackbar.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.nav_pesar) {
            if (codExplotacionSeleccionada != null && codLoteSeleccionado != null) {
                Intent intent = new Intent(this, com.example.proyecto_gestipork.modelo.CargarPesosActivity.class);
                intent.putExtra("cod_explotacion", codExplotacionSeleccionada);
                intent.putExtra("cod_lote", codLoteSeleccionado);
                startActivity(intent);
            } else {
                Snackbar.make(parentLayout,
                        "Debes seleccionar primero un lote (mantén pulsado sobre uno)",
                        Snackbar.LENGTH_LONG).show();
            }
            return true;
        }


        if (id == R.id.nav_baja) {
            if (codExplotacionSeleccionada != null && codLoteSeleccionado != null) {
                BajaDialogFragment dialog = BajaDialogFragment.newInstance(codLoteSeleccionado, codExplotacionSeleccionada);
                dialog.show(getSupportFragmentManager(), "BajaDialogFragment");
            } else {
                Snackbar.make(parentLayout,
                        "Debes seleccionar primero un lote (mantén pulsado sobre uno)",
                        Snackbar.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.nav_notas) {
            if (codExplotacionSeleccionada != null && codLoteSeleccionado != null) {
                Intent intent = new Intent(this, NotasActivity.class);
                intent.putExtra("cod_explotacion", codExplotacionSeleccionada);
                intent.putExtra("cod_lote", codLoteSeleccionado);
                startActivity(intent);
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                        "Debes seleccionar primero un lote (mantén pulsado sobre uno)",
                        Snackbar.LENGTH_LONG).show();
            }
            return true;
        }


        return false;
    };



    private List<String> obtenerLotesExplotacion() {
        List<String> lotes = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT cod_lote FROM lotes WHERE estado = 1 AND cod_explotacion = ?",
                new String[]{codExplotacionSeleccionada}
        );
        if (cursor.moveToFirst()) {
            do {
                lotes.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lotes;
    }

    private void cargarLotes() {
        listaLotes.clear();

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT l.id, l.cod_explotacion, l.nDisponibles, l.nIniciales, l.cod_lote, l.cod_paridera, " +
                        "l.cod_cubricion, l.cod_itaca, l.raza, l.estado, i.color " +
                        "FROM lotes l LEFT JOIN itaca i ON l.cod_itaca = i.cod_itaca " +
                        "WHERE l.estado = 1 AND l.cod_explotacion = ?",
                new String[]{codExplotacionSeleccionada}
        );

        if (cursor.moveToFirst()) {
            do {
                Lotes lote = new Lotes();
                lote.setId(cursor.getInt(0));
                lote.setCod_explotacion(cursor.getString(1));
                lote.setnDisponibles(cursor.getInt(2));
                lote.setnIniciales(cursor.getInt(3));
                lote.setCod_lote(cursor.getString(4));
                lote.setCod_paridera(cursor.getString(5));
                lote.setCod_cubricion(cursor.getString(6));
                lote.setCod_itaca(cursor.getString(7));
                lote.setRaza(cursor.getString(8));
                lote.setEstado(cursor.getInt(9) == 1);
                lote.setColor(cursor.getString(10));
                listaLotes.add(0, lote);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (listaLotes.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            txtVacio.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            txtVacio.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lotes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add_lote) {
            // ✅ LLAMADA AL NUEVO FRAGMENT
            NuevoLoteDialogFragment dialog = NuevoLoteDialogFragment.newInstance(codExplotacionSeleccionada);
            dialog.show(getSupportFragmentManager(), "NuevoLoteDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 👇 Este método lo usa NuevoLoteDialogFragment para refrescar
    public void recargarLotes() {
        cargarLotes();
        adapter.notifyDataSetChanged();
    }
}
