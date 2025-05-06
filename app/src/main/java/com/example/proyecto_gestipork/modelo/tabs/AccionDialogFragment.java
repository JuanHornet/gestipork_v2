package com.example.proyecto_gestipork.modelo.tabs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
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

import java.util.Calendar;
import java.util.Locale;

public class AccionDialogFragment extends DialogFragment {

    private Spinner spinnerTipo;
    private EditText editFecha, editCantidad, editObservaciones;

    private Button btnGuardar, btnCancelar;

    private Accion accionExistente; // Si es null, es una nueva acción
    private String codLote, codExplotacion;
    private OnAccionGuardadaListener callback;

    public interface OnAccionGuardadaListener {
        void onAccionGuardada(); // Para refrescar el RecyclerView
    }

    public AccionDialogFragment(String codLote, String codExplotacion, Accion accion, OnAccionGuardadaListener callback) {
        this.codLote = codLote;
        this.codExplotacion = codExplotacion;
        this.accionExistente = accion;
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_accion, container, false);

        spinnerTipo = view.findViewById(R.id.spinner_tipo_accion);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Selecciona opción","Destete", "Circovirus", "Septicemia", "Mal Rojo", "Anillado", "Desparasitado", "Castración hembras", "Castración Machos"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        editFecha = view.findViewById(R.id.edit_fecha_accion);
        editCantidad = view.findViewById(R.id.edit_cantidad);
        editObservaciones = view.findViewById(R.id.edit_observaciones);
        btnGuardar = view.findViewById(R.id.btn_guardar_accion);
        btnCancelar = view.findViewById(R.id.btn_cancelar_accion);

        // Si se edita una acción existente
        if (accionExistente != null) {
            // Establecer la selección del spinner
            int index = adapter.getPosition(accionExistente.getTipo());
            if (index >= 0) {
                spinnerTipo.setSelection(index);
            }

            // Rellenar el resto de campos
            editFecha.setText(accionExistente.getFecha());
            editCantidad.setText(String.valueOf(accionExistente.getCantidad()));
            editObservaciones.setText(accionExistente.getObservaciones());
        }


        editFecha.setOnClickListener(v -> mostrarDatePicker());

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            String tipo = spinnerTipo.getSelectedItem().toString();
            String fecha = editFecha.getText().toString().trim();
            String cantidadStr = editCantidad.getText().toString().trim();
            String observacion = editObservaciones.getText().toString().trim();

            if (tipo.equals("Selecciona opción")) {
                Toast.makeText(getContext(), "Selecciona un tipo de acción válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fecha.isEmpty() || cantidadStr.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);

            DBHelper dbHelper = new DBHelper(getContext());

            if (accionExistente == null) {
                dbHelper.insertarAccion(tipo, cantidad, fecha, codLote, codExplotacion, observacion);
            } else {
                dbHelper.actualizarAccion(accionExistente.getId(), tipo, cantidad, fecha, observacion);
            }

            if (callback != null) callback.onAccionGuardada();
            dismiss();
        });


        return view;
    }

    private void mostrarDatePicker() {
        // Asegurar que se usa idioma español
        Locale spanish = new Locale("es", "ES");
        Locale.setDefault(spanish);

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY); // Esto se usará internamente

        DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String fechaFormateada = String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year);
                    editFecha.setText(fechaFormateada);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // No forzamos temas ni accedemos a getCalendarView()
        picker.show();
    }




}
