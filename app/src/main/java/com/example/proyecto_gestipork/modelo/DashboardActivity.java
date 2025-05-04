package com.example.proyecto_gestipork.modelo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;
import com.example.proyecto_gestipork.login.LoginActivity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private Spinner spinnerExplotaciones;
    private RecyclerView recyclerResumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        RecyclerView recyclerResumen = findViewById(R.id.recycler_resumen);
        recyclerResumen.setLayoutManager(new LinearLayoutManager(this));
        DashboardAdapter adapterD = new DashboardAdapter(DashboardActivity.this);
        recyclerResumen.setAdapter(adapterD);


        Toolbar toolbar = findViewById(R.id.toolbar_dashboard);
        setSupportActionBar(toolbar);
        // Cambiar el color del icono de los tres puntos (overflow)
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert));



        // Referencia ya creada arriba
        spinnerExplotaciones = findViewById(R.id.spinner_explotaciones);


        // Obtener ID del usuario logado
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = prefs.getString("userEmail", "");
        DBHelper dbHelper = new DBHelper(this);
        int idUsuario = dbHelper.obtenerIdUsuarioDesdeEmail(email);

        // Obtener explotaciones de ese usuario
        Cursor cursor = dbHelper.obtenerExplotacionesDeUsuario(idUsuario);
        List<String> listaExplotaciones = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(0); // nombre está en la primera columna
                listaExplotaciones.add(nombre);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Cargar en el Spinner con texto grande
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item, // tu layout personalizado con textSize 18sp
                listaExplotaciones
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExplotaciones.setAdapter(adapter);

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
            mostrarDialogoNuevaExplotacion();
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
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void mostrarDialogoNuevaExplotacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva Explotación");

        // Campo de entrada
        final EditText input = new EditText(this);
        input.setHint("Nombre de la explotación");
        input.setPadding(50, 40, 50, 40);
        builder.setView(input);

        // Botón Guardar
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = input.getText().toString().trim();

            if (!nombre.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                String email = prefs.getString("userEmail", "");
                DBHelper dbHelper = new DBHelper(this);
                int idUsuario = dbHelper.obtenerIdUsuarioDesdeEmail(email);

                boolean insertado = dbHelper.insertarExplotacion(nombre, idUsuario);
                if (insertado) {
                    Toast.makeText(this, "Explotación guardada", Toast.LENGTH_SHORT).show();
                    recargarSpinnerExplotaciones(); // ¡Refrescamos!
                } else {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Introduce un nombre", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
    private void recargarSpinnerExplotaciones() {
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                listaExplotaciones
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExplotaciones.setAdapter(adapter);
    }

    private void cerrarSesion() {
        SharedPreferences preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Borra todos los datos de la sesión
        editor.apply();

        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia historial
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Animación opcional
    }

    private void editarExplotacionSeleccionada() {
        int posicion = spinnerExplotaciones.getSelectedItemPosition();
        if (posicion == -1) {
            Toast.makeText(this, "Selecciona una explotación", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombreActual = spinnerExplotaciones.getSelectedItem().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Explotación");

        final EditText input = new EditText(this);
        input.setText(nombreActual);
        input.setPadding(50, 40, 50, 40);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = input.getText().toString().trim();
            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            String email = prefs.getString("userEmail", "");
            DBHelper dbHelper = new DBHelper(this);
            int idUsuario = dbHelper.obtenerIdUsuarioDesdeEmail(email);

            boolean actualizado = dbHelper.actualizarNombreExplotacion(nombreActual, nuevoNombre, idUsuario);
            if (actualizado) {
                Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                recargarSpinnerExplotaciones();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void eliminarExplotacionSeleccionada() {
        int posicion = spinnerExplotaciones.getSelectedItemPosition();
        if (posicion == -1) {
            Toast.makeText(this, "Selecciona una explotación", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = spinnerExplotaciones.getSelectedItem().toString();

        new AlertDialog.Builder(this)
                .setTitle("Eliminar Explotación")
                .setMessage("¿Seguro que deseas eliminar \"" + nombre + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
                    String email = prefs.getString("userEmail", "");
                    DBHelper dbHelper = new DBHelper(this);
                    int idUsuario = dbHelper.obtenerIdUsuarioDesdeEmail(email);

                    boolean eliminado = dbHelper.eliminarExplotacionPorNombre(nombre, idUsuario);
                    if (eliminado) {
                        Toast.makeText(this, "Explotación eliminada", Toast.LENGTH_SHORT).show();
                        recargarSpinnerExplotaciones();
                    } else {
                        Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }



    public void irALotes() {
        int posicion = spinnerExplotaciones.getSelectedItemPosition();
        if (posicion == -1) {
            Toast.makeText(this, "Selecciona una explotación", Toast.LENGTH_SHORT).show();
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
            String cod = cursor.getString(0); // ✅ usa solo getString si cod_explotacion es tipo TEXT
            cursor.close();

            Intent intent = new Intent(this, LotesActivity.class);
            intent.putExtra("cod_explotacion", cod);
            startActivity(intent);
        } else {
            cursor.close();
            Toast.makeText(this, "Error al obtener la explotación", Toast.LENGTH_SHORT).show();
        }
    }



}

