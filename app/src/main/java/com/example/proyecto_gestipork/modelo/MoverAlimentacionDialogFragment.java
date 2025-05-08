package com.example.proyecto_gestipork.modelo.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;

public class MoverAlimentacionDialogFragment extends DialogFragment {

    private static final String[] TIPOS_ALIMENTACION = {"Bellota", "Cebo Campo", "Cebo"};

    private String codLote;
    private String codExplotacion;
    private String tipoOrigen;
    private int disponibles;

    public static MoverAlimentacionDialogFragment newInstance(String codLote, String codExplotacion, String tipoOrigen, int disponibles) {
        MoverAlimentacionDialogFragment frag = new MoverAlimentacionDialogFragment();
        Bundle args = new Bundle();
        args.putString("cod_lote", codLote);
        args.putString("cod_explotacion", codExplotacion);
        args.putString("tipo_origen", tipoOrigen);
        args.putInt("disponibles", disponibles);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_mover_alimentacion, null);

        assert getArguments() != null;
        codLote = getArguments().getString("cod_lote");
        codExplotacion = getArguments().getString("cod_explotacion");
        tipoOrigen = getArguments().getString("tipo_origen");
        disponibles = getArguments().getInt("disponibles");

        Spinner spinnerDestino = view.findViewById(R.id.spinner_destino);
        EditText editCantidad = view.findViewById(R.id.edit_cantidad);

        // Configurar Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, TIPOS_ALIMENTACION);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDestino.setAdapter(adapter);

        // Seleccionar valor distinto al origen
        for (int i = 0; i < TIPOS_ALIMENTACION.length; i++) {
            if (!TIPOS_ALIMENTACION[i].equals(tipoOrigen)) {
                spinnerDestino.setSelection(i);
                break;
            }
        }

        return new AlertDialog.Builder(context)
                .setTitle("Mover animales")
                .setView(view)
                .setPositiveButton("Mover", (dialog, which) -> {
                    String destino = spinnerDestino.getSelectedItem().toString();
                    String cantidadStr = editCantidad.getText().toString().trim();

                    if (cantidadStr.isEmpty()) {
                        Toast.makeText(context, "Introduce una cantidad válida", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int cantidad = Integer.parseInt(cantidadStr);
                    if (cantidad <= 0 || cantidad > disponibles) {
                        Toast.makeText(context, "Cantidad inválida. Máx: " + disponibles, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DBHelper dbHelper = new DBHelper(context);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    // Restar en origen
                    db.execSQL("UPDATE alimentacion SET nAnimales = nAnimales - ? WHERE cod_lote = ? AND cod_explotacion = ? AND tipoAlimentacion = ?",
                            new Object[]{cantidad, codLote, codExplotacion, tipoOrigen});

                    // Sumar en destino
                    db.execSQL("UPDATE alimentacion SET nAnimales = nAnimales + ? WHERE cod_lote = ? AND cod_explotacion = ? AND tipoAlimentacion = ?",
                            new Object[]{cantidad, codLote, codExplotacion, destino});

                    db.close();
                    Toast.makeText(context, "Movimiento realizado", Toast.LENGTH_SHORT).show();

                    if (getParentFragment() instanceof OnAlimentacionActualizadaListener) {
                        ((OnAlimentacionActualizadaListener) getParentFragment()).onAlimentacionActualizada();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
    }

    public interface OnAlimentacionActualizadaListener {
        void onAlimentacionActualizada();
    }
}
