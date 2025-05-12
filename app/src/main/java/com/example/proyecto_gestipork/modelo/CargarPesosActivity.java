package com.example.proyecto_gestipork.modelo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CargarPesosActivity extends AppCompatActivity {

    private Spinner spinnerLotes;
    private TextView txtAnimalesPesados, txtMediaKg, txtMediaArrobas, txtSegmentacion;
    private EditText edtPeso;
    private ImageButton btnGuardarPeso;
    private RecyclerView recyclerPesos;

    private DBHelper dbHelper;
    private List<PesoItem> listaPesos;
    private PesosLoteAdapter adapter;

    private String codExplotacion;
    private String codLote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_pesos);

        // ✅ Toolbar estandar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Carga de Pesos");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // ✅ Inicializar vistas
        spinnerLotes = findViewById(R.id.spinnerLotes);
        txtAnimalesPesados = findViewById(R.id.txtAnimalesPesados);
        txtMediaKg = findViewById(R.id.txtMediaKg);
        txtMediaArrobas = findViewById(R.id.txtMediaArrobas);
        txtSegmentacion = findViewById(R.id.txtSegmentacion);
        edtPeso = findViewById(R.id.edtPeso);
        btnGuardarPeso = findViewById(R.id.btnGuardarPeso);
        recyclerPesos = findViewById(R.id.recyclerPesos);

        dbHelper = new DBHelper(this);
        listaPesos = new ArrayList<>();

        // ✅ Recibir datos
        Intent intent = getIntent();
        codExplotacion = intent.getStringExtra("cod_explotacion");
        String loteSeleccionado = intent.getStringExtra("cod_lote");

        // ✅ Cargar lotes al spinner
        List<String> lotesActivos = dbHelper.obtenerLotesActivos(codExplotacion);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, lotesActivos);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLotes.setAdapter(spinnerAdapter);

        // ✅ Seleccionar lote actual o primero
        if (loteSeleccionado != null && lotesActivos.contains(loteSeleccionado)) {
            spinnerLotes.setSelection(lotesActivos.indexOf(loteSeleccionado));
            codLote = loteSeleccionado;
        } else if (!lotesActivos.isEmpty()) {
            codLote = lotesActivos.get(0);
        }

        // ✅ Cargar pesos del lote actual
        cargarPesosBD();

        // ✅ Listener cambio de lote
        spinnerLotes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                codLote = lotesActivos.get(position);
                cargarPesosBD();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // ✅ Adapter y RecyclerView
        adapter = new PesosLoteAdapter(this, listaPesos, position -> {
            int idEliminar = listaPesos.get(position).getId();
            dbHelper.eliminarPesoPorId(idEliminar);
            listaPesos.remove(position);
            adapter.notifyItemRemoved(position);
            recalcularResumen();
        });

        recyclerPesos.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerPesos.setAdapter(adapter);

        // ✅ Guardar nuevo peso
        btnGuardarPeso.setOnClickListener(v -> {
            String pesoStr = edtPeso.getText().toString().trim();
            if (!pesoStr.isEmpty()) {
                try {
                    int peso = Integer.parseInt(pesoStr);
                    if (peso <= 0) throw new NumberFormatException();
                    String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    dbHelper.insertarPeso(codExplotacion, codLote, peso, fechaHoy);
                    listaPesos.add(0, new PesoItem(obtenerUltimoIdPeso(), peso));
                    adapter.notifyItemInserted(0);
                    recyclerPesos.scrollToPosition(0);  // Opcional: hace scroll automático al primer item
                    edtPeso.setText("");
                    recalcularResumen();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Introduce un número válido mayor a 0", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Introduce un peso", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarPesosBD() {
        listaPesos.clear();
        Cursor cursor = dbHelper.obtenerPesosLote(codExplotacion, codLote);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int peso = cursor.getInt(1);
            listaPesos.add(new PesoItem(id, peso));
        }
        cursor.close();
        if (adapter != null) adapter.notifyDataSetChanged();
        recalcularResumen();
    }

    private int obtenerUltimoIdPeso() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT MAX(id) FROM pesar", null);
        int id = -1;
        if (cursor.moveToFirst()) id = cursor.getInt(0);
        cursor.close();
        return id;
    }

    private void recalcularResumen() {
        int total = listaPesos.size();
        int suma = 0, menos13 = 0, de13a14 = 0, de14a15 = 0, mas15 = 0;

        for (PesoItem item : listaPesos) {
            int peso = item.getPesoKg();
            suma += peso;

            if (peso < 150) menos13++;
            else if (peso >= 150 && peso <= 161) de13a14++;
            else if (peso >= 162 && peso <= 173) de14a15++;
            else if (peso >= 174) mas15++;
        }

        txtAnimalesPesados.setText("Animales pesados: " + total);
        txtMediaKg.setText("Media kg: " + (total > 0 ? (suma / total) : 0));
        txtMediaArrobas.setText("Media arrobas: " + (total > 0 ? String.format(Locale.getDefault(), "%.2f", (suma / (double) total) / 11.5) : "0"));
        txtSegmentacion.setText("-13@: " + menos13 + ", 13-14@: " + de13a14 + ", 14-15@: " + de14a15 + ", +15@: " + mas15);
    }

}
