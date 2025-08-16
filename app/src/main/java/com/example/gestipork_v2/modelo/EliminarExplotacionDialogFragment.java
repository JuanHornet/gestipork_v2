package com.example.gestipork_v2.modelo;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.ExplotacionService;
import com.example.gestipork_v2.network.SupabaseConfig;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EliminarExplotacionDialogFragment extends DialogFragment {

    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_UUID_EXPLOTACION = "uuid_explotacion";

    public static EliminarExplotacionDialogFragment newInstance(String nombre, String uuidExplotacion) {
        EliminarExplotacionDialogFragment fragment = new EliminarExplotacionDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE, nombre);
        args.putString(ARG_UUID_EXPLOTACION, uuidExplotacion);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = requireContext();
        String nombre = getArguments().getString(ARG_NOMBRE);
        String uuidExplotacion = getArguments().getString(ARG_UUID_EXPLOTACION);

        return new AlertDialog.Builder(context)
                .setTitle("Eliminar Explotación")
                .setMessage("¿Estás seguro de que quieres eliminar la explotación \"" + nombre + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    DBHelper dbHelper = new DBHelper(context);
                    boolean eliminado = dbHelper.eliminarExplotacionPorUUID(uuidExplotacion);

                    if (eliminado) {
                        Toast.makeText(context, "Explotación eliminada", Toast.LENGTH_SHORT).show();
                        ((DashboardActivity) requireActivity()).cargarExplotaciones();

                        // Llamar a Supabase para eliminar remotamente
                        ExplotacionService service = ApiClient.getClient().create(ExplotacionService.class);
                        Call<Void> call = service.eliminarExplotacion(
                                "eq." + uuidExplotacion,  // ✅ filtro de Supabase
                                SupabaseConfig.getAuthHeader(),
                                SupabaseConfig.getApiKey(),
                                SupabaseConfig.getContentType()
                        );


                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(context, "Error al eliminar en Supabase: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(context, "Error de red al eliminar en Supabase", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(context, "Error al eliminar en local", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
    }
}
