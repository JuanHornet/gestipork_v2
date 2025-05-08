package com.example.proyecto_gestipork.modelo.tabs;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
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
            String observacion = cursor.getString(cursor.getColumnIndexOrThrow("observacion"));


            listaAcciones.add(new Accion(id, tipo, fecha, cantidad, observacion));

        }
        cursor.close();

        adapter = new AccionAdapter(listaAcciones, new AccionAdapter.OnAccionLongClickListener() {
            @Override
            public void onEditarAccion(Accion accion) {
                AccionDialogFragment dialog = new AccionDialogFragment(
                        codLote,
                        codExplotacion,
                        accion,
                        () -> {
                            cargarAcciones();
                            if (accion.getTipo().equals("Destete")) {
                                // Notificar a la activity
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("accion_destete_actualizada", true);
                                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                            }
                        }
                );
                dialog.show(getChildFragmentManager(), "EditarAccion");

            }

            @Override
            public void onEliminarAccion(Accion accion) {
                // Verificamos si es tipo "Destete"
                boolean esDestete = accion.getTipo().equalsIgnoreCase("Destete");

                String mensaje = esDestete
                        ? "Eliminar esta acción de destete también eliminará los animales registrados en el lote. ¿Deseas continuar?"
                        : "¿Estás seguro de que quieres eliminar esta acción?";

                new AlertDialog.Builder(getContext())
                        .setTitle("Eliminar acción")
                        .setMessage(mensaje)
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            DBHelper dbHelper = new DBHelper(getContext());

                            // Si es destete, reinicia los campos del lote
                            if (esDestete) {
                                ContentValues values = new ContentValues();
                                values.put("nDisponibles", 0);
                                values.put("nIniciales", 0);

                                dbHelper.getWritableDatabase().update(
                                        "lotes",
                                        values,
                                        "cod_lote = ? AND cod_explotacion = ?",
                                        new String[]{codLote, codExplotacion}
                                );
                            }

                            // Eliminar la acción de la tabla acciones
                            boolean eliminado = dbHelper.eliminarAccion(accion.getId());
                            if (eliminado) cargarAcciones();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }


        });


        recyclerView.setAdapter(adapter);
    }




}



