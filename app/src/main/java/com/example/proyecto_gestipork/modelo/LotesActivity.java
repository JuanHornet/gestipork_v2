package com.example.proyecto_gestipork.modelo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.modelo.LoteAdapter;
import com.example.proyecto_gestipork.data.DBHelper;
import com.example.proyecto_gestipork.modelo.Lotes;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.proyecto_gestipork.R;

import java.util.ArrayList;
import java.util.List;

public class LotesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LoteAdapter adapter;
    private List<Lotes> listaLotes;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lotes);

        // Configurar toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_lotes);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recycler_lotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar lista y BD
        listaLotes = new ArrayList<>();
        dbHelper = new DBHelper(this);

        cargarLotes();

        adapter = new LoteAdapter(listaLotes);
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
                "WHERE l.estado = 1";

        Cursor cursor = db.rawQuery(consulta, null);

        if (cursor.moveToFirst()) {
            do {
                Lotes lote = new Lotes();
                lote.setId(cursor.getInt(0));
                lote.setCod_explotacion(cursor.getInt(1));
                lote.setnDisponibles(cursor.getInt(2));
                lote.setnIniciales(cursor.getInt(3));
                lote.setCod_lote(cursor.getString(4));
                lote.setCod_paridera(cursor.getString(5));
                lote.setCod_cubricion(cursor.getString(6));
                lote.setCod_itaca(cursor.getString(7));
                lote.setRaza(cursor.getString(8));
                lote.setEstado(cursor.getInt(9) == 1);
                lote.setColor(cursor.getString(10)); // viene de tabla itaca

                listaLotes.add(lote);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lotes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_lote) {
            // Aquí abrirías el diálogo o actividad para añadir un nuevo lote
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
