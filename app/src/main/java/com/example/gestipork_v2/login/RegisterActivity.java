package com.example.gestipork_v2.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gestipork_v2.R;
import com.example.gestipork_v2.data.DBHelper;

import com.example.gestipork_v2.login.Usuario;
import com.example.gestipork_v2.network.ApiClient;
import com.example.gestipork_v2.network.UsuarioService;
import com.example.gestipork_v2.network.SupabaseConfig;

import retrofit2.Call;

public class RegisterActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btnRegister;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        dbHelper = new DBHelper(this);

        btnRegister.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            String uuid = java.util.UUID.randomUUID().toString();
            String hashedPassword = DBHelper.hashPassword(password);

            Usuario usuario = new Usuario(uuid, "", email, hashedPassword);

            UsuarioService service = ApiClient.getClient().create(UsuarioService.class);
            Call<Void> call = service.registrarUsuario(
                    usuario,
                    SupabaseConfig.getAuthHeader(),
                    SupabaseConfig.getApiKey(),
                    SupabaseConfig.getContentType()
            );

            call.enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        boolean insertado = dbHelper.registrarUsuarioConUUID(usuario);
                        if (insertado) {
                            Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error al guardar en SQLite", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "Error: el usuario ya existe", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}