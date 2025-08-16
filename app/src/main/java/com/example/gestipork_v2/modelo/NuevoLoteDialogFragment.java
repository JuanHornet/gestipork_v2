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

    private static final String ARG_COD_EXPLOTACION = "id_explotacion";

    private String idExplotacion;
    private EditText inputCodLote;
    private Spinner spinnerRaza;


    public static NuevoLoteDialogFragment newInstance(String idExplotacion) {
        NuevoLoteDialogFragment fragment = new NuevoLoteDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COD_EXPLOTACION, idExplotacion);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            idExplotacion = getArguments().getString(ARG_COD_EXPLOTACION);
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
        String idLote = inputCodLote.getText().toString().trim();
        String raza = spinnerRaza.getSelectedItem().toString();

        if (TextUtils.isEmpty(idLote)) {
            Toast.makeText(getContext(), "El código del lote es obligatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("Selecciona raza".equals(raza)) {
            Toast.makeText(getContext(), "Debes seleccionar una raza válida", Toast.LENGTH_SHORT).show();
            return;
        }

        DBHelper dbHelper = new DBHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Ya recibimos el UUID correcto de la explotación
        String idExplotacion = this.idExplotacion;

        // Verificar si ya existe ese lote para esta explotación
        Cursor cursor = db.rawQuery("SELECT id FROM lotes WHERE id = ? AND id_explotacion = ?",
                new String[]{idLote, idExplotacion});
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
        values.put("nombre_lote", idLote);
        values.put("raza", raza);
        values.put("id_explotacion", idExplotacion); // UUID real
        values.put("estado", 1);
        values.put("color", "#F7F1F9");
        values.put("nDisponibles", 0);
        values.put("nIniciales", 0);
        values.putNull("cod_paridera");
        values.putNull("cod_cubricion");
        values.putNull("cod_itaca");
        values.put("sincronizado", 0);
        values.put("fecha_actualizacion", obtenerFechaActual());

        long resultado = db.insert("lotes", null, values);

        if (resultado != -1) {
            dbHelper.insertarRegistrosRelacionadosLote(getContext(), db, uuidLote, idExplotacion, raza);
            Toast.makeText(getContext(), "Lote guardado", Toast.LENGTH_SHORT).show();

            if (getActivity() instanceof LotesActivity) {
                ((LotesActivity) getActivity()).recargarLotes();
            }
        } else {
            Toast.makeText(getContext(), "Error al guardar lote", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }


}
