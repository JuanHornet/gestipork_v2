package com.example.gestipork_v2.modelo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MoverAlimentacionDialogFragment extends DialogFragment {

    private static final String[] TIPOS_ALIMENTACION = {"Bellota", "Cebo Campo", "Cebo"};

    private String idLote;
    private String idExplotacion;
    private String tipoOrigen;
    private int disponibles;

    public static MoverAlimentacionDialogFragment newInstance(String codLote, String codExplotacion, String tipoOrigen, int disponibles) {
        MoverAlimentacionDialogFragment frag = new MoverAlimentacionDialogFragment();
        Bundle args = new Bundle();
        args.putString("id_lote", codLote);
        args.putString("id_explotacion", codExplotacion);
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
        idLote = getArguments().getString("id_lote");
        idExplotacion = getArguments().getString("id_explotacion");
        tipoOrigen = getArguments().getString("tipo_origen");
        disponibles = getArguments().getInt("disponibles");

        Spinner spinnerDestino = view.findViewById(R.id.spinner_destino_alimentacion);
        EditText editCantidad = view.findViewById(R.id.edit_cantidad_mover);
        EditText editFecha = view.findViewById(R.id.edit_fecha_mover);  // ✅ aquí está bien colocado

        // Configurar DatePicker
        editFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog picker = new DatePickerDialog(requireContext(),
                    (view1, year, month, dayOfMonth) -> {
                        String fecha = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                        editFecha.setText(fecha);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            picker.show();
        });

        // Filtrar tipos destino
        List<String> opcionesDestino = new ArrayList<>();
        for (String tipo : TIPOS_ALIMENTACION) {
            if (!tipo.equals(tipoOrigen)) {
                opcionesDestino.add(tipo);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, opcionesDestino);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDestino.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Mover animales")
                .setView(view)
                .setPositiveButton("Mover", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String destino = spinnerDestino.getSelectedItem().toString();
                String cantidadStr = editCantidad.getText().toString().trim();
                String fechaCambio = editFecha.getText().toString().trim(); // recoger fecha

                if (cantidadStr.isEmpty() || fechaCambio.isEmpty()) {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cantidad = Integer.parseInt(cantidadStr);
                if (cantidad <= 0 || cantidad > disponibles) {
                    Toast.makeText(context, "Cantidad inválida. Máx: " + disponibles, Toast.LENGTH_SHORT).show();
                    return;
                }

                DBHelper dbHelper = new DBHelper(context);
                dbHelper.restarAnimalesAlimentacion(idLote, idExplotacion, tipoOrigen, cantidad);
                dbHelper.sumarAnimalesAlimentacionConFecha(idLote, idExplotacion, destino, cantidad, fechaCambio);

                Toast.makeText(context, "Movimiento realizado", Toast.LENGTH_SHORT).show();

                if (getActivity() instanceof OnAlimentacionActualizadaListener) {
                    ((OnAlimentacionActualizadaListener) getActivity()).onAlimentacionActualizada();
                }

                dialog.dismiss();
            });

        });

        return dialog;
    }


    public interface OnAlimentacionActualizadaListener {
        void onAlimentacionActualizada();
    }
}
