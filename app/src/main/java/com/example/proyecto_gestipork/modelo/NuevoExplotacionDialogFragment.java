package com.example.proyecto_gestipork.modelo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;

public class NuevoExplotacionDialogFragment extends DialogFragment {

    public interface OnExplotacionCreadaListener {
        void onExplotacionCreada();
    }

    private OnExplotacionCreadaListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnExplotacionCreadaListener) {
            listener = (OnExplotacionCreadaListener) context;
        } else {
            throw new RuntimeException(context.toString() + " debe implementar OnExplotacionCreadaListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nueva_explotacion, null);
        EditText editTextNombre = view.findViewById(R.id.editTextNombreExplotacion);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Nueva Explotación")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    if (!TextUtils.isEmpty(nombre)) {
                        guardarExplotacion(nombre);
                    } else {
                        Toast.makeText(getContext(), "Introduce un nombre", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void guardarExplotacion(String nombre) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String email = prefs.getString("userEmail", "");
        DBHelper dbHelper = new DBHelper(requireContext());
        int idUsuario = dbHelper.obtenerIdUsuarioDesdeEmail(email);

        boolean insertado = dbHelper.insertarExplotacion(nombre, idUsuario);
        if (insertado) {
            Toast.makeText(getContext(), "Explotación guardada", Toast.LENGTH_SHORT).show();
            if (listener != null) listener.onExplotacionCreada();
        } else {
            Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}
