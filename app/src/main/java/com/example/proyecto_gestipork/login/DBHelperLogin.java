package com.example.proyecto_gestipork.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DBHelperLogin extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Usuarios.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelperLogin(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "password TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        onCreate(db);
    }

    // Método para registrar un usuario
    public boolean registrarUsuario(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Cifrar la contraseña antes de guardarla
        String passwordCifrada = cifrarSHA256(password);
        values.put("email", email);
        values.put("password", passwordCifrada);

        long result = db.insert("usuarios", null, values);
        db.close();
        return result != -1; // Si result es -1, significa que hubo un error
    }

    public boolean validarUsuario(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Cifrar la contraseña ingresada antes de compararla
        String passwordCifrada = cifrarSHA256(password);

        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE email = ? AND password = ?",
                new String[]{email, passwordCifrada});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return existe;
    }

    public String cifrarSHA256(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString(); // Retorna la contraseña encriptada en formato hexadecimal
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}


