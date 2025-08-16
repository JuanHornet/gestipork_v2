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

public class PesarDialogFragment extends DialogFragment {

    private String idExplotacion, idLote;

    public static PesarDialogFragment newInstance(String idExplotacion, String idLote) {
        PesarDialogFragment frag = new PesarDialogFragment();
        Bundle args = new Bundle();
        args.putString("id_explotacion", idExplotacion);
        args.putString("id_lote", idLote);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        idExplotacion = getArguments().getString("id_explotacion");
        idLote = getArguments().getString("id_lote");

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_pesar, null);
        EditText etPeso = view.findViewById(R.id.etPeso);
        EditText etFecha = view.findViewById(R.id.etFecha);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        etFecha.setText(sdf.format(Calendar.getInstance().getTime()));
        etFecha.setOnClickListener(v -> mostrarDatePicker(etFecha));

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("Registrar peso")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String pesoStr = etPeso.getText().toString().trim();
                    String fecha = etFecha.getText().toString().trim();

                    if (pesoStr.isEmpty()) {
                        Toast.makeText(getContext(), "Introduce un peso", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int peso = Integer.parseInt(pesoStr);
                    DBHelper db = new DBHelper(getContext());
                    db.insertarPeso(idExplotacion, idLote, peso, fecha);
                    Toast.makeText(getContext(), "Peso registrado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .create();
    }

    private void mostrarDatePicker(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(),
                (view, year, month, day) -> target.setText(String.format(Locale.getDefault(),
                        "%02d-%02d-%04d", day, month + 1, year)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
