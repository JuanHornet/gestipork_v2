package com.example.proyecto_gestipork.modelo;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ItacaActivity extends AppCompatActivity {

    private EditText editNAnimales, editNMadres, editNPadres, editFechaPrimero, editFechaUltimo, editCrotales, editDCER;;
    private Button btnEditar, btnGuardar, btnCancelar;
    private LinearLayout layoutBotones;
    private AutoCompleteTextView spinnerRaza, spinnerColor;

    private String codLote, codExplotacion;
    private String inicialAnimales, inicialMadres, inicialPadres, inicialPrimero, inicialUltimo, inicialRaza, inicialColor, inicialCrotales, inicialDCER;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itaca);

        codLote = getIntent().getStringExtra("cod_lote");
        codExplotacion = getIntent().getStringExtra("cod_explotacion");

        editNAnimales = findViewById(R.id.edit_n_animales);
        editNMadres = findViewById(R.id.edit_n_madres);
        editNPadres = findViewById(R.id.edit_n_padres);
        editFechaPrimero = findViewById(R.id.edit_fecha_primero);
        editFechaUltimo = findViewById(R.id.edit_fecha_ultimo);
        spinnerRaza = findViewById(R.id.spinner_raza);
        spinnerColor = findViewById(R.id.spinner_color);
        editCrotales = findViewById(R.id.edit_crotales);
        editDCER = findViewById(R.id.edit_dcer);

        btnEditar = findViewById(R.id.btn_editar);
        btnGuardar = findViewById(R.id.btn_guardar_itaca);
        btnCancelar = findViewById(R.id.btn_cancelar);
        layoutBotones = findViewById(R.id.layout_botones_edicion);

        // Opciones para raza
        String[] opcionesRaza = {"Ibérico 100%", "Cruzado 50%"};
        ArrayAdapter<String> adapterRaza = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, opcionesRaza);
        spinnerRaza.setAdapter(adapterRaza);

        // Opciones para color
        String[] opcionesColor = {"Seleccione color de crotal", "azul", "naranja", "rojo", "verde", "rosa"};
        ArrayAdapter<String> adapterColor = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, opcionesColor);
        spinnerColor.setAdapter(adapterColor);

        // Mostrar desplegable al hacer clic
        spinnerRaza.setOnClickListener(v -> spinnerRaza.showDropDown());
        spinnerColor.setOnClickListener(v -> spinnerColor.showDropDown());
        spinnerRaza.setInputType(0);
        spinnerRaza.setKeyListener(null);
        spinnerColor.setInputType(0);
        spinnerColor.setKeyListener(null);

        cargarDatos();
        bloquearCampos();

        editFechaPrimero.setOnClickListener(v -> {
            if (editFechaPrimero.isEnabled()) mostrarDatePicker(editFechaPrimero);
        });

        editFechaUltimo.setOnClickListener(v -> {
            if (editFechaUltimo.isEnabled()) mostrarDatePicker(editFechaUltimo);
        });

        btnEditar.setOnClickListener(v -> activarEdicion());

        btnCancelar.setOnClickListener(v -> {
            restaurarDatos();
            bloquearCampos();
        });

        btnGuardar.setOnClickListener(v -> guardarCambios());

        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Itaca " + codLote);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void cargarDatos() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT nAnimales, nMadres, nPadres, fechaPNacimiento, fechaUltNacimiento, raza, color, crotalesSolicitados, DCER FROM itaca WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion}
        );


        if (cursor.moveToFirst()) {
            inicialAnimales = String.valueOf(cursor.getInt(0));
            inicialMadres = String.valueOf(cursor.getInt(1));
            inicialPadres = String.valueOf(cursor.getInt(2));
            inicialPrimero = cursor.getString(3);
            inicialUltimo = cursor.getString(4);
            inicialRaza = cursor.getString(5);
            String colorBD = cursor.getString(6);
            inicialColor = (colorBD == null || colorBD.trim().isEmpty() || colorBD.equals("#CCCCCC"))
                    ? "Seleccione color de crotal"
                    : colorBD;
            inicialCrotales = String.valueOf(cursor.getInt(7));
            inicialDCER = cursor.getString(8);

            editDCER.setText(inicialDCER);
            editNAnimales.setText(inicialAnimales);
            editNMadres.setText(inicialMadres);
            editNPadres.setText(inicialPadres);
            editFechaPrimero.setText(inicialPrimero);
            editFechaUltimo.setText(inicialUltimo);
            spinnerRaza.setText(inicialRaza, false);
            spinnerColor.setText(inicialColor, false);
            editCrotales.setText(inicialCrotales);
        }

        cursor.close();
        db.close();
    }

    private void bloquearCampos() {
        editNAnimales.setEnabled(false);
        editNMadres.setEnabled(false);
        editNPadres.setEnabled(false);
        editFechaPrimero.setEnabled(false);
        editFechaUltimo.setEnabled(false);
        spinnerRaza.setEnabled(false);
        spinnerColor.setEnabled(false);
        editCrotales.setEnabled(false);
        editDCER.setEnabled(false);
        btnEditar.setVisibility(View.VISIBLE);
        layoutBotones.setVisibility(View.GONE);
    }

    private void activarEdicion() {
        editNAnimales.setEnabled(true);
        editNMadres.setEnabled(true);
        editNPadres.setEnabled(true);
        editFechaPrimero.setEnabled(true);
        editFechaUltimo.setEnabled(true);
        spinnerRaza.setEnabled(true);
        spinnerColor.setEnabled(true);
        editCrotales.setEnabled(true);
        editDCER.setEnabled(true);
        btnEditar.setVisibility(View.GONE);
        layoutBotones.setVisibility(View.VISIBLE);
    }

    private void restaurarDatos() {
        editNAnimales.setText(inicialAnimales);
        editNMadres.setText(inicialMadres);
        editNPadres.setText(inicialPadres);
        editFechaPrimero.setText(inicialPrimero);
        editFechaUltimo.setText(inicialUltimo);
        spinnerRaza.setText(inicialRaza, false);
        spinnerColor.setText(inicialColor, false);
        editCrotales.setText(inicialCrotales);
        editDCER.setText(inicialDCER);
    }

    private void guardarCambios() {
        String animales = editNAnimales.getText().toString().trim();
        String madres = editNMadres.getText().toString().trim();
        String padres = editNPadres.getText().toString().trim();
        String primero = editFechaPrimero.getText().toString().trim();
        String ultimo = editFechaUltimo.getText().toString().trim();
        String raza = spinnerRaza.getText().toString().trim();
        String color = spinnerColor.getText().toString().trim();
        String crotales = editCrotales.getText().toString().trim();
        String dcer = editDCER.getText().toString().trim();

        if (color.equals("Seleccione color de crotal")) {
            Snackbar.make(btnGuardar,
                    "Debes seleccionar un color de crotal válido",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "UPDATE itaca SET nAnimales = ?, nMadres = ?, nPadres = ?, fechaPNacimiento = ?, fechaUltNacimiento = ?, raza = ?, color = ?, crotalesSolicitados = ?, DCER = ? WHERE cod_lote = ? AND cod_explotacion = ?",
                new Object[]{animales, madres, padres, primero, ultimo, raza, color, crotales, dcer, codLote, codExplotacion}
        );

        db.execSQL("UPDATE lotes SET color = ? WHERE cod_lote = ? AND cod_explotacion = ?",
                new Object[]{color, codLote, codExplotacion});


        db.close();
        Toast.makeText(this, "Datos de Itaca actualizados", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);//actualiza el color del crotal

        finish();
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
                    target.setText(sdf.format(fechaSeleccionada.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
