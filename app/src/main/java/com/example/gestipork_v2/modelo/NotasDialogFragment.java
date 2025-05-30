package com.example.gestipork_v2.modelo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotasDialogFragment extends DialogFragment {

    private String codLote, codExplotacion;

    // INTERFAZ CALLBACK
    public interface OnNotaGuardadaListener {
        void onNotaGuardada();
    }

    private OnNotaGuardadaListener listener;

    public void setOnNotaGuardadaListener(OnNotaGuardadaListener listener) {
        this.listener = listener;
    }

    public static NotasDialogFragment newInstance(String codLote, String codExplotacion) {
        NotasDialogFragment frag = new NotasDialogFragment();
        Bundle args = new Bundle();
        args.putString("cod_lote", codLote);
        args.putString("cod_explotacion", codExplotacion);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        codLote = getArguments().getString("cod_lote");
        codExplotacion = getArguments().getString("cod_explotacion");

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_nota, null);
        EditText etFecha = view.findViewById(R.id.etFechaNota);
        EditText etObservacion = view.findViewById(R.id.etObservacionNota);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        etFecha.setText(sdf.format(Calendar.getInstance().getTime()));

        etFecha.setOnClickListener(v -> mostrarDatePicker(etFecha));

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Nueva Nota")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String fecha = etFecha.getText().toString().trim();
                    String observacion = etObservacion.getText().toString().trim();

                    if (observacion.isEmpty()) {
                        Toast.makeText(getContext(), "La observación no puede estar vacía", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DBHelper db = new DBHelper(getContext());
                    db.insertarNota(codLote, codExplotacion, fecha, observacion);

                    if (listener != null) {
                        listener.onNotaGuardada();   // AVISAR PARA REFRESCAR
                    }

                    Toast.makeText(getContext(), "Nota registrada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .create();
    }

    private void mostrarDatePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> target.setText(
                        String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, month + 1, year)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
