package com.example.proyecto_gestipork.modelo.tabs;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AccionesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private AccionAdapter adapter;
    private List<Accion> listaAcciones;

    private String codLote, codExplotacion;

    public AccionesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_acciones, container, false);

        // Leer valores de cod_lote y cod_explotacion del bundle primero
        if (getArguments() != null) {
            codLote = getArguments().getString("cod_lote");
            codExplotacion = getArguments().getString("cod_explotacion");
        }

        recyclerView = vista.findViewById(R.id.recycler_acciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAdd = vista.findViewById(R.id.fab_add_accion);
        fabAdd.setOnClickListener(v -> {
            AccionDialogFragment dialog = new AccionDialogFragment(
                    codLote, codExplotacion, null, this::cargarAcciones
            );
            dialog.show(getChildFragmentManager(), "NuevaAccion");
        });

        cargarAcciones();

        return vista;
    }


    private void cargarAcciones() {
        DBHelper dbHelper = new DBHelper(getContext());
        listaAcciones = new ArrayList<>();

        Cursor cursor = dbHelper.obtenerAcciones(codLote, codExplotacion);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipoAccion"));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fechaAccion"));
            int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales"));

            listaAcciones.add(new Accion(id,tipo, fecha, cantidad));
        }
        cursor.close();

        adapter = new AccionAdapter(listaAcciones, accion -> {

            boolean eliminada = dbHelper.eliminarAccion(accion.getId());

            if (eliminada) {
                cargarAcciones(); // Recargar lista
            }
        });

        recyclerView.setAdapter(adapter);
    }




}



