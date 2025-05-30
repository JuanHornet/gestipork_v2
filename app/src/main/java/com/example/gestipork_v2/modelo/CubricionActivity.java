package com.example.gestipork_v2.modelo;


import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CubricionActivity extends AppCompatActivity {

    private EditText editNMadres, editNPadres, editFechaInicio, editFechaFin;
    private Button btnEditar, btnGuardar, btnCancelar;

    private String codLote, codExplotacion;
    private String inicialMadres, inicialPadres, inicialInicio, inicialFin;

    private LinearLayout layoutBotones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cubricion);



        codLote = getIntent().getStringExtra("cod_lote");
        codExplotacion = getIntent().getStringExtra("cod_explotacion");

        editNMadres = findViewById(R.id.edit_n_madres);
        editNPadres = findViewById(R.id.edit_n_padres);
        editFechaInicio = findViewById(R.id.edit_fecha_inicio);
        editFechaFin = findViewById(R.id.edit_fecha_fin);
        btnEditar = findViewById(R.id.btn_editar);
        btnGuardar = findViewById(R.id.btn_guardar_cubricion);
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
            finish(); // Vuelve a DetalleLoteActivity
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);

        // Esta línea hace que se muestre la flecha
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Cubrición " + codLote);

        }

        // Esta línea es la que gestiona el clic en la flecha
        toolbar.setNavigationOnClickListener(v -> finish());

    }

    private void cargarDatos() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT nMadres, nPadres, fechaInicioCubricion, fechaFinCubricion FROM cubriciones WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion});

        if (cursor.moveToFirst()) {
            inicialMadres = String.valueOf(cursor.getInt(0));
            inicialPadres = String.valueOf(cursor.getInt(1));
            inicialInicio = cursor.getString(2);
            inicialFin = cursor.getString(3);

            editNMadres.setText(inicialMadres);
            editNPadres.setText(inicialPadres);
            editFechaInicio.setText(inicialInicio);
            editFechaFin.setText(inicialFin);
        }

        cursor.close();
        db.close();
    }

    private void bloquearCampos() {
        editNMadres.setEnabled(false);
        editNPadres.setEnabled(false);
        editFechaInicio.setEnabled(false);
        editFechaFin.setEnabled(false);

        btnEditar.setVisibility(View.VISIBLE);
        layoutBotones.setVisibility(View.GONE);

    }

    private void activarEdicion() {
        editNMadres.setEnabled(true);
        editNPadres.setEnabled(true);
        editFechaInicio.setEnabled(true);
        editFechaFin.setEnabled(true);

        btnEditar.setVisibility(View.GONE);
        layoutBotones.setVisibility(View.VISIBLE);

    }

    private void restaurarDatos() {
        editNMadres.setText(inicialMadres);
        editNPadres.setText(inicialPadres);
        editFechaInicio.setText(inicialInicio);
        editFechaFin.setText(inicialFin);
    }

    private void guardarCambios() {
        String madres = editNMadres.getText().toString().trim();
        String padres = editNPadres.getText().toString().trim();
        String inicio = editFechaInicio.getText().toString().trim();
        String fin = editFechaFin.getText().toString().trim();

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("UPDATE cubriciones SET nMadres = ?, nPadres = ?, fechaInicioCubricion = ?, fechaFinCubricion = ? WHERE cod_lote = ? AND cod_explotacion = ?",
                new Object[]{madres, padres, inicio, fin, codLote, codExplotacion});

        db.close();
        Toast.makeText(this, "Cubrición actualizada", Toast.LENGTH_SHORT).show();
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
