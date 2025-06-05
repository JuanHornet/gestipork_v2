package com.example.gestipork_v2.modelo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.gestipork_v2.data.DBHelper;

public class EliminarExplotacionDialogFragment extends DialogFragment {

    private static final String ARG_NOMBRE = "nombre";

    public static EliminarExplotacionDialogFragment newInstance(String nombre) {
        EliminarExplotacionDialogFragment fragment = new EliminarExplotacionDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE, nombre);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = requireContext();
        String nombre = getArguments().getString(ARG_NOMBRE);

        return new AlertDialog.Builder(context)
                .setTitle("Eliminar Explotación")
                .setMessage("¿Seguro que deseas eliminar \"" + nombre + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // ✅ Obtener UUID del usuario desde SharedPreferences
                    SharedPreferences prefs = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                    String uuidUsuario = prefs.getString("userUUID", null);

                    if (uuidUsuario == null) {
                        Toast.makeText(context, "Error: usuario no identificado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DBHelper dbHelper = new DBHelper(context);
                    // ✅ Usar nuevo método basado en UUID
                    boolean eliminado = dbHelper.eliminarExplotacionPorNombreYUUID(nombre, uuidUsuario);
                    if (eliminado) {
                        Toast.makeText(context, "Explotación eliminada", Toast.LENGTH_SHORT).show();
                        ((DashboardActivity) requireActivity()).cargarExplotaciones();
                    } else {
                        Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
    }
}
