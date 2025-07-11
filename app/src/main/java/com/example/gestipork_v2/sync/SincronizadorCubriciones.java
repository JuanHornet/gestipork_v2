package com.example.gestipork_v2.sync;

import android.content.Context;
import android.util.Log;

import com.example.gestipork_v2.modelo.Cubriciones;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.CubricionService;
import com.example.gestipork_v2.repository.CubricionRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class SincronizadorCubriciones {

    private static final String TAG = "SINCRONIZADOR_CUBRICIONES";

    public static void sincronizar(Context context, String authHeader, String apiKey) {
        CubricionRepository repository = new CubricionRepository(context);
        repository.subirCubricionesNoSincronizadas(authHeader, apiKey);
    }

}
