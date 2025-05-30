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
                    SharedPreferences prefs = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                    String email = prefs.getString("userEmail", "");
                    DBHelper dbHelper = new DBHelper(context);
                    int idUsuario = dbHelper.obtenerIdUsuarioDesdeEmail(email);

                    boolean eliminado = dbHelper.eliminarExplotacionPorNombre(nombre, idUsuario);
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
