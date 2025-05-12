package com.example.proyecto_gestipork.modelo;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContarDialogFragment extends DialogFragment {

    private static final String ARG_COD_EXPLOTACION = "cod_explotacion";
    private static final String ARG_COD_LOTE = "cod_lote";

    private String codExplotacion;
    private String codLote;

    public static ContarDialogFragment newInstance(String codExplotacion, String codLote) {
        ContarDialogFragment fragment = new ContarDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COD_EXPLOTACION, codExplotacion);
        args.putString(ARG_COD_LOTE, codLote);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            codExplotacion = getArguments().getString(ARG_COD_EXPLOTACION);
            codLote = getArguments().getString(ARG_COD_LOTE);
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_contar, null);

        Spinner spinnerLote = view.findViewById(R.id.spinnerLote); // ⛔ NO visible en esta versión
        spinnerLote.setVisibility(View.GONE);

        EditText editNumero = view.findViewById(R.id.editNumeroAnimales);
        EditText editObservaciones = view.findViewById(R.id.editObservaciones);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Nuevo conteo")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nAnimalesStr = editNumero.getText().toString().trim();
                    String observaciones = editObservaciones.getText().toString().trim();

                    if (TextUtils.isEmpty(nAnimalesStr)) {
                        Toast.makeText(getContext(), "El número de animales es obligatorio", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int nAnimales;
                    try {
                        nAnimales = Integer.parseInt(nAnimalesStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Introduce un número válido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Guardar en BD
                    DBHelper dbHelper = new DBHelper(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put("cod_explotacion", codExplotacion);
                    values.put("cod_lote", codLote);
                    values.put("nAnimales", nAnimales);
                    values.put("observaciones", observaciones);
                    values.put("fecha", obtenerFechaActual());

                    long resultado = db.insert("contar", null, values);
                    db.close();

                    if (resultado != -1) {
                        Toast.makeText(getContext(), "Conteo guardado", Toast.LENGTH_SHORT).show();
                        // Notificar a la actividad para actualizar la lista
                        if (getActivity() instanceof ContarActivity) {
                            ((ContarActivity) getActivity()).recargarLista();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al guardar conteo", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private String obtenerFechaActual() {
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }


}
