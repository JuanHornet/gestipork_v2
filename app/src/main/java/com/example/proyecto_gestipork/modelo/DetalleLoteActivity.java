package com.example.proyecto_gestipork.modelo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.base.BaseActivity;
import com.example.proyecto_gestipork.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DetalleLoteActivity extends BaseActivity implements MoverAlimentacionDialogFragment.OnAlimentacionActualizadaListener {

    private String codLote;
    private String codExplotacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_lote);

        codLote = getIntent().getStringExtra("cod_lote");
        codExplotacion = getIntent().getStringExtra("cod_explotacion");

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle del Lote");
        }

        // Alimentación
        actualizarAnimalesDisponibles();
        actualizarDatosLote();

        // ViewPager + Tabs
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager.setAdapter(new DetalleLotePagerAdapter(this, codLote, codExplotacion));
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Acciones" : "Salidas")
        ).attach();

        // CardView alimentación
        findViewById(R.id.text_bellota).setOnClickListener(v -> abrirDialogoMover("Bellota"));
        findViewById(R.id.text_cebo_campo).setOnClickListener(v -> abrirDialogoMover("Cebo Campo"));
        findViewById(R.id.text_cebo).setOnClickListener(v -> abrirDialogoMover("Cebo"));
        actualizarAlimentacionCardView();

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(navListener);
    }

    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_contar) {
            Intent intent = new Intent(DetalleLoteActivity.this, ContarActivity.class);
            intent.putExtra("cod_explotacion", codExplotacion);
            intent.putExtra("cod_lote", codLote);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.nav_baja) {
            BajaDialogFragment dialog = BajaDialogFragment.newInstance(codLote, codExplotacion);
            dialog.show(getSupportFragmentManager(), "BajaDialogFragment");
            return true;
        } else if (itemId == R.id.nav_notas) {
            Intent intent = new Intent(DetalleLoteActivity.this, NotasActivity.class);
            intent.putExtra("cod_explotacion", codExplotacion);
            intent.putExtra("cod_lote", codLote);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.nav_pesar) {
            Intent intent = new Intent(DetalleLoteActivity.this, com.example.proyecto_gestipork.modelo.CargarPesosActivity.class);
            intent.putExtra("cod_explotacion", codExplotacion);
            intent.putExtra("cod_lote", codLote);
            startActivity(intent);
            return true;
        } else {
            return false;
        }
    };



    private void actualizarDatosLote() {
        TextView textCodLote = findViewById(R.id.text_cod_lote);
        TextView textRazaEdad = findViewById(R.id.text_raza_edad);

        DBHelper dbHelper = new DBHelper(this);
        Cursor loteCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT cod_lote, raza FROM lotes WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion}
        );

        if (loteCursor.moveToFirst()) {
            String raza = loteCursor.getString(1);
            textCodLote.setText(loteCursor.getString(0));

            Cursor parideraCursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT fechaFinParidera FROM parideras WHERE cod_lote = ? AND cod_explotacion = ?",
                    new String[]{codLote, codExplotacion}
            );

            if (parideraCursor.moveToFirst()) {
                String fechaFin = parideraCursor.getString(0);
                textRazaEdad.setText(raza + " · Edad: " + calcularEdadEnMeses(fechaFin) + " meses");
            } else {
                textRazaEdad.setText(raza + " · Edad: desc.");
            }

            parideraCursor.close();
        }

        loteCursor.close();
    }

    private int calcularEdadEnMeses(String fechaFin) {
        if (fechaFin == null || fechaFin.isEmpty()) return -1;
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
            java.util.Date fecha = sdf.parse(fechaFin);
            java.util.Calendar hoy = java.util.Calendar.getInstance();
            java.util.Calendar fin = java.util.Calendar.getInstance();
            fin.setTime(fecha);
            return (hoy.get(java.util.Calendar.YEAR) - fin.get(java.util.Calendar.YEAR)) * 12 +
                    (hoy.get(java.util.Calendar.MONTH) - fin.get(java.util.Calendar.MONTH));
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_lote, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_editar_lote) {
            Toast.makeText(this, "Editar Lote", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_ver_historial_pesajes) {
            // 👉 Esto abrirá tu pantalla de Historial (PesarActivity)
            startActivity(new Intent(this, PesarActivity.class)
                    .putExtra("cod_lote", codLote)
                    .putExtra("cod_explotacion", codExplotacion));
            return true;
        } else if (id == R.id.menu_eliminar_lote) {
            mostrarDialogoEliminarLote();
            return true;
        } else if (id == R.id.menu_ver_cubricion) {
            startActivity(new Intent(this, CubricionActivity.class)
                    .putExtra("cod_lote", codLote)
                    .putExtra("cod_explotacion", codExplotacion));
            return true;
        } else if (id == R.id.menu_ver_paridera) {
            startActivityForResult(new Intent(this, ParideraActivity.class)
                    .putExtra("cod_lote", codLote)
                    .putExtra("cod_explotacion", codExplotacion), 1001);
            return true;
        } else if (id == R.id.menu_ver_itaca) {
            startActivity(new Intent(this, ItacaActivity.class)
                    .putExtra("cod_lote", codLote)
                    .putExtra("cod_explotacion", codExplotacion));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarDialogoEliminarLote() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar Lote")
                .setMessage("¿Seguro que deseas eliminar este lote y todos sus registros relacionados?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    DBHelper dbHelper = new DBHelper(this);
                    if (dbHelper.eliminarLoteConRelaciones(codLote, codExplotacion)) {
                        Toast.makeText(this, "Lote eliminado", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Error al eliminar el lote", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void actualizarAnimalesDisponibles() {
        runOnUiThread(() -> {
            TextView textAnimales = findViewById(R.id.text_n_animales);
            DBHelper dbHelper = new DBHelper(this);
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT nDisponibles FROM lotes WHERE cod_lote = ? AND cod_explotacion = ?",
                    new String[]{codLote, codExplotacion}
            );
            if (cursor.moveToFirst()) textAnimales.setText(cursor.getInt(0) + " disponibles");
            cursor.close();
        });
    }

    private void abrirDialogoMover(String tipoOrigen) {
        DBHelper dbHelper = new DBHelper(this);
        int disponibles = dbHelper.obtenerAnimalesAlimentacion(codLote, codExplotacion, tipoOrigen);
        MoverAlimentacionDialogFragment.newInstance(codLote, codExplotacion, tipoOrigen, disponibles)
                .show(getSupportFragmentManager(), "MoverAlimentacionDialog");
    }

    @Override
    public void onAlimentacionActualizada() {
        actualizarAlimentacionCardView();
    }

    public void actualizarAlimentacionCardView() {
        DBHelper dbHelper = new DBHelper(this);
        ((TextView) findViewById(R.id.text_bellota)).setText(String.valueOf(dbHelper.obtenerAnimalesAlimentacion(codLote, codExplotacion, "Bellota")));
        ((TextView) findViewById(R.id.text_cebo_campo)).setText(String.valueOf(dbHelper.obtenerAnimalesAlimentacion(codLote, codExplotacion, "Cebo Campo")));
        ((TextView) findViewById(R.id.text_cebo)).setText(String.valueOf(dbHelper.obtenerAnimalesAlimentacion(codLote, codExplotacion, "Cebo")));
    }
}
