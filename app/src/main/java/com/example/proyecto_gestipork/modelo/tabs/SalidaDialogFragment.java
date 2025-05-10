package com.example.proyecto_gestipork.modelo.tabs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
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
import com.example.proyecto_gestipork.modelo.SalidasExplotacion;

import java.util.Calendar;
import java.util.Locale;

public class SalidaDialogFragment extends DialogFragment {

    private Spinner spinnerTipoSalida, spinnerTipoAlimentacion;
    private EditText editFechaSalida, editCantidad, editObservaciones;
    private String codLote, codExplotacion;
    private Integer salidaId;
    // null si es nueva

    public interface OnSalidaGuardadaListener {
        void onSalidaGuardada();
    }

    private OnSalidaGuardadaListener callback;

    public SalidaDialogFragment(String codLote, String codExplotacion, Integer salidaId, OnSalidaGuardadaListener callback) {
        this.codLote = codLote;
        this.codExplotacion = codExplotacion;
        this.salidaId = salidaId;
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_salida, null);

        spinnerTipoSalida = view.findViewById(R.id.spinner_tipo_salida);
        spinnerTipoAlimentacion = view.findViewById(R.id.spinner_tipo_alimentacion);
        editFechaSalida = view.findViewById(R.id.edit_fecha_salida);
        editCantidad = view.findViewById(R.id.edit_n_animales);
        editObservaciones = view.findViewById(R.id.edit_observaciones_salida);

        // Adaptadores de spinners
        ArrayAdapter<String> adapterSalida = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Venta", "Muerte", "Otro"});
        adapterSalida.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoSalida.setAdapter(adapterSalida);

        ArrayAdapter<String> adapterAlimentacion = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Bellota", "Cebo Campo", "Cebo"});
        adapterAlimentacion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAlimentacion.setAdapter(adapterAlimentacion);

        editFechaSalida.setOnClickListener(v -> mostrarDatePicker());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(salidaId == null ? "Nueva Salida" : "Editar Salida")
                .setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String tipoSalida = spinnerTipoSalida.getSelectedItem().toString();
                String tipoAlimentacion = spinnerTipoAlimentacion.getSelectedItem().toString();
                String fecha = editFechaSalida.getText().toString().trim();
                String cantidadStr = editCantidad.getText().toString().trim();
                String observacion = editObservaciones.getText().toString().trim();

                if (fecha.isEmpty() || cantidadStr.isEmpty()) {
                    Toast.makeText(getContext(), "Fecha y cantidad son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cantidad = Integer.parseInt(cantidadStr);
                DBHelper dbHelper = new DBHelper(getContext());

                if (salidaId == null) {
                    dbHelper.insertarSalida(tipoSalida, tipoAlimentacion, cantidad, fecha, codLote, codExplotacion, observacion);
                } else {
                    dbHelper.actualizarSalida(salidaId, tipoSalida, tipoAlimentacion, cantidad, fecha, observacion);
                }

                if (callback != null) callback.onSalidaGuardada();
                dialog.dismiss();
            });
        });
        if (salidaId != null) {
            DBHelper dbHelper = new DBHelper(requireContext());
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT * FROM salidas WHERE id = ?", new String[]{String.valueOf(salidaId)}
            );

            if (cursor.moveToFirst()) {
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipoSalida"));
                String alim = cursor.getString(cursor.getColumnIndexOrThrow("tipoAlimentacion"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fechaSalida"));
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("nAnimales"));
                String obs = cursor.getString(cursor.getColumnIndexOrThrow("observacion"));

                // Asignar valores a los campos
                ArrayAdapter<String> adapterTipo = (ArrayAdapter<String>) spinnerTipoSalida.getAdapter();
                ArrayAdapter<String> adapterAlim = (ArrayAdapter<String>) spinnerTipoAlimentacion.getAdapter();

                int indexTipo = adapterTipo.getPosition(tipo);
                if (indexTipo >= 0) spinnerTipoSalida.setSelection(indexTipo);

                int indexAlim = adapterAlim.getPosition(alim);
                if (indexAlim >= 0) spinnerTipoAlimentacion.setSelection(indexAlim);

                editFechaSalida.setText(fecha);
                editCantidad.setText(String.valueOf(cantidad));
                editObservaciones.setText(obs);
            }

            cursor.close();
        }
        return dialog;


    }

    private void mostrarDatePicker() {
        Locale spanish = new Locale("es", "ES");
        Locale.setDefault(spanish);

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, year, month, day) -> {
                    String fecha = String.format("%02d-%02d-%04d", day, month + 1, year);
                    editFechaSalida.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        picker.show();
    }
}
