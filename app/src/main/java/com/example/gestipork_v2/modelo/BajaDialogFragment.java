package com.example.gestipork_v2.modelo;

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

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BajaDialogFragment extends DialogFragment {

    private String idLote;
    private String idExplotacion;
    private static final String[] TIPOS_ALIMENTACION = {"Bellota", "Cebo Campo", "Cebo"};

    public static BajaDialogFragment newInstance(String idLote, String idExplotacion) {
        BajaDialogFragment frag = new BajaDialogFragment();
        Bundle args = new Bundle();
        args.putString("id_lote", idLote);
        args.putString("cod_explotacion", idExplotacion);
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        idLote = getArguments().getString("id_lote");
        idExplotacion = getArguments().getString("id_explotacion");

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
                    int disponibles = dbHelper.obtenerAnimalesAlimentacion(idLote, idExplotacion, tipoAlimentacion);
                    if (cantidad > disponibles) {
                        Toast.makeText(getContext(),
                                "No hay suficientes animales en " + tipoAlimentacion + ". Disponibles: " + disponibles,
                                Toast.LENGTH_LONG).show();
                        return; // Salir sin registrar la baja
                    }

                    // Registrar la baja
                    dbHelper.insertarSalida("Muerte", tipoAlimentacion, cantidad,
                            fecha, idLote, idExplotacion, observaciones);

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
