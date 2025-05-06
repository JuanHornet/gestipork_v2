package com.example.proyecto_gestipork.modelo;

import android.content.Intent;
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
            startActivity(i);
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

}
