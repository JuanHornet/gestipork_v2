package com.example.gestipork_v2.modelo;

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

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.*;

public class CargarPesosActivity extends AppCompatActivity {

    private Spinner spinnerLotes;
    private TextView txtAnimalesPesados, txtMediaKg, txtMediaArrobas, txtSegmentacion;
    private EditText edtPeso;
    private ImageButton btnGuardarPeso;
    private RecyclerView recyclerPesos;

    private DBHelper dbHelper;
    private List<PesoItem> listaPesos;
    private PesosLoteAdapter adapter;

    private String idExplotacion;
    private String idLote;
    private Map<String, String> mapaNombreLoteUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_pesos);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Carga de Pesos");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        spinnerLotes = findViewById(R.id.spinnerLotes);
        txtAnimalesPesados = findViewById(R.id.txtAnimalesPesados);
        txtMediaKg = findViewById(R.id.txtMediaKg);
        txtMediaArrobas = findViewById(R.id.txtMediaArrobas);
        edtPeso = findViewById(R.id.edtPeso);
        btnGuardarPeso = findViewById(R.id.btnGuardarPeso);
        recyclerPesos = findViewById(R.id.recyclerPesos);

        dbHelper = new DBHelper(this);
        listaPesos = new ArrayList<>();

        // Recibir UUIDs
        Intent intent = getIntent();
        idExplotacion = intent.getStringExtra("id_explotacion");
        String idLoteInicial = intent.getStringExtra("id_lote");

        // Cargar lotes activos con UUID
        mapaNombreLoteUuid = new LinkedHashMap<>();
        List<String> nombresLotes = new ArrayList<>();

        Cursor cursor = dbHelper.obtenerLotesActivosConUUID(idExplotacion);
        while (cursor.moveToNext()) {
            String idLoteDB = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String nombreLote = cursor.getString(cursor.getColumnIndexOrThrow("nombre_lote"));
            mapaNombreLoteUuid.put(nombreLote, idLoteDB);
            nombresLotes.add(nombreLote);
        }
        cursor.close();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_grande, nombresLotes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLotes.setAdapter(spinnerAdapter);

        if (idLoteInicial != null && mapaNombreLoteUuid.containsValue(idLoteInicial)) {
            for (Map.Entry<String, String> entry : mapaNombreLoteUuid.entrySet()) {
                if (entry.getValue().equals(idLoteInicial)) {
                    spinnerLotes.setSelection(nombresLotes.indexOf(entry.getKey()));
                    idLote = idLoteInicial;
                    break;
                }
            }
        } else if (!nombresLotes.isEmpty()) {
            idLote = mapaNombreLoteUuid.get(nombresLotes.get(0));
        }

        cargarPesosBD();

        spinnerLotes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nombreLoteSeleccionado = nombresLotes.get(position);
                idLote = mapaNombreLoteUuid.get(nombreLoteSeleccionado);
                cargarPesosBD();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        adapter = new PesosLoteAdapter(this, listaPesos, position -> {

            int idEliminar = listaPesos.get(position).getId();
            dbHelper.eliminarPesoPorId(idEliminar);
            listaPesos.remove(position);
            adapter.notifyItemRemoved(position);
            recalcularResumen();
        });

        recyclerPesos.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerPesos.setAdapter(adapter);

        btnGuardarPeso.setOnClickListener(v -> {
            String pesoStr = edtPeso.getText().toString().trim();
            if (!pesoStr.isEmpty()) {
                try {
                    int peso = Integer.parseInt(pesoStr);
                    if (peso <= 0) throw new NumberFormatException();

                    String fecha = ((TextView) findViewById(R.id.textFechaSeleccionada)).getText().toString();

                    dbHelper.insertarPeso(idExplotacion, idLote, peso, fecha);
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
        String fechaRecibida = intent.getStringExtra("fecha");
        if (fechaRecibida != null) {
            textFechaSeleccionada.setText(fechaRecibida);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            textFechaSeleccionada.setText(sdf.format(Calendar.getInstance().getTime()));
        }

        textFechaSeleccionada.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                String fechaSeleccionada = String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month + 1, year);
                textFechaSeleccionada.setText(fechaSeleccionada);
                cargarPesosBD();
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show();
        });

        // colores
        ContextCompat.getColor(this, R.color.rojo);     // se mantienen como estaban
        ContextCompat.getColor(this, R.color.naranja);
        ContextCompat.getColor(this, R.color.verde);
        ContextCompat.getColor(this, R.color.azul);
    }

    private void cargarPesosBD() {
        listaPesos.clear();
        String fecha = ((TextView) findViewById(R.id.textFechaSeleccionada)).getText().toString();
        Cursor cursor = dbHelper.obtenerPesosPorLoteYFecha(idExplotacion, idLote, fecha);
        while (cursor.moveToNext()) {
            listaPesos.add(new PesoItem(cursor.getInt(0), cursor.getInt(1)));
        }
        cursor.close();
        if (adapter != null) adapter.notifyDataSetChanged();
        recalcularResumen();
    }

    private int obtenerUltimoIdPeso() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT MAX(id) FROM pesar", null);
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
            else if (peso <= 161) de13a14++;
            else if (peso <= 173) de14a15++;
            else mas15++;
        }

        txtAnimalesPesados.setText("Animales: " + total);
        txtMediaKg.setText("Media kg: " + (total > 0 ? (suma / total) : 0));
        txtMediaArrobas.setText("Media @: " + (total > 0 ? String.format(Locale.getDefault(), "%.2f", (suma / (double) total) / 11.5) : "0"));

        ((TextView) findViewById(R.id.txtSegmentacion13)).setText("-13@: " + menos13);
        ((TextView) findViewById(R.id.txtSegmentacion14)).setText("13-14@: " + de13a14);
        ((TextView) findViewById(R.id.txtSegmentacion15)).setText("14-15@: " + de14a15);
        ((TextView) findViewById(R.id.txtSegmentacion16)).setText("+15@: " + mas15);
    }
}
