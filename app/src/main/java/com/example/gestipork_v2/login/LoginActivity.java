package com.example.gestipork_v2.login;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestipork_v2.data.ConstantesPrefs;
import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.DashboardActivity;

import com.example.gestipork_v2.network.ApiClient;
import com.google.android.material.textfield.TextInputEditText;

import com.example.gestipork_v2.R;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText txtEmail, txtPassword;


    Button btnLogin;
    TextView txtRegistro;
    DBHelper dbHelper;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtEmail = findViewById(R.id.editTextUsername);
        txtPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegistro = findViewById(R.id.txtRegistro);

        dbHelper = new DBHelper(this);

        preferences = getSharedPreferences(ConstantesPrefs.PREFS_LOGIN, MODE_PRIVATE);


        // teclado enfoca directamente el campo Usuario
        txtEmail.requestFocus();

        // Que pulsar "Enter" en el teclado haga login
        txtPassword.setOnEditorActionListener((v, actionId, event) -> {
            btnLogin.performClick();
            return true;
        });

        // Si ya estaba logueado, entrar directamente
        boolean isLoggedIn = preferences.getBoolean(ConstantesPrefs.PREFS_IS_LOGGED_IN, false);
        if (isLoggedIn) {
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailField = findViewById(R.id.editTextUsername);
                EditText passwordField = findViewById(R.id.editTextPassword);

                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                String uuid = dbHelper.validarYObtenerUUID(email, password);

                if (uuid != null) {
                    loginExitoso(uuid, email);
                } else {
                    // No está en SQLite, intentamos login online
                    dbHelper.validarOnlineYGuardarSiExitoso(email, password, LoginActivity.this, resultado -> {
                        if (resultado != null) {
                            loginExitoso(resultado, email);
                        } else {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show());
                        }
                    });
                }


            }
        });



        txtRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Registro pulsado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish(); // cerrar el Login actual
            }
        });

    }
    private void loginExitoso(String uuid, String email) {
        SharedPreferences preferences = getSharedPreferences(ConstantesPrefs.PREFS_LOGIN, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(ConstantesPrefs.PREFS_IS_LOGGED_IN, true);
        editor.putString(ConstantesPrefs.PREFS_USER_EMAIL, email);
        editor.putString(ConstantesPrefs.PREFS_USER_UUID, uuid);

        // ⚠️ Obtener el token y API key reales desde la clase ApiClient
        String tokenReal = ApiClient.getToken();     // o el método correcto que estés usando
        String apiKeyReal = ApiClient.getApiKey();   // o el método correcto que estés usando

        editor.putString(ConstantesPrefs.PREFS_SUPABASE_TOKEN, tokenReal);
        editor.putString(ConstantesPrefs.PREFS_SUPABASE_APIKEY, apiKeyReal);

        editor.apply();

        dbHelper.importarExplotacionesSiNoExisten(uuid, this, () -> {
            new com.example.gestipork_v2.sync.SincronizadorLotes(LoginActivity.this).sincronizarLotes();

            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }


}
