package com.example.gestipork_v2.modelo;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContarDialogDesdeLotesFragment extends DialogFragment {

    private static final String ARG_COD_EXPLOTACION = "id_explotacion";
    private static final String ARG_LOTES = "lista_lotes";

    private String codExplotacion;
    private List<String> listaLotes;

    public static ContarDialogDesdeLotesFragment newInstance(String codExplotacion, List<String> listaLotes) {
        ContarDialogDesdeLotesFragment fragment = new ContarDialogDesdeLotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COD_EXPLOTACION, codExplotacion);
        args.putStringArrayList(ARG_LOTES, new ArrayList<>(listaLotes));
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            codExplotacion = getArguments().getString(ARG_COD_EXPLOTACION);
            listaLotes = getArguments().getStringArrayList(ARG_LOTES);
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_contar, null);

        Spinner spinnerLote = view.findViewById(R.id.spinnerLote);
        EditText editNumero = view.findViewById(R.id.editNumeroAnimales);
        EditText editObservaciones = view.findViewById(R.id.editObservaciones);

        // Mostrar el spinner porque estamos en versión DesdeLotes
        spinnerLote.setVisibility(View.VISIBLE);

        // Llenar spinner con lotes
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, listaLotes);
        spinnerLote.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Nuevo conteo")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String codLote = spinnerLote.getSelectedItem().toString();
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
                    values.put("id_explotacion", codExplotacion);
                    values.put("id_lote", codLote);
                    values.put("nAnimales", nAnimales);
                    values.put("observaciones", observaciones);
                    values.put("fecha", obtenerFechaActual());

                    long resultado = db.insert("contar", null, values);
                    db.close();

                    if (resultado != -1) {
                        Toast.makeText(getContext(), "Conteo guardado", Toast.LENGTH_SHORT).show();
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

    public static ContarDialogDesdeLotesFragment newInstanceSeleccionarLote(String codExplotacion, List<String> listaLotes) {
        ContarDialogDesdeLotesFragment fragment = new ContarDialogDesdeLotesFragment();
        Bundle args = new Bundle();
        args.putString("id_explotacion", codExplotacion);
        args.putStringArrayList("lista_lotes", new ArrayList<>(listaLotes));
        fragment.setArguments(args);
        return fragment;
    }
}
