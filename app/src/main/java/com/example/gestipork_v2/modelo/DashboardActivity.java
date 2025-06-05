package com.example.gestipork_v2.modelo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.base.BaseActivity;
import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.login.LoginActivity;
import com.example.gestipork_v2.sync.SyncWorker;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.ExistingPeriodicWorkPolicy;

import java.util.concurrent.TimeUnit;

public class DashboardActivity extends BaseActivity implements NuevoExplotacionDialogFragment.OnExplotacionCreadaListener {

    private Spinner spinnerExplotaciones;
    private RecyclerView recyclerResumen;
    private DashboardAdapter adapter;
    private TextView txtVacio;
    private String codExplotacionSeleccionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Panel de control");
        }
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert));

        spinnerExplotaciones = findViewById(R.id.spinner_explotaciones);
        recyclerResumen = findViewById(R.id.recycler_resumen);
        txtVacio = findViewById(R.id.txtVacio);

        recyclerResumen.setLayoutManager(new LinearLayoutManager(this));

        cargarExplotaciones();

        //sincronizamos datos con supabase cada 15 minutos

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest syncRequest = new PeriodicWorkRequest.Builder(
                SyncWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "syncGestipork",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
        );

        Button btnSync = findViewById(R.id.btnSincronizar);
        btnSync.setOnClickListener(v -> sincronizarManualAhora());

        //sincronizarManualAhora(); // 🔁 prueba directa al arrancar la app

    }

    public void cargarExplotaciones() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = prefs.getString("userEmail", "");
        DBHelper dbHelper = new DBHelper(this);
        int idUsuario = dbHelper.obtenerIdUsuarioDesdeEmail(email);

        Cursor cursor = dbHelper.obtenerExplotacionesDeUsuario(idUsuario);
        List<String> listaExplotaciones = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                listaExplotaciones.add(cursor.getString(0)); // nombre
            } while (cursor.moveToNext());
        }
        cursor.close();

        boolean hayExplotaciones = !listaExplotaciones.isEmpty();
        txtVacio.setVisibility(hayExplotaciones ? View.GONE : View.VISIBLE);
        recyclerResumen.setVisibility(hayExplotaciones ? View.VISIBLE : View.GONE);

        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                listaExplotaciones
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExplotaciones.setAdapter(adapterSpinner);

        if (hayExplotaciones) {
            spinnerExplotaciones.setSelection(0);
            cargarCodExplotacionYActualizarDashboard();
        }

        spinnerExplotaciones.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                cargarCodExplotacionYActualizarDashboard();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void cargarCodExplotacionYActualizarDashboard() {
        // Evita error si aún no hay ítem seleccionado
        if (spinnerExplotaciones == null || spinnerExplotaciones.getSelectedItem() == null) {
            return;
        }

        String nombreSeleccionado = spinnerExplotaciones.getSelectedItem().toString();

        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = prefs.getString("userEmail", "");
        DBHelper dbHelper = new DBHelper(this);
        int idUsuario = dbHelper.obtenerIdUsuarioDesdeEmail(email);

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT cod_explotacion FROM explotaciones WHERE nombre = ? AND iduser = ?",
                new String[]{nombreSeleccionado, String.valueOf(idUsuario)}
        );

        if (cursor.moveToFirst()) {
            codExplotacionSeleccionada = cursor.getString(0);
            cursor.close();
            adapter = new DashboardAdapter(this, codExplotacionSeleccionada);
            recyclerResumen.setAdapter(adapter);
        } else {
            cursor.close();
            Toast.makeText(this, "Error al obtener código de explotación", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add_explotacion) {
            NuevoExplotacionDialogFragment dialog = new NuevoExplotacionDialogFragment();
            dialog.show(getSupportFragmentManager(), "NuevoExplotacionDialog");
            return true;
        } else if (id == R.id.menu_logout) {
            cerrarSesion();
            return true;
        } else if (id == R.id.menu_edit_explotacion) {
            editarExplotacionSeleccionada();
            return true;
        } else if (id == R.id.menu_delete_explotacion) {
            eliminarExplotacionSeleccionada();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion() {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        preferences.edit().clear().apply();

        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    private void editarExplotacionSeleccionada() {
        int posicion = spinnerExplotaciones.getSelectedItemPosition();
        if (posicion == -1) {
            Toast.makeText(this, "Selecciona una explotación", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombreActual = spinnerExplotaciones.getSelectedItem().toString();
        EditarExplotacionDialogFragment dialog = EditarExplotacionDialogFragment.newInstance(nombreActual);
        dialog.show(getSupportFragmentManager(), "EditarExplotacionDialog");
    }

    private void eliminarExplotacionSeleccionada() {
        int posicion = spinnerExplotaciones.getSelectedItemPosition();
        if (posicion == -1) {
            Toast.makeText(this, "Selecciona una explotación", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = spinnerExplotaciones.getSelectedItem().toString();
        EliminarExplotacionDialogFragment dialog = EliminarExplotacionDialogFragment.newInstance(nombre);
        dialog.show(getSupportFragmentManager(), "EliminarExplotacionDialog");
    }

    @Override
    public void onExplotacionCreada() {
        cargarExplotaciones();
    }

    public void irALotes() {
        if (codExplotacionSeleccionada == null || codExplotacionSeleccionada.isEmpty()) {
            Toast.makeText(this, "Selecciona una explotación", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombreSeleccionado = spinnerExplotaciones.getSelectedItem().toString();

        Intent intent = new Intent(this, LotesActivity.class);
        intent.putExtra("cod_explotacion", codExplotacionSeleccionada);
        intent.putExtra("nombre_explotacion", nombreSeleccionado);
        startActivity(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        cargarCodExplotacionYActualizarDashboard();
    }
    public void sincronizarManualAhora() {
        OneTimeWorkRequest syncNow = new OneTimeWorkRequest.Builder(SyncWorker.class).build();
        WorkManager.getInstance(this).enqueue(syncNow);
    }


}
