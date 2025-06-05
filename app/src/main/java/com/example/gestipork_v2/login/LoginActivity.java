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

import com.example.gestipork_v2.data.DBHelper;
import com.example.gestipork_v2.modelo.DashboardActivity;

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

        preferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        // teclado enfoca directamente el campo Usuario
        txtEmail.requestFocus();

        // Que pulsar "Enter" en el teclado haga login
        txtPassword.setOnEditorActionListener((v, actionId, event) -> {
            btnLogin.performClick();
            return true;
        });

        // Si ya estaba logueado, entrar directamente
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
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
                    Toast.makeText(LoginActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("userEmail", email);
                    editor.putString("userUUID", uuid);  // 👈 Guardamos el UUID
                    editor.apply();

                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
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
}
