package com.example.gestipork_v2.modelo;


import com.google.gson.annotations.Expose;

public class Explotacion {

    @Expose
    private String id; // UUID

    @Expose
    private String nombre;

    @Expose
    private String id_usuario; // UUID del usuario

    @Expose
    private String cod_explotacion;

    public Explotacion(String id, String nombre, String iduser, String cod_explotacion) {
        this.id = id;
        this.nombre = nombre;
        this.id_usuario = iduser;
        this.cod_explotacion = cod_explotacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIduser() {
        return id_usuario;
    }

    public void setIduser(String iduser) {
        this.id_usuario = iduser;
    }

    public String getCod_explotacion() {
        return cod_explotacion;
    }

    public void setCod_explotacion(String cod_explotacion) {
        this.cod_explotacion = cod_explotacion;
    }

    // Getters y setters si los necesitas
}
