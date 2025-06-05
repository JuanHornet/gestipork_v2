package com.example.gestipork_v2.network;


import com.example.gestipork_v2.login.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UsuarioService {

    @POST("usuarios")
    Call<Void> registrarUsuario(
            @Body Usuario usuario,
            @Header("Authorization") String authHeader,
            @Header("apikey") String apiKey,
            @Header("Content-Type") String contentType
    );
}
