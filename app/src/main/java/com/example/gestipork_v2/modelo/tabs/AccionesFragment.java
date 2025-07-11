package com.example.gestipork_v2.modelo.tabs;

import android.content.ContentValues;
import android.database.Cursor;
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
import com.example.gestipork_v2.modelo.DetalleLoteActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AccionesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private AccionAdapter adapter;
    private List<Accion> listaAcciones;

    private String idLote, idExplotacion;

    public AccionesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_acciones, container, false);

        // Leer valores de id_lote y cod_explotacion del bundle primero
        if (getArguments() != null) {
            idLote = getArguments().getString("id_lote");
            idExplotacion = getArguments().getString("cod_explotacion");
        }

        recyclerView = vista.findViewById(R.id.recycler_acciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fabAdd = vista.findViewById(R.id.fab_add_accion);
        fabAdd.setOnClickListener(v -> {
            AccionDialogFragment dialog = new AccionDialogFragment(
                    idLote,
                    idExplotacion,
                    null,
                    () -> {
                        cargarAcciones(); // Recarga la lista

                        // Verificar si fue destete
                        DBHelper dbHelper = new DBHelper(getContext());
                        Cursor c = dbHelper.getReadableDatabase().rawQuery(
                                "SELECT tipoAccion FROM acciones WHERE id_lote = ? AND cod_explotacion = ? ORDER BY fechaAccion DESC LIMIT 1",
                                new String[]{idLote, idExplotacion}
                        );

                        if (c.moveToFirst()) {
                            String tipo = c.getString(0);
                            if (tipo.equalsIgnoreCase("Destete")) {
                                if (getActivity() instanceof DetalleLoteActivity) {
                                    ((DetalleLoteActivity) getActivity()).actualizarAnimalesDisponibles();
                                }
                            }
                        }
                        c.close();
                    }
            );
            dialog.show(getChildFragmentManager(), "NuevaAccion");
        });


        cargarAcciones();

        return vista;
    }


    private void cargarAcciones() {
        DBHelper dbHelper = new DBHelper(getContext());
        listaAcciones = new ArrayList<>();

        Cursor cursor = dbHelper.obtenerAcciones(idLote, idExplotacion);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
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
                        idLote,
                        idExplotacion,
                        accion,
                        () -> {
                            cargarAcciones();

                            if (accion.getTipo().equals("Destete")) {
                                // Forzar recarga en la Activity directamente
                                if (getActivity() instanceof DetalleLoteActivity) {
                                    ((DetalleLoteActivity) getActivity()).actualizarAnimalesDisponibles();
                                }
                            }
                        }
                );
                dialog.show(getChildFragmentManager(), "EditarAccion");
            }


            @Override
            public void onEliminarAccion(Accion accion) {
                boolean esDestete = accion.getTipo().equalsIgnoreCase("Destete");

                String mensaje = esDestete
                        ? "Eliminar esta acción de destete también pondrá a cero el número de animales en el lote. ¿Deseas continuar?"

                        : "¿Estás seguro de que quieres eliminar esta acción?";

                new AlertDialog.Builder(getContext())
                        .setTitle("Eliminar acción")
                        .setMessage(mensaje)
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            DBHelper dbHelper = new DBHelper(getContext());

                            if (esDestete) {
                                ContentValues values = new ContentValues();
                                values.put("nDisponibles", 0);
                                values.put("nIniciales", 0);
                                dbHelper.getWritableDatabase().update(
                                        "lotes",
                                        values,
                                        "id = ?",
                                        new String[]{idLote}
                                );
                            }

                            boolean eliminado = dbHelper.eliminarAccion(accion.getId());
                            if (eliminado) {
                                cargarAcciones();

                                // ACTUALIZAR LA VISTA SUPERIOR DEL LOTE
                                if (getActivity() instanceof DetalleLoteActivity) {
                                    ((DetalleLoteActivity) getActivity()).actualizarAnimalesDisponibles();
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



