package com.example.gestipork_v2.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Usuario {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    private String email;

    @Expose
    private String password;

    public Usuario(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
