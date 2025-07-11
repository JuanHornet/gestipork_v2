package com.example.gestipork_v2.modelo.tabs;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.SalidasExplotacion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalidasFragment extends Fragment {

    private RecyclerView recyclerView;
    private SalidaAdapter adapter;
    private List<SalidasExplotacion> listaSalidas;
    private FloatingActionButton fabAdd;

    private String codLote, codExplotacion;

    public SalidasFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_salidas, container, false);

        if (getArguments() != null) {
            codLote = getArguments().getString("id_lote");
            codExplotacion = getArguments().getString("cod_explotacion");
        }

        recyclerView = vista.findViewById(R.id.recycler_salidas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fabAdd = vista.findViewById(R.id.fab_add_salida);

        fabAdd.setOnClickListener(v -> {
            SalidaDialogFragment dialog = new SalidaDialogFragment(
                    codLote,
                    codExplotacion,
                    null,
                    SalidasFragment.this::cargarSalidas
            );
            dialog.show(getChildFragmentManager(), "NuevaSalida");
        });

        cargarSalidas();

        return vista;
    }

    private void cargarSalidas() {
        listaSalidas = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(getContext());
        Cursor cursor = dbHelper.obtenerSalidas(codLote, codExplotacion);

        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipoSalida"));
            String alimentacion = cursor.getString(cursor.getColumnIndexOrThrow("tipoAlimentacion"));
            int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales"));
            String fechaStr = cursor.getString(cursor.getColumnIndexOrThrow("fechaSalida"));
            String obs = cursor.getString(cursor.getColumnIndexOrThrow("observacion"));

            Date fecha = null;
            try {
                fecha = formato.parse(fechaStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SalidasExplotacion salida = new SalidasExplotacion(id, cantidad, tipo, alimentacion, codLote, codExplotacion, obs, fecha);
            listaSalidas.add(salida);
        }
        cursor.close();

        adapter = new SalidaAdapter(listaSalidas, new SalidaAdapter.OnSalidaClickListener() {
            @Override
            public void onEditarSalida(SalidasExplotacion salida) {
                SalidaDialogFragment dialog = new SalidaDialogFragment(
                        codLote,
                        codExplotacion,
                        salida.getId(),
                        SalidasFragment.this::cargarSalidas
                );
                dialog.show(getChildFragmentManager(), "EditarSalida");
            }

            @Override
            public void onEliminarSalida(SalidasExplotacion salida) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Eliminar salida")
                        .setMessage("¿Estás seguro de que quieres eliminar esta salida?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            DBHelper dbHelper = new DBHelper(getContext());
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            int filas = db.delete("salidas", "id = ?", new String[]{String.valueOf(salida.getId())});
                            if (filas > 0) {
                                cargarSalidas();
                                // 👉 Notificar al Activity si implementa OnActualizarResumenListener
                                if (getActivity() instanceof OnActualizarResumenListener) {
                                    ((OnActualizarResumenListener) getActivity()).onActualizarResumenLote();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);
    }
}
