package com.example.proyecto_gestipork.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.proyecto_gestipork.R;

public class RegisterActivity extends AppCompatActivity {

    EditText txtEmail, txtPassword;
    Button btnRegister;
    DBHelperLogin dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        dbHelper = new DBHelperLogin(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean registrado = dbHelper.registrarUsuario(email, password);
                if (registrado) {
                    Toast.makeText(RegisterActivity.this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la actividad y vuelve al login
                } else {
                    Toast.makeText(RegisterActivity.this, "Error: el usuario ya existe", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}