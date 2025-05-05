package com.example.proyecto_gestipork.modelo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.base.BaseActivity;
import com.example.proyecto_gestipork.modelo.LoteAdapter;
import com.example.proyecto_gestipork.data.DBHelper;
import com.example.proyecto_gestipork.modelo.Lotes;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.proyecto_gestipork.R;

import java.util.ArrayList;
import java.util.List;

public class LotesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private LoteAdapter adapter;
    private List<Lotes> listaLotes;
    private DBHelper dbHelper;

    private TextView txtVacio;


    private String codExplotacionSeleccionada;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lotes);

        codExplotacionSeleccionada = getIntent().getStringExtra("cod_explotacion");
        Log.d("LotesActivity", "Recibido cod_explotacion: " + codExplotacionSeleccionada);

        if (codExplotacionSeleccionada == null || codExplotacionSeleccionada.isEmpty()) {
            Toast.makeText(this, "No se recibió cod_explotacion", Toast.LENGTH_SHORT).show();
            finish(); // evita crash
            return;
        }


        // Configurar toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recycler_lotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista y BD
        listaLotes = new ArrayList<>();
        dbHelper = new DBHelper(this);


        txtVacio = findViewById(R.id.txt_vacio);
        cargarLotes();

        adapter = new LoteAdapter(this, listaLotes);
        recyclerView.setAdapter(adapter);



    }

    private void cargarLotes() {
        listaLotes.clear(); // Limpia por si ya había datos

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String consulta = "SELECT l.id, l.cod_explotacion, l.nDisponibles, l.nIniciales, " +
                "l.cod_lote, l.cod_paridera, l.cod_cubricion, l.cod_itaca, l.raza, l.estado, " +
                "i.color " +
                "FROM lotes l " +
                "LEFT JOIN itaca i ON l.cod_itaca = i.cod_itaca " +
                "WHERE l.estado = 1 AND l.cod_explotacion = ?";

        Cursor cursor = db.rawQuery(consulta, new String[]{codExplotacionSeleccionada});

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
                lote.setColor(cursor.getString(10)); // viene de tabla itaca

                listaLotes.add(0, lote);// el nuevo lote aparece el primero
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // Mostrar u ocultar mensaje vacío
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_lote) {
            mostrarDialogoNuevoLote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoNuevoLote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nuevo Lote");

        // Layout personalizado con dos EditText
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputCodLote = new EditText(this);
        inputCodLote.setHint("Código del lote");
        layout.addView(inputCodLote);

        // Spinner para raza
        final Spinner spinnerRaza = new Spinner(this);
        String[] opcionesRaza = {"Selecciona raza", "Ibérico 100%", "Cruzado 50%"};
        ArrayAdapter<String> adapterRaza = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesRaza);
        spinnerRaza.setAdapter(adapterRaza);
        layout.addView(spinnerRaza);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String codLote = inputCodLote.getText().toString().trim();
            String razaSeleccionada = spinnerRaza.getSelectedItem().toString();

            if (codLote.isEmpty()) {
                Toast.makeText(this, "El código del lote es obligatorio", Toast.LENGTH_SHORT).show();
                return;
            }

            if (razaSeleccionada.equals("Selecciona raza")) {
                Toast.makeText(this, "Debes seleccionar una raza válida", Toast.LENGTH_SHORT).show();
                return;
            }

            insertarNuevoLote(codLote, razaSeleccionada);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void insertarNuevoLote(String codLote, String raza) {
        if (codExplotacionSeleccionada == null || codExplotacionSeleccionada.isEmpty()) {
            Toast.makeText(this, "Error: explotación no seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM lotes WHERE cod_lote = ? AND cod_explotacion = ?", new String[]{codLote});

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Este lote ya existe en esta explotación", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }

        cursor.close();

        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("cod_lote", codLote);
        values.put("raza", raza);
        values.put("cod_explotacion", codExplotacionSeleccionada);
        values.put("estado", 1);
        values.put("color", "#F7F1F9");

        // ✅ Valores por defecto
        values.put("nDisponibles", 0);
        values.put("nIniciales", 0);
        values.put("cod_paridera", "");
        values.put("cod_cubricion", "");
        values.put("cod_itaca", "");

        long resultado = db.insert("lotes", null, values);

        if (resultado != -1) {
            Toast.makeText(this, "Lote guardado", Toast.LENGTH_SHORT).show();
            cargarLotes(); // refresca la lista
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Error al guardar lote", Toast.LENGTH_SHORT).show();
        }

        // Insertar también en la tabla parideras
        String codParidera = "P" + codLote + codExplotacionSeleccionada;

        ContentValues parideraValues = new ContentValues();
        parideraValues.put("cod_paridera", codParidera);
        parideraValues.put("cod_lote", codLote);
        parideraValues.put("cod_explotacion", codExplotacionSeleccionada);

        // Los demás campos quedan vacíos/null por ahora (se podrán actualizar después)
        parideraValues.put("fechaInicioParidera", "");
        parideraValues.put("fechaFinParidera", "");
        parideraValues.put("nacidosVivos", 0);
        parideraValues.put("nParidas", 0);
        parideraValues.put("nVacias", 0);

        long resultadoParidera = db.insert("parideras", null, parideraValues);

        if (resultadoParidera == -1) {
            Toast.makeText(this, "Lote guardado, pero error en parideras", Toast.LENGTH_SHORT).show();
        }
        // Insertar también en la tabla cubriciones
        String codCubricion = "C" + codLote + codExplotacionSeleccionada;

        ContentValues cubricionValues = new ContentValues();
        cubricionValues.put("cod_cubricion", codCubricion);
        cubricionValues.put("cod_lote", codLote);
        cubricionValues.put("cod_explotacion", codExplotacionSeleccionada);

        // Campos iniciales vacíos o por defecto
        cubricionValues.put("nMadres", 0);
        cubricionValues.put("nPadres", 0);
        cubricionValues.put("fechaInicioCubricion", "");
        cubricionValues.put("fechaFinCubricion", "");

        long resultadoCubricion = db.insert("cubriciones", null, cubricionValues);

        if (resultadoCubricion == -1) {
            Toast.makeText(this, "Lote guardado, pero error en cubriciones", Toast.LENGTH_SHORT).show();
        }

        // Insertar también en la tabla itaca
        String codItaca = "I" + codLote + codExplotacionSeleccionada;

        ContentValues itacaValues = new ContentValues();
        itacaValues.put("cod_itaca", codItaca);
        itacaValues.put("cod_lote", codLote);
        itacaValues.put("cod_explotacion", codExplotacionSeleccionada);
        itacaValues.put("raza", raza); // ✅ Igual que la del lote

        // Inicializa campos opcionales o en blanco
        itacaValues.put("nAnimales", 0);
        itacaValues.put("nMadres", 0);
        itacaValues.put("nPadres", 0);
        itacaValues.put("fechaPNacimiento", "");
        itacaValues.put("fechaUltNacimiento", "");
        itacaValues.put("color", "#CCCCCC");
        itacaValues.put("crotalesSolicitados", 0);

        long resultadoItaca = db.insert("itaca", null, itacaValues);

        if (resultadoItaca == -1) {
            Toast.makeText(this, "Lote guardado, pero error en itaca", Toast.LENGTH_SHORT).show();
        }


    }



}
