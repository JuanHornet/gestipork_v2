package com.example.proyecto_gestipork.modelo;

import android.app.DatePickerDialog;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        edtPeso = findViewById(R.id.edtPeso);
        btnGuardarPeso = findViewById(R.id.btnGuardarPeso);
        recyclerPesos = findViewById(R.id.recyclerPesos);

        dbHelper = new DBHelper(this);
        listaPesos = new ArrayList<>();

        // ✅ Recibir datos
        Intent intent = getIntent();
        codExplotacion = intent.getStringExtra("cod_explotacion");
        String loteSeleccionado = intent.getStringExtra("cod_lote");

        // ✅ Cargar lotes al spinner con tamaño personalizado
        List<String> lotesActivos = dbHelper.obtenerLotesActivos(codExplotacion);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_grande,
                lotesActivos
        );
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

                    // ✅ NUEVO: obtener la fecha seleccionada
                    TextView textFechaSeleccionada = findViewById(R.id.textFechaSeleccionada);
                    String fechaSeleccionada = textFechaSeleccionada.getText().toString();

                    dbHelper.insertarPeso(codExplotacion, codLote, peso, fechaSeleccionada);
                    listaPesos.add(0, new PesoItem(obtenerUltimoIdPeso(), peso));
                    adapter.notifyItemInserted(0);
                    recyclerPesos.scrollToPosition(0);
                    edtPeso.setText("");
                    recalcularResumen();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Introduce un número válido mayor a 0", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Introduce un peso", Toast.LENGTH_SHORT).show();
            }
        });


        TextView textFechaSeleccionada = findViewById(R.id.textFechaSeleccionada);

        // ✅ Si viene fecha desde el intent,le da prioridad; si no, fecha actual
        String fechaRecibida = getIntent().getStringExtra("fecha");
        if (fechaRecibida != null && !fechaRecibida.isEmpty()) {
            textFechaSeleccionada.setText(fechaRecibida);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Calendar calendario = Calendar.getInstance();
            textFechaSeleccionada.setText(sdf.format(calendario.getTime()));
        }

        // ✅ Abrir DatePicker al pulsar sobre el TextView
        textFechaSeleccionada.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();

            int anio = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    CargarPesosActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Cuando selecciona fecha, actualizar el TextView
                        String fechaSeleccionada = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year);
                        textFechaSeleccionada.setText(fechaSeleccionada);

                        cargarPesosBD();
                    },
                    anio, mes, dia
            );
            datePicker.show();
        });

        //colores para la segmentacion
        TextView txt13 = findViewById(R.id.txtSegmentacion13);
        TextView txt14 = findViewById(R.id.txtSegmentacion14);
        TextView txt15 = findViewById(R.id.txtSegmentacion15);
        TextView txt16 = findViewById(R.id.txtSegmentacion16);

        txt13.setTextColor(ContextCompat.getColor(this, R.color.rojo));
        txt14.setTextColor(ContextCompat.getColor(this, R.color.naranja));
        txt15.setTextColor(ContextCompat.getColor(this, R.color.verde));
        txt16.setTextColor(ContextCompat.getColor(this, R.color.azul));


    }

    private void cargarPesosBD() {
        listaPesos.clear();

        // ✅ Leer la fecha seleccionada
        TextView textFechaSeleccionada = findViewById(R.id.textFechaSeleccionada);
        String fechaSeleccionada = textFechaSeleccionada.getText().toString();

        // ✅ Leer pesos filtrando por explotación, lote y fecha
        Cursor cursor = dbHelper.obtenerPesosPorLoteYFecha(codExplotacion, codLote, fechaSeleccionada);
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

        // código para actualizar los 4 TextView de segmentación
        TextView txt13 = findViewById(R.id.txtSegmentacion13);
        TextView txt14 = findViewById(R.id.txtSegmentacion14);
        TextView txt15 = findViewById(R.id.txtSegmentacion15);
        TextView txt16 = findViewById(R.id.txtSegmentacion16);

        txtAnimalesPesados.setText("Animales: " + total);
        txtMediaKg.setText("Media kg: " + (total > 0 ? (suma / total) : 0));
        txtMediaArrobas.setText("Media @: " + (total > 0 ? String.format(Locale.getDefault(), "%.2f", (suma / (double) total) / 11.5) : "0"));

        // Se actualizan los 4 valores individualmente
        txt13.setText("-13@: " + menos13);
        txt14.setText("13-14@: " + de13a14);
        txt15.setText("14-15@: " + de14a15);
        txt16.setText("+15@: " + mas15);

    }

}
