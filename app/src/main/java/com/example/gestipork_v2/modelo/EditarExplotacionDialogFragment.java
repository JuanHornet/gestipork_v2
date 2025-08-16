package com.example.gestipork_v2.modelo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.ConstantesPrefs;
import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.ExplotacionService;
import com.example.gestipork_v2.network.SupabaseConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditarExplotacionDialogFragment extends DialogFragment {

    private static final String ARG_NOMBRE_ACTUAL = "nombre_actual";
    private static final String ARG_UUID_EXPLOTACION = "uuid_explotacion";

    public static EditarExplotacionDialogFragment newInstance(String nombreActual, String uuidExplotacion) {
        EditarExplotacionDialogFragment fragment = new EditarExplotacionDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE_ACTUAL, nombreActual);
        args.putString(ARG_UUID_EXPLOTACION, uuidExplotacion);
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = requireContext();
        String nombreActual = getArguments().getString(ARG_NOMBRE_ACTUAL);
        String uuidExplotacion = getArguments().getString(ARG_UUID_EXPLOTACION);


        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_explotacion, null);
        EditText editText = view.findViewById(R.id.edit_nombre_explotacion);
        editText.setText(nombreActual);

        return new AlertDialog.Builder(context)
                .setTitle("Editar Explotación")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevoNombre = editText.getText().toString().trim();
                    if (!nuevoNombre.isEmpty()) {

                        // ✅ Obtener UUID del usuario desde SharedPreferences
                        SharedPreferences prefs = context.getSharedPreferences(ConstantesPrefs.PREFS_LOGIN, Context.MODE_PRIVATE);
                        String uuidUsuario = prefs.getString(ConstantesPrefs.PREFS_USER_UUID, null);


                        if (uuidUsuario == null) {
                            Toast.makeText(context, "Error: usuario no identificado", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DBHelper dbHelper = new DBHelper(context);

                        // ✅ Actualizar por UUID en lugar de ID entero
                        boolean actualizado = dbHelper.actualizarNombreExplotacionPorUUID(nombreActual, nuevoNombre, uuidUsuario);
                        Explotacion explotacion = new Explotacion(uuidExplotacion, nuevoNombre, uuidUsuario);

                        ExplotacionService service = ApiClient.getClient().create(ExplotacionService.class);
                        Call<Void> call = service.actualizarExplotacion(
                                uuidExplotacion,
                                explotacion,
                                SupabaseConfig.getAuthHeader(),
                                SupabaseConfig.getApiKey(),
                                SupabaseConfig.getContentType()
                        );

                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (!response.isSuccessful() && isAdded()) {
                                    Toast.makeText(context, "Error al sincronizar con Supabase: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                if (isAdded()) {
                                    Toast.makeText(context, "Error de red al sincronizar explotación", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        if (actualizado) {
                            Toast.makeText(context, "Explotación actualizada", Toast.LENGTH_SHORT).show();
                            ((DashboardActivity) requireActivity()).cargarExplotaciones();

                            // ➕ Nueva parte para sincronizar con Supabase:
                            explotacion = new Explotacion(uuidExplotacion, nuevoNombre, uuidUsuario);
                            service = ApiClient.getClient().create(ExplotacionService.class);

                            call = service.actualizarExplotacion(
                                    "eq." + uuidExplotacion, // 👈 filtro de Supabase
                                    explotacion,
                                    SupabaseConfig.getAuthHeader(),
                                    SupabaseConfig.getApiKey(),
                                    SupabaseConfig.getContentType()
                            );


                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(context, "Explotación sincronizada", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Error Supabase: HTTP " + response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(context, "Fallo al sincronizar explotación", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
    }
}
