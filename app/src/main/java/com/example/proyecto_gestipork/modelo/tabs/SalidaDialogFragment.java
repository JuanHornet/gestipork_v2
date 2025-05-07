package com.example.proyecto_gestipork.modelo.tabs;



import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;
import com.example.proyecto_gestipork.modelo.SalidasExplotacion;

import java.util.Calendar;
import java.util.Locale;

public class SalidaDialogFragment extends DialogFragment {

    private Spinner spinnerTipoSalida, spinnerTipoAlimentacion;
    private EditText editFecha, editCantidad, editObservaciones;
    private Button btnGuardar, btnCancelar;

    private SalidasExplotacion salidaExistente;
    private String codLote, codExplotacion;
    private OnSalidaGuardadaListener callback;

    public interface OnSalidaGuardadaListener {
        void onSalidaGuardada();
    }

    public SalidaDialogFragment(String codLote, String codExplotacion, SalidasExplotacion salidaExistente, OnSalidaGuardadaListener callback) {
        this.codLote = codLote;
        this.codExplotacion = codExplotacion;
        this.salidaExistente = salidaExistente;
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_salida, container, false);

        spinnerTipoSalida = view.findViewById(R.id.spinner_tipo_salida);
        spinnerTipoAlimentacion = view.findViewById(R.id.spinner_tipo_alimentacion);
        editFecha = view.findViewById(R.id.edit_fecha_salida);
        editCantidad = view.findViewById(R.id.edit_n_animales);
        editObservaciones = view.findViewById(R.id.edit_observaciones);
        btnGuardar = view.findViewById(R.id.btn_guardar_salida);
        btnCancelar = view.findViewById(R.id.btn_cancelar_salida);

        // Spinners
        ArrayAdapter<String> salidaAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Selecciona motivo", "Sacrificio", "Fallecimiento", "Venta", "Otro"});
        salidaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoSalida.setAdapter(salidaAdapter);

        ArrayAdapter<String> alimentacionAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Bellota", "Cebo Campo", "Cebo"});
        alimentacionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAlimentacion.setAdapter(alimentacionAdapter);

        // Si es edición
        if (salidaExistente != null) {
            editFecha.setText(formatearFecha(salidaExistente.getFechaSalida()));
            editCantidad.setText(String.valueOf(salidaExistente.getnAnimales()));
            editObservaciones.setText(salidaExistente.getObservacion());

            int indexMotivo = salidaAdapter.getPosition(salidaExistente.getTipoSalida());
            if (indexMotivo >= 0) spinnerTipoSalida.setSelection(indexMotivo);

            int indexAlim = alimentacionAdapter.getPosition(salidaExistente.getTipoAlimentacion());
            if (indexAlim >= 0) spinnerTipoAlimentacion.setSelection(indexAlim);
        }

        editFecha.setOnClickListener(v -> mostrarDatePicker());

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            String motivo = spinnerTipoSalida.getSelectedItem().toString();
            String alimentacion = spinnerTipoAlimentacion.getSelectedItem().toString();
            String fecha = editFecha.getText().toString().trim();
            String cantidadStr = editCantidad.getText().toString().trim();
            String observaciones = editObservaciones.getText().toString().trim();

            if (motivo.equals("Selecciona motivo") || fecha.isEmpty() || cantidadStr.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            DBHelper dbHelper = new DBHelper(getContext());

            if (salidaExistente == null) {
                dbHelper.insertarSalida(motivo, alimentacion, cantidad, fecha, codLote, codExplotacion, observaciones);
            } else {
                dbHelper.actualizarSalida(salidaExistente.getId(), motivo, alimentacion, cantidad, fecha, observaciones);
            }

            if (callback != null) callback.onSalidaGuardada();
            dismiss();
        });

        return view;
    }

    private void mostrarDatePicker() {
        Locale.setDefault(new Locale("es", "ES"));
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, year, month, day) -> {
                    String fecha = String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month + 1, year);
                    editFecha.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        picker.show();
    }

    private String formatearFecha(java.util.Date date) {
        if (date == null) return "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return String.format(Locale.getDefault(), "%02d-%02d-%04d",
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR));
    }
}
