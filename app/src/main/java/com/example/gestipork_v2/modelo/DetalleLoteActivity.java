package com.example.gestipork_v2.modelo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.base.BaseActivity;
import com.example.gestipork_v2.base.ColorUtils;
import com.example.gestipork_v2.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DetalleLoteActivity extends BaseActivity implements MoverAlimentacionDialogFragment.OnAlimentacionActualizadaListener {

    private String idLote;
    private String idExplotacion;

    private String idCubricion, idParidera, idItaca;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_lote);

        idLote = getIntent().getStringExtra("id_lote");
        idExplotacion = getIntent().getStringExtra("id_explotacion");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle del Lote");
        }

        actualizarAnimalesDisponibles();
        actualizarDatosLote();

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager.setAdapter(new DetalleLotePagerAdapter(this, idLote, idExplotacion));
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Acciones" : "Salidas")
        ).attach();

        findViewById(R.id.text_bellota).setOnClickListener(v -> abrirDialogoMover("Bellota"));
        findViewById(R.id.text_cebo_campo).setOnClickListener(v -> abrirDialogoMover("Cebo Campo"));
        findViewById(R.id.text_cebo).setOnClickListener(v -> abrirDialogoMover("Cebo"));
        actualizarAlimentacionCardView();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(navListener);

        obtenerIdsRelacionados();
    }

    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_contar) {
            startActivity(new Intent(this, ContarActivity.class)
                    .putExtra("id_explotacion", idExplotacion)
                    .putExtra("id_lote", idLote));
            return true;
        } else if (itemId == R.id.nav_baja) {
            BajaDialogFragment dialog = BajaDialogFragment.newInstance(idLote, idExplotacion);
            dialog.show(getSupportFragmentManager(), "BajaDialogFragment");
            return true;
        } else if (itemId == R.id.nav_notas) {
            startActivity(new Intent(this, NotasActivity.class)
                    .putExtra("id_explotacion", idExplotacion)
                    .putExtra("id_lote", idLote));
            return true;
        } else if (itemId == R.id.nav_pesar) {
            startActivity(new Intent(this, CargarPesosActivity.class)
                    .putExtra("id_explotacion", idExplotacion)
                    .putExtra("id_lote", idLote));
            return true;
        }
        return false;
    };

    private void actualizarDatosLote() {
        TextView textNombreLote = findViewById(R.id.text_nombre_lote);
        TextView textRazaEdad = findViewById(R.id.text_raza_edad);

        DBHelper dbHelper = new DBHelper(this);

        // ✅ Obtener id_lote visible y raza desde la tabla lotes
        Cursor loteCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT nombre_lote, raza FROM lotes WHERE id = ? AND id_explotacion = ?",
                new String[]{idLote, idExplotacion}
        );

        if (loteCursor.moveToFirst()) {
            String nombreLote = loteCursor.getString(0); // Solo visual
            String raza = loteCursor.getString(1);
            textNombreLote.setText(nombreLote);

            // ✅ Obtener la fecha fin paridera para calcular la edad
            Cursor parideraCursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT fechaFinParidera FROM parideras WHERE id_lote = ? AND id_explotacion = ?",
                    new String[]{this.idLote, idExplotacion}
            );

            if (parideraCursor.moveToFirst()) {
                String fechaFin = parideraCursor.getString(0);
                int edad = calcularEdadEnMeses(fechaFin);
                if (edad <= 0) {
                    textRazaEdad.setText(raza + " · Edad: desc.");
                } else {
                    textRazaEdad.setText(raza + " · Edad: " + edad + " meses");
                }
            } else {
                textRazaEdad.setText(raza + " · Edad: desc.");
            }
            parideraCursor.close();
        }
        loteCursor.close();

        // ✅ PINTAR CÍRCULO DE COLOR DEL LOTE (Itaca)
        View circleView = findViewById(R.id.view_color_lote);
        String colorStr = obtenerColorLoteDesdeDB(idLote, idExplotacion);
        int color = ColorUtils.mapColorNameToHex(this, colorStr);
        circleView.getBackground().setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
    }


    private String obtenerColorLoteDesdeDB(String idLote, String idExplotacion) {
        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT color FROM itaca WHERE id_lote = ? AND id_explotacion = ?",
                new String[]{idLote, idExplotacion}
        );
        String color = null;
        if (cursor.moveToFirst()) {
            color = cursor.getString(0);
        }
        cursor.close();
        return color;
    }


    private int calcularEdadEnMeses(String fechaFin) {
        if (fechaFin == null || fechaFin.isEmpty()) return 0;
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
            java.util.Date fecha = sdf.parse(fechaFin);
            java.util.Calendar hoy = java.util.Calendar.getInstance();
            java.util.Calendar fin = java.util.Calendar.getInstance();
            fin.setTime(fecha);
            return (hoy.get(java.util.Calendar.YEAR) - fin.get(java.util.Calendar.YEAR)) * 12 +
                    (hoy.get(java.util.Calendar.MONTH) - fin.get(java.util.Calendar.MONTH));
        } catch (Exception e) {
            return 0;
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
            startActivity(new Intent(this, PesarActivity.class)
                    .putExtra("id_lote", idLote)
                    .putExtra("id_explotacion", idExplotacion));
            return true;
        } else if (id == R.id.menu_eliminar_lote) {
            mostrarDialogoEliminarLote();
            return true;
        } else if (id == R.id.menu_ver_cubricion) {
            if (idCubricion != null) {
                startActivity(new Intent(this, CubricionActivity.class)
                        .putExtra("id_cubricion", idCubricion));
            } else {
                Toast.makeText(this, "No se encontró la cubrición", Toast.LENGTH_SHORT).show();
            }
            return true;

        } else if (id == R.id.menu_ver_paridera) {
            if (idParidera != null) {
                startActivityForResult(new Intent(this, ParideraActivity.class)
                        .putExtra("id_paridera", idParidera), 1001);
            } else {
                Toast.makeText(this, "No se encontró la paridera", Toast.LENGTH_SHORT).show();
            }
            return true;

        } else if (id == R.id.menu_ver_itaca) {
            if (idItaca != null) {
                startActivityForResult(new Intent(this, ItacaActivity.class)
                        .putExtra("id_itaca", idItaca)
                        .putExtra("id_lote", idLote)
                        .putExtra("id_explotacion", idExplotacion), 1001);
            } else {
                Toast.makeText(this, "No se encontró Itaca", Toast.LENGTH_SHORT).show();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            actualizarDatosLote();
        }
    }

    private void mostrarDialogoEliminarLote() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar Lote")
                .setMessage("¿Seguro que deseas eliminar este lote y todos sus registros relacionados?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    DBHelper dbHelper = new DBHelper(this);
                    if (dbHelper.eliminarLoteConRelaciones(idLote, idExplotacion)) {
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
                    "SELECT nDisponibles FROM lotes WHERE id = ? AND id_explotacion = ?",
                    new String[]{idLote, idExplotacion}
            );
            if (cursor.moveToFirst())
                textAnimales.setText(cursor.getInt(0) + " disponibles");
            cursor.close();
        });
    }

    private void abrirDialogoMover(String tipoOrigen) {
        DBHelper dbHelper = new DBHelper(this);
        int disponibles = dbHelper.obtenerAnimalesAlimentacion(idLote, idExplotacion, tipoOrigen);
        MoverAlimentacionDialogFragment.newInstance(idLote, idExplotacion, tipoOrigen, disponibles)
                .show(getSupportFragmentManager(), "MoverAlimentacionDialog");
    }

    @Override
    public void onAlimentacionActualizada() {
        actualizarAlimentacionCardView();
    }

    public void actualizarAlimentacionCardView() {
        DBHelper dbHelper = new DBHelper(this);
        ((TextView) findViewById(R.id.text_bellota)).setText(String.valueOf(dbHelper.obtenerAnimalesAlimentacion(idLote, idExplotacion, "Bellota")));
        ((TextView) findViewById(R.id.text_cebo_campo)).setText(String.valueOf(dbHelper.obtenerAnimalesAlimentacion(idLote, idExplotacion, "Cebo Campo")));
        ((TextView) findViewById(R.id.text_cebo)).setText(String.valueOf(dbHelper.obtenerAnimalesAlimentacion(idLote, idExplotacion, "Cebo")));
    }
    // Método para que SalidaDialogFragment llame y actualice después de registrar una salida
    public void refrescarResumenLote() {
        actualizarAnimalesDisponibles();       // actualiza el número total
        actualizarAlimentacionCardView();      // actualiza el CardView de alimentación
    }
    public interface OnActualizarResumenListener {
        void onActualizarResumenLote();
    }

    private void obtenerIdsRelacionados() {
        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor;

        cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT id FROM cubriciones WHERE id = ? AND id_explotacion = ?",
                new String[]{idLote, idExplotacion});
        if (cursor.moveToFirst()) idCubricion = cursor.getString(0);
        cursor.close();

        cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT id FROM parideras WHERE id = ? AND id_explotacion = ?",
                new String[]{idLote, idExplotacion});
        if (cursor.moveToFirst()) idParidera = cursor.getString(0);
        cursor.close();

        cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT id FROM itaca WHERE id = ? AND id_explotacion = ?",
                new String[]{idLote, idExplotacion});
        if (cursor.moveToFirst()) idItaca = cursor.getString(0);
        cursor.close();

        dbHelper.close();
    }

}
