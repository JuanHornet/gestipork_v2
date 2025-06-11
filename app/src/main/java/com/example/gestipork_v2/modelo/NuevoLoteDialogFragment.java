package com.example.gestipork_v2.modelo;

import static com.example.gestipork_v2.base.FechaUtils.obtenerFechaActual;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
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

public class NuevoLoteDialogFragment extends DialogFragment {

    private static final String ARG_COD_EXPLOTACION = "cod_explotacion";

    private String codExplotacion;
    private EditText inputCodLote;
    private Spinner spinnerRaza;

    public static NuevoLoteDialogFragment newInstance(String codExplotacion) {
        NuevoLoteDialogFragment fragment = new NuevoLoteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COD_EXPLOTACION, codExplotacion);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            codExplotacion = getArguments().getString(ARG_COD_EXPLOTACION);
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nuevo_lote, null);

        inputCodLote = view.findViewById(R.id.editTextCodLote);
        spinnerRaza = view.findViewById(R.id.spinnerRaza);

        String[] opcionesRaza = {"Selecciona raza", "Ibérico 100%", "Cruzado 50%"};
        ArrayAdapter<String> adapterRaza = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, opcionesRaza);
        spinnerRaza.setAdapter(adapterRaza);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Nuevo Lote")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> guardarLote())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void guardarLote() {
        String codLote = inputCodLote.getText().toString().trim();
        String raza = spinnerRaza.getSelectedItem().toString();

        if (TextUtils.isEmpty(codLote)) {
            Toast.makeText(getContext(), "El código del lote es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("Selecciona raza".equals(raza)) {
            Toast.makeText(getContext(), "Debes seleccionar una raza válida", Toast.LENGTH_SHORT).show();
            return;
        }

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Verificar si existe lote en esta explotación
        Cursor cursor = db.rawQuery("SELECT id FROM lotes WHERE cod_lote = ? AND cod_explotacion = ?",
                new String[]{codLote, codExplotacion});
        if (cursor.moveToFirst()) {
            cursor.close();
            db.close();
            Toast.makeText(getContext(), "Este lote ya existe en esta explotación", Toast.LENGTH_SHORT).show();
            return;
        }
        cursor.close();
        String uuidLote = java.util.UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put("id", uuidLote);
        values.put("cod_lote", codLote);
        values.put("raza", raza);
        values.put("cod_explotacion", codExplotacion);
        values.put("estado", 1);
        values.put("color", "#F7F1F9");
        values.put("nDisponibles", 0);
        values.put("nIniciales", 0);
        values.put("cod_paridera", "");
        values.put("cod_cubricion", "");
        values.put("cod_itaca", "");
        values.put("sincronizado", 0);
        values.put("fecha_actualizacion", obtenerFechaActual());

        long resultado = db.insert("lotes", null, values);

        if (resultado != -1) {
            // ✅ Insertar registros relacionados
            dbHelper.insertarRegistrosRelacionadosLote(getContext(), db, codLote, codExplotacion, raza);
            Toast.makeText(getContext(), "Lote guardado", Toast.LENGTH_SHORT).show();

            // ✅ Refrescar la lista en LotesActivity
            if (getActivity() instanceof LotesActivity) {
                ((LotesActivity) getActivity()).recargarLotes();
            }
        } else {
            Toast.makeText(getContext(), "Error al guardar lote", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
