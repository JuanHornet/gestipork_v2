package com.example.proyecto_gestipork.modelo.tabs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.proyecto_gestipork.R;
import com.example.proyecto_gestipork.data.DBHelper;

import java.util.Calendar;
import java.util.Locale;

public class AccionDialogFragment extends DialogFragment {

    private EditText editTipo, editFecha, editCantidad;
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

        editTipo = view.findViewById(R.id.edit_tipo_accion);
        editFecha = view.findViewById(R.id.edit_fecha_accion);
        editCantidad = view.findViewById(R.id.edit_cantidad);
        btnGuardar = view.findViewById(R.id.btn_guardar_accion);
        btnCancelar = view.findViewById(R.id.btn_cancelar_accion);

        // Si se edita una acción existente
        if (accionExistente != null) {
            editTipo.setText(accionExistente.getTipo());
            editFecha.setText(accionExistente.getFecha());
            editCantidad.setText(String.valueOf(accionExistente.getCantidad()));
        }

        editFecha.setOnClickListener(v -> mostrarDatePicker());

        btnCancelar.setOnClickListener(v -> dismiss());

        btnGuardar.setOnClickListener(v -> {
            String tipo = editTipo.getText().toString().trim();
            String fecha = editFecha.getText().toString().trim();
            int cantidad = Integer.parseInt(editCantidad.getText().toString().trim());

            DBHelper dbHelper = new DBHelper(getContext());

            if (accionExistente == null) {
                dbHelper.insertarAccion(tipo, cantidad, fecha, codLote, codExplotacion);
            } else {
                dbHelper.actualizarAccion(accionExistente.getId(), tipo, cantidad, fecha);
            }

            if (callback != null) callback.onAccionGuardada();
            dismiss();
        });

        return view;
    }

    private void mostrarDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(requireContext(),
                (view, year, month, day) -> {
                    String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);
                    editFecha.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }
}
