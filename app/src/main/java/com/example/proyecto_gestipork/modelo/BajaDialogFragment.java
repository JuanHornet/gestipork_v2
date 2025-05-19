package com.example.proyecto_gestipork.modelo;

import android.app.DatePickerDialog;
import android.app.Dialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BajaDialogFragment extends DialogFragment {

    private String codLote;
    private String codExplotacion;
    private static final String[] TIPOS_ALIMENTACION = {"Bellota", "Cebo Campo", "Cebo"};

    public static BajaDialogFragment newInstance(String codLote, String codExplotacion) {
        BajaDialogFragment frag = new BajaDialogFragment();
        Bundle args = new Bundle();
        args.putString("cod_lote", codLote);
        args.putString("cod_explotacion", codExplotacion);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        codLote = getArguments().getString("cod_lote");
        codExplotacion = getArguments().getString("cod_explotacion");

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_baja, null);

        Spinner spinnerTipo = view.findViewById(R.id.spinnerTipoAlimentacion);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, TIPOS_ALIMENTACION);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        EditText etCantidad = view.findViewById(R.id.etCantidad);
        EditText etFecha = view.findViewById(R.id.etFecha);
        EditText etObservaciones = view.findViewById(R.id.etObservaciones);

        // Poner fecha actual
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        etFecha.setText(sdf.format(Calendar.getInstance().getTime()));

        etFecha.setOnClickListener(v -> mostrarDatePicker(etFecha));

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String tipoAlimentacion = spinnerTipo.getSelectedItem().toString();
                    String cantidadStr = etCantidad.getText().toString().trim();
                    String fecha = etFecha.getText().toString().trim();
                    String observaciones = etObservaciones.getText().toString().trim();

                    if (cantidadStr.isEmpty()) {
                        Toast.makeText(getContext(), "Introduce una cantidad", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int cantidad = Integer.parseInt(cantidadStr);
                    DBHelper dbHelper = new DBHelper(getContext());

                    // Validar animales disponibles
                    int disponibles = dbHelper.obtenerAnimalesAlimentacion(codLote, codExplotacion, tipoAlimentacion);
                    if (cantidad > disponibles) {
                        Toast.makeText(getContext(),
                                "No hay suficientes animales en " + tipoAlimentacion + ". Disponibles: " + disponibles,
                                Toast.LENGTH_LONG).show();
                        return; // Salir sin registrar la baja
                    }

                    // Registrar la baja
                    dbHelper.insertarSalida("Muerte", tipoAlimentacion, cantidad,
                            fecha, codLote, codExplotacion, observaciones);

                    Toast.makeText(getContext(), "Baja registrada correctamente", Toast.LENGTH_SHORT).show();

                    // Actualizar vistas en DetalleLoteActivity si está activo
                    if (getActivity() instanceof DetalleLoteActivity) {
                        ((DetalleLoteActivity) getActivity()).actualizarAnimalesDisponibles();
                        ((DetalleLoteActivity) getActivity()).actualizarAlimentacionCardView();
                    }

                })
                .setNegativeButton("Cancelar", null);

        return builder.create();
    }

    private void mostrarDatePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(),
                (view, year, month, day) -> {
                    String fecha = String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month + 1, year);
                    target.setText(fecha);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
