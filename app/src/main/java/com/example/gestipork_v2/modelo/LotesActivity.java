package com.example.gestipork_v2.modelo;

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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.base.BaseActivity;
import com.example.gestipork_v2.data.DBHelper;
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
    private String idExplotacionSeleccionada;
    private String idLoteSeleccionado = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lotes);

        idExplotacionSeleccionada = getIntent().getStringExtra("id_explotacion");
        Log.d("LotesActivity", "Recibido id_explotacion: " + idExplotacionSeleccionada);

        if (idExplotacionSeleccionada == null || idExplotacionSeleccionada.isEmpty()) {
            Toast.makeText(this, "No se recibió cod_explotacion", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Toolbar

        String nombreExplotacion = getIntent().getStringExtra("nombre_explotacion");
        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lotes · " + nombreExplotacion);
        toolbar.setNavigationOnClickListener(v -> finish());

        // BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_lotes);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // RecyclerView
        recyclerView = findViewById(R.id.recycler_lotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtVacio = findViewById(R.id.txtVacio);

        dbHelper = new DBHelper(this);
        listaLotes = new ArrayList<>();
        cargarLotes();

        adapter = new LoteAdapter(this, listaLotes);
        recyclerView.setAdapter(adapter);
        adapter.setOnLoteClickListener(lote -> idLoteSeleccionado = lote.getNombre_lote());




    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        int id = item.getItemId();

        View parentLayout = findViewById(android.R.id.content); //necesario para Snackbar

        if (id == R.id.nav_contar) {
            if (idExplotacionSeleccionada != null && idLoteSeleccionado != null) {
                Intent intent = new Intent(this, ContarActivity.class);
                intent.putExtra("id_explotacion", idExplotacionSeleccionada);
                intent.putExtra("id_lote", idLoteSeleccionado);
                startActivity(intent);
            } else {
                Snackbar.make(parentLayout,
                        "Debes seleccionar primero un lote (mantén pulsado sobre uno)",
                        Snackbar.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.nav_pesar) {
            if (idExplotacionSeleccionada != null && idLoteSeleccionado != null) {
                Intent intent = new Intent(this, com.example.gestipork_v2.modelo.CargarPesosActivity.class);
                intent.putExtra("id_explotacion", idExplotacionSeleccionada);
                intent.putExtra("id_lote", idLoteSeleccionado);
                startActivity(intent);
            } else {
                Snackbar.make(parentLayout,
                        "Debes seleccionar primero un lote (mantén pulsado sobre uno)",
                        Snackbar.LENGTH_LONG).show();
            }
            return true;
        }


        if (id == R.id.nav_baja) {
            if (idExplotacionSeleccionada != null && idLoteSeleccionado != null) {
                BajaDialogFragment dialog = BajaDialogFragment.newInstance(idLoteSeleccionado, idExplotacionSeleccionada);
                dialog.show(getSupportFragmentManager(), "BajaDialogFragment");
            } else {
                Snackbar.make(parentLayout,
                        "Debes seleccionar primero un lote (mantén pulsado sobre uno)",
                        Snackbar.LENGTH_LONG).show();
            }
            return true;
        }

        if (id == R.id.nav_notas) {
            if (idExplotacionSeleccionada != null && idLoteSeleccionado != null) {
                Intent intent = new Intent(this, NotasActivity.class);
                intent.putExtra("id_explotacion", idExplotacionSeleccionada);
                intent.putExtra("id_lote", idLoteSeleccionado);
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
                "SELECT id FROM lotes WHERE estado = 1 AND id_explotacion = ?",
                new String[]{idExplotacionSeleccionada}
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
                "SELECT id, id_explotacion, nDisponibles, nIniciales, nombre_lote, cod_paridera, " +
                        "cod_cubricion, cod_itaca, raza, estado, color " +                     // lee lotes.color
                        "FROM lotes " +
                        "WHERE estado = 1 AND id_explotacion = ?",
                new String[]{idExplotacionSeleccionada}
        );


        if (cursor.moveToFirst()) {
            do {
                Lotes lote = new Lotes();
                lote.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
                lote.setId_explotacion(cursor.getString(1));
                lote.setnDisponibles(cursor.getInt(2));
                lote.setnIniciales(cursor.getInt(3));
                lote.setNombre_lote(cursor.getString(4));
                lote.setCod_paridera(cursor.getString(5));
                lote.setCod_cubricion(cursor.getString(6));
                lote.setCod_itaca(cursor.getString(7));
                lote.setRaza(cursor.getString(8));
                lote.setEstado(cursor.getInt(9));
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
            // LLAMADA AL NUEVO FRAGMENT
            NuevoLoteDialogFragment dialog = NuevoLoteDialogFragment.newInstance(idExplotacionSeleccionada);
            dialog.show(getSupportFragmentManager(), "NuevoLoteDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Este método lo usa NuevoLoteDialogFragment para refrescar
    public void recargarLotes() {
        cargarLotes();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recargarLotes();    //fuerza a recargar siempre al volver
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            cargarLotes();
            adapter.notifyDataSetChanged();
        }
    }



}
