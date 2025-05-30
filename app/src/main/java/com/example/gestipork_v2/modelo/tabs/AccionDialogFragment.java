package com.example.gestipork_v2.modelo.tabs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.DetalleLoteActivity;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_accion, null);

        spinnerTipo = view.findViewById(R.id.spinner_tipo_accion);
        editFecha = view.findViewById(R.id.edit_fecha_accion);
        editCantidad = view.findViewById(R.id.edit_cantidad);
        editObservaciones = view.findViewById(R.id.edit_observaciones);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                new String[]{"Selecciona opción", "Destete", "Circovirus", "Septicemia", "Mal Rojo", "Anillado", "Desparasitado", "Castración hembras", "Castración Machos"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        if (accionExistente != null) {
            int index = adapter.getPosition(accionExistente.getTipo());
            if (index >= 0) spinnerTipo.setSelection(index);
            editFecha.setText(accionExistente.getFecha());
            editCantidad.setText(String.valueOf(accionExistente.getCantidad()));
            editObservaciones.setText(accionExistente.getObservaciones());
        }

        editFecha.setOnClickListener(v -> mostrarDatePicker());

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(accionExistente == null ? "Nueva Acción" : "Editar Acción")
                .setView(view)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String tipo = spinnerTipo.getSelectedItem().toString();
                String fecha = editFecha.getText().toString().trim();
                String cantidadStr = editCantidad.getText().toString().trim();
                String observacion = editObservaciones.getText().toString().trim();

                if (tipo.equals("Selecciona opción")) {
                    Toast.makeText(context, "Selecciona un tipo de acción válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (fecha.isEmpty() || cantidadStr.isEmpty()) {
                    Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cantidad = Integer.parseInt(cantidadStr);
                DBHelper dbHelper = new DBHelper(context);

                if (accionExistente != null && tipo.equalsIgnoreCase("Destete")) {
                    new AlertDialog.Builder(context)
                            .setTitle("Modificar acción Destete")
                            .setMessage("Modificar esta acción también cambiará el número de animales del lote. ¿Deseas continuar?")
                            .setPositiveButton("Sí", (confirmDialog, which) -> {
                                dbHelper.actualizarAccion(accionExistente.getId(), tipo, cantidad, fecha, observacion);
                                ContentValues values = new ContentValues();
                                values.put("nDisponibles", cantidad);
                                values.put("nIniciales", cantidad);
                                dbHelper.getWritableDatabase().update("lotes", values, "cod_lote = ? AND cod_explotacion = ?", new String[]{codLote, codExplotacion});
                                if (callback != null) callback.onAccionGuardada();
                                dialog.dismiss();
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                } else {
                    if (accionExistente == null) {
                        dbHelper.insertarAccion(tipo, cantidad, fecha, codLote, codExplotacion, observacion);

                        if (tipo.equalsIgnoreCase("Destete") && getActivity() instanceof DetalleLoteActivity) {
                            ((DetalleLoteActivity) getActivity()).actualizarAnimalesDisponibles();
                            ((DetalleLoteActivity) getActivity()).actualizarAlimentacionCardView();
                        }
                    } else {
                        dbHelper.actualizarAccion(accionExistente.getId(), tipo, cantidad, fecha, observacion);
                    }

                    if (callback != null) callback.onAccionGuardada();
                    dialog.dismiss();

                }
            });
        });

        return dialog;
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

        picker.show();
    }




}
