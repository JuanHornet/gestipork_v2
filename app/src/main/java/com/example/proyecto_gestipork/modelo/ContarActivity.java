package com.example.proyecto_gestipork.modelo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.base.BaseActivity;
import com.example.proyecto_gestipork.data.DBHelper;
import com.example.proyecto_gestipork.modelo.Conteo;
import com.example.proyecto_gestipork.modelo.ContarAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class ContarActivity extends BaseActivity {

    private String codExplotacion, codLote;
    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private ContarAdapter adapter;
    private List<Conteo> listaConteos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contar);

        codExplotacion = getIntent().getStringExtra("cod_explotacion");
        codLote = getIntent().getStringExtra("cod_lote");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        configurarToolbar(toolbar, "Conteos", R.menu.menu_contar, true);

        recyclerView = findViewById(R.id.recyclerViewConteos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);

        cargarConteos();
    }

    private void cargarConteos() {
        listaConteos = dbHelper.obtenerConteosLista(codExplotacion, codLote);
        adapter = new ContarAdapter(listaConteos);
        recyclerView.setAdapter(adapter);
        actualizarResumen();
    }

    private void actualizarResumen() {
        int total = listaConteos.size();
        int saldo = 0;
        for (Conteo conteo : listaConteos) {
            saldo += conteo.getnAnimales();
        }

        findViewById(R.id.txtTotalConteos).setVisibility(total > 0 ? RecyclerView.VISIBLE : RecyclerView.GONE);
        findViewById(R.id.txtSaldoActual).setVisibility(total > 0 ? RecyclerView.VISIBLE : RecyclerView.GONE);

        ((TextView) findViewById(R.id.txtTotalConteos)).setText("Total registros: " + total);
        ((TextView) findViewById(R.id.txtSaldoActual)).setText("Saldo actual: " + saldo);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_conteo) {
            // Mostrar diálogo para añadir conteo
            ContarDialogFragment dialog = ContarDialogFragment.newInstance(codExplotacion, codLote);
            dialog.show(getSupportFragmentManager(), "ContarDialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void recargarLista() {
        listaConteos.clear();
        listaConteos.addAll(dbHelper.obtenerConteosLista(codExplotacion, codLote));
        adapter.notifyDataSetChanged();
        actualizarResumen();
    }
}
