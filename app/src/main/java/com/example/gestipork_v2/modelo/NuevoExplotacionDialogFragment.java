package com.example.gestipork_v2.modelo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.ExplotacionService;
import com.example.gestipork_v2.network.SupabaseConfig;

import java.util.UUID;

import retrofit2.Call;

public class NuevoExplotacionDialogFragment extends DialogFragment {

    public interface OnExplotacionCreadaListener {
        void onExplotacionCreada();
    }

    private OnExplotacionCreadaListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnExplotacionCreadaListener) {
            listener = (OnExplotacionCreadaListener) context;
        } else {
            throw new RuntimeException(context.toString() + " debe implementar OnExplotacionCreadaListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nueva_explotacion, null);
        EditText editTextNombre = view.findViewById(R.id.editTextNombreExplotacion);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Nueva Explotación")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = editTextNombre.getText().toString().trim();
                    if (!TextUtils.isEmpty(nombre)) {
                        guardarExplotacion(nombre);
                    } else {
                        Toast.makeText(getContext(), "Introduce un nombre", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void guardarExplotacion(String nombre) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        String uuidUsuario = prefs.getString("userUUID", null);

        if (uuidUsuario == null) {
            Toast.makeText(getContext(), "Error: usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        String uuidExplotacion = UUID.randomUUID().toString();
        String codExplotacion = "EXP_" + uuidUsuario.substring(0, 8) + "_" + uuidExplotacion.substring(0, 8);

        DBHelper dbHelper = new DBHelper(requireContext());
        boolean insertado = dbHelper.insertarExplotacionNueva(nombre, uuidUsuario, uuidExplotacion, codExplotacion);

        if (insertado) {
            Toast.makeText(getContext(), "Explotación guardada localmente", Toast.LENGTH_SHORT).show();

            // Enviar a Supabase
            Explotacion nuevaExplotacion = new Explotacion(uuidExplotacion, nombre, uuidUsuario, codExplotacion);

            ExplotacionService service = ApiClient.getClient().create(ExplotacionService.class);
            Call<Void> call = service.insertarExplotacion(
                    nuevaExplotacion,
                    SupabaseConfig.getAuthHeader(),
                    SupabaseConfig.getApiKey(),
                    SupabaseConfig.getContentType()
            );

            call.enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        if (listener != null) listener.onExplotacionCreada();
                    } else {
                        Toast.makeText(getContext(), "Error Supabase: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Fallo de red al subir explotación", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getContext(), "Error al guardar en SQLite", Toast.LENGTH_SHORT).show();
        }
    }
}