package com.example.proyecto_gestipork.modelo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;

public class EditarExplotacionDialogFragment extends DialogFragment {

    private static final String ARG_NOMBRE_ACTUAL = "nombre_actual";

    public static EditarExplotacionDialogFragment newInstance(String nombreActual) {
        EditarExplotacionDialogFragment fragment = new EditarExplotacionDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE_ACTUAL, nombreActual);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = requireContext();
        String nombreActual = getArguments().getString(ARG_NOMBRE_ACTUAL);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_explotacion, null);
        EditText editText = view.findViewById(R.id.edit_nombre_explotacion);
        editText.setText(nombreActual);

        return new AlertDialog.Builder(context)
                .setTitle("Editar Explotación")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = editText.getText().toString().trim();
                    if (!nuevoNombre.isEmpty()) {
                        SharedPreferences prefs = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
                        String email = prefs.getString("userEmail", "");
                        DBHelper dbHelper = new DBHelper(context);
                        int idUsuario = dbHelper.obtenerIdUsuarioDesdeEmail(email);

                        boolean actualizado = dbHelper.actualizarNombreExplotacion(nombreActual, nuevoNombre, idUsuario);
                        if (actualizado) {
                            Toast.makeText(context, "Explotación actualizada", Toast.LENGTH_SHORT).show();
                            ((DashboardActivity) requireActivity()).cargarExplotaciones();
                        } else {
                            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
    }
}
