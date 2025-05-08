package com.example.proyecto_gestipork.modelo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.base.BaseActivity;
import com.example.proyecto_gestipork.data.DBHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DetalleLoteActivity extends BaseActivity {

    private String codLote;
    private String codExplotacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_lote);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_estandar);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Mostrar flecha hacia atrás
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle del Lote");
        }

        codLote = getIntent().getStringExtra("cod_lote");
        codExplotacion = getIntent().getStringExtra("cod_explotacion");

        TextView textAnimales = findViewById(R.id.text_n_animales);
        DBHelper dbHelper = new DBHelper(this);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT nDisponibles FROM lotes WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion}
        );

        if (cursor.moveToFirst()) {
            int disponibles = cursor.getInt(0);
            textAnimales.setText(disponibles + " disponibles");
        }
        cursor.close();
        TextView textCodLote = findViewById(R.id.text_cod_lote);
        TextView textRazaEdad = findViewById(R.id.text_raza_edad);

        Cursor loteCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT cod_lote, raza FROM lotes WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion}
        );

        if (loteCursor.moveToFirst()) {
            String cod = loteCursor.getString(0);
            String raza = loteCursor.getString(1);
            textCodLote.setText(cod);

            Cursor parideraCursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT fechaFinParidera FROM parideras WHERE cod_lote = ? AND cod_explotacion = ?",
                    new String[]{codLote, codExplotacion}
            );

            if (parideraCursor.moveToFirst()) {
                String fechaFin = parideraCursor.getString(0);
                if (fechaFin != null && !fechaFin.isEmpty()) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
                        java.util.Date fecha = sdf.parse(fechaFin);

                        java.util.Calendar hoy = java.util.Calendar.getInstance();
                        java.util.Calendar fin = java.util.Calendar.getInstance();
                        fin.setTime(fecha);

                        int edadMeses;
                        if (hoy.get(java.util.Calendar.YEAR) == fin.get(java.util.Calendar.YEAR) &&
                                hoy.get(java.util.Calendar.MONTH) == fin.get(java.util.Calendar.MONTH)) {
                            edadMeses = 0;
                        } else {
                            edadMeses = (hoy.get(java.util.Calendar.YEAR) - fin.get(java.util.Calendar.YEAR)) * 12 +
                                    (hoy.get(java.util.Calendar.MONTH) - fin.get(java.util.Calendar.MONTH));
                        }


                        textRazaEdad.setText(raza + " · Edad: " + edadMeses + " meses");

                    } catch (Exception e) {
                        textRazaEdad.setText(raza + " · Edad: desc.");
                    }
                } else {
                    textRazaEdad.setText(raza + " · Edad: desc.");
                }
            }
            parideraCursor.close();
        }
        loteCursor.close();


        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        viewPager.setAdapter(new DetalleLotePagerAdapter(this, codLote, codExplotacion));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tab.setText(position == 0 ? "Acciones" : "Salidas");
                }).attach();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detalle_lote, menu);
        return true;
    }


    // Acciones del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Flecha de retroceso
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        // Opciones del menú
        if (id == R.id.menu_editar_lote) {
            Toast.makeText(this, "Editar Lote", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_eliminar_lote) {
            mostrarDialogoEliminarLote();
            return true;
        } else if (id == R.id.menu_ver_cubricion) {
            Intent i = new Intent(this, CubricionActivity.class);
            i.putExtra("cod_lote", codLote);
            i.putExtra("cod_explotacion", codExplotacion);
            startActivity(i);
            return true;
        } else if (id == R.id.menu_ver_paridera) {
            Intent i = new Intent(this, ParideraActivity.class);
            i.putExtra("cod_lote", codLote);
            i.putExtra("cod_explotacion", codExplotacion);
            startActivityForResult(i, 1001); // ✅ para que se detecte si vuelve con cambios
            return true;

        } else if (id == R.id.menu_ver_itaca) {
            Intent i = new Intent(this, ItacaActivity.class);
            i.putExtra("cod_lote", codLote);
            i.putExtra("cod_explotacion", codExplotacion);
            startActivity(i);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void mostrarDialogoEliminarLote() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Lote")
                .setMessage("¿Seguro que deseas eliminar este lote y todos sus registros relacionados?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    DBHelper dbHelper = new DBHelper(this);
                    boolean eliminado = dbHelper.eliminarLoteConRelaciones(codLote, codExplotacion);

                    if (eliminado) {
                        Toast.makeText(this, "Lote eliminado", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish(); // volver a LotesActivity

                        } else {
                        Toast.makeText(this, "Error al eliminar el lote", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getBooleanExtra("paridera_actualizada", false)) {
                    actualizarEdadEnCardView();
                }
                if (data.getBooleanExtra("accion_destete_actualizada", false)) {
                    actualizarAnimalesDisponibles();  // 👈 nuevo método
                }
            }
        }
    }

    private void actualizarEdadEnCardView() {
        TextView textRazaEdad = findViewById(R.id.text_raza_edad);

        DBHelper dbHelper = new DBHelper(this);
        Cursor loteCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT raza FROM lotes WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion}
        );

        if (loteCursor.moveToFirst()) {
            String raza = loteCursor.getString(0);

            Cursor parideraCursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT fechaFinParidera FROM parideras WHERE cod_lote = ? AND cod_explotacion = ?",
                    new String[]{codLote, codExplotacion}
            );

            if (parideraCursor.moveToFirst()) {
                String fechaFin = parideraCursor.getString(0);
                if (fechaFin != null && !fechaFin.isEmpty()) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
                        java.util.Date fecha = sdf.parse(fechaFin);

                        java.util.Calendar hoy = java.util.Calendar.getInstance();
                        java.util.Calendar fin = java.util.Calendar.getInstance();
                        fin.setTime(fecha);

                        int edadMeses = (hoy.get(java.util.Calendar.YEAR) - fin.get(java.util.Calendar.YEAR)) * 12 +
                                (hoy.get(java.util.Calendar.MONTH) - fin.get(java.util.Calendar.MONTH));

                        textRazaEdad.setText(raza + " · Edad: " + edadMeses + " meses");
                    } catch (Exception e) {
                        textRazaEdad.setText(raza + " · Edad: desc.");
                    }
                } else {
                    textRazaEdad.setText(raza + " · Edad: desc.");
                }
            }

            parideraCursor.close();
        }

        loteCursor.close();
    }
    public void actualizarAnimalesDisponibles() {
        runOnUiThread(() -> {
            TextView textAnimales = findViewById(R.id.text_n_animales);
            DBHelper dbHelper = new DBHelper(this);

            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT nDisponibles FROM lotes WHERE cod_lote = ? AND cod_explotacion = ?",
                    new String[]{codLote, codExplotacion}
            );

            if (cursor.moveToFirst()) {
                int disponibles = cursor.getInt(0);
                textAnimales.setText(disponibles + " disponibles");
            }

            cursor.close();
        });
    }



}
