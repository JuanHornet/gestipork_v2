package com.example.proyecto_gestipork.modelo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ParideraActivity extends AppCompatActivity {

    private EditText editNacidosVivos, editCerdasParidas, editCerdasVacias, editFechaInicio, editFechaFin;
    private Button btnEditar, btnGuardar, btnCancelar;
    private LinearLayout layoutBotones;

    private String codLote, codExplotacion;
    private String inicialVivos, inicialParidas, inicialVacias, inicialInicio, inicialFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paridera);

        codLote = getIntent().getStringExtra("cod_lote");
        codExplotacion = getIntent().getStringExtra("cod_explotacion");

        editNacidosVivos = findViewById(R.id.edit_nacidos_vivos);
        editCerdasParidas = findViewById(R.id.edit_cerdas_paridas);
        editCerdasVacias = findViewById(R.id.edit_cerdas_vacias);
        editFechaInicio = findViewById(R.id.edit_fecha_inicio);
        editFechaFin = findViewById(R.id.edit_fecha_fin);
        btnEditar = findViewById(R.id.btn_editar);
        btnGuardar = findViewById(R.id.btn_guardar_paridera);
        btnCancelar = findViewById(R.id.btn_cancelar);
        layoutBotones = findViewById(R.id.layout_botones_edicion);

        cargarDatos();
        bloquearCampos();

        editFechaInicio.setOnClickListener(v -> {
            if (editFechaInicio.isEnabled()) mostrarDatePicker(editFechaInicio);
        });

        editFechaFin.setOnClickListener(v -> {
            if (editFechaFin.isEnabled()) mostrarDatePicker(editFechaFin);
        });

        btnEditar.setOnClickListener(v -> activarEdicion());

        btnCancelar.setOnClickListener(v -> {
            restaurarDatos();
            bloquearCampos();
        });

        btnGuardar.setOnClickListener(v -> {
            guardarCambios();
            finish(); // Vuelve atrás
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Paridera " + codLote);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void cargarDatos() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT nacidosVivos, nParidas, nVacias, fechaInicioParidera, fechaFinParidera FROM parideras WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion});

        if (cursor.moveToFirst()) {
            inicialVivos = String.valueOf(cursor.getInt(0));
            inicialParidas = String.valueOf(cursor.getInt(1));
            inicialVacias = String.valueOf(cursor.getInt(2));
            inicialInicio = cursor.getString(3);
            inicialFin = cursor.getString(4);

            editNacidosVivos.setText(inicialVivos);
            editCerdasParidas.setText(inicialParidas);
            editCerdasVacias.setText(inicialVacias);
            editFechaInicio.setText(inicialInicio);
            editFechaFin.setText(inicialFin);
        }

        cursor.close();
        db.close();
    }

    private void bloquearCampos() {
        editNacidosVivos.setEnabled(false);
        editCerdasParidas.setEnabled(false);
        editCerdasVacias.setEnabled(false);
        editFechaInicio.setEnabled(false);
        editFechaFin.setEnabled(false);

        btnEditar.setVisibility(View.VISIBLE);
        layoutBotones.setVisibility(View.GONE);
    }

    private void activarEdicion() {
        editNacidosVivos.setEnabled(true);
        editCerdasParidas.setEnabled(true);
        editCerdasVacias.setEnabled(true);
        editFechaInicio.setEnabled(true);
        editFechaFin.setEnabled(true);

        btnEditar.setVisibility(View.GONE);
        layoutBotones.setVisibility(View.VISIBLE);
    }

    private void restaurarDatos() {
        editNacidosVivos.setText(inicialVivos);
        editCerdasParidas.setText(inicialParidas);
        editCerdasVacias.setText(inicialVacias);
        editFechaInicio.setText(inicialInicio);
        editFechaFin.setText(inicialFin);
    }

    private void guardarCambios() {
        String vivos = editNacidosVivos.getText().toString().trim();
        String paridas = editCerdasParidas.getText().toString().trim();
        String vacias = editCerdasVacias.getText().toString().trim();
        String inicio = editFechaInicio.getText().toString().trim();
        String fin = editFechaFin.getText().toString().trim();

        // Seguridad para campos vacíos
        if (vivos.isEmpty()) vivos = "0";
        if (paridas.isEmpty()) paridas = "0";
        if (vacias.isEmpty()) vacias = "0";
        if (inicio.isEmpty()) inicio = null;
        if (fin.isEmpty()) fin = null;

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("UPDATE parideras SET nacidosVivos = ?, nParidas = ?, nVacias = ?, fechaInicioParidera = ?, fechaFinParidera = ? WHERE cod_lote = ? AND cod_explotacion = ?",
                new Object[]{vivos, paridas, vacias, inicio, fin, codLote, codExplotacion});

        db.close();
        Toast.makeText(this, "Paridera actualizada", Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("paridera_actualizada", true);
        setResult(RESULT_OK, resultIntent);
    }


    private void mostrarDatePicker(EditText target) {
        Locale locale = new Locale("es", "ES");
        Locale.setDefault(locale);

        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar fechaSeleccionada = Calendar.getInstance();
                    fechaSeleccionada.set(year, month, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", locale);
                    String fechaFormateada = sdf.format(fechaSeleccionada.getTime());
                    target.setText(fechaFormateada);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
