package com.example.gestipork_v2.modelo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.ConstantesPrefs;
import com.example.gestipork_v2.data.DBHelper;

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

                        // ✅ Obtener UUID del usuario desde SharedPreferences
                        SharedPreferences prefs = context.getSharedPreferences(ConstantesPrefs.PREFS_LOGIN, Context.MODE_PRIVATE);
                        String uuidUsuario = prefs.getString(ConstantesPrefs.PREFS_USER_UUID, null);


                        if (uuidUsuario == null) {
                            Toast.makeText(context, "Error: usuario no identificado", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DBHelper dbHelper = new DBHelper(context);

                        // ✅ Actualizar por UUID en lugar de ID entero
                        boolean actualizado = dbHelper.actualizarNombreExplotacionPorUUID(nombreActual, nuevoNombre, uuidUsuario);
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
