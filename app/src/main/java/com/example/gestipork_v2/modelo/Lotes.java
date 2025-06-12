package com.example.gestipork_v2.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Lotes {
    @Expose
    private String id;
    @Expose
    @SerializedName("ndisponibles")
    private int nDisponibles;
    @Expose
    @SerializedName("niniciales")
    private int nIniciales;
    @Expose
    @SerializedName("cod_lote")
    private String cod_lote;
    @Expose
    @SerializedName("cod_explotacion")
    private String cod_explotacion;
    @Expose
    @SerializedName("cod_paridera")
    private String cod_paridera;
    @Expose
    @SerializedName("cod_cubricion")
    private String cod_cubricion;
    @Expose
    @SerializedName("cod_itaca")
    private String cod_itaca;
    @Expose
    private String raza;
    @Expose
    private String color;
    @Expose
    private int estado; // se envía como int, ya que Supabase lo guarda como integer
    @Expose
    @SerializedName("fecha_actualizacion")
    private String fecha_actualizacion;

    @Expose(serialize = false)
    private int sincronizado; // solo en SQLite, no se debe enviar

    public Lotes() {}

    // Constructor completo si lo necesitas

    public Lotes(String id, int nDisponibles, int nIniciales, String cod_lote, String cod_explotacion, String cod_paridera, String cod_cubricion, String cod_itaca, String raza, String color, int estado, String fecha_actualizacion, int sincronizado) {
        this.id = id;
        this.nDisponibles = nDisponibles;
        this.nIniciales = nIniciales;
        this.cod_lote = cod_lote;
        this.cod_explotacion = cod_explotacion;
        this.cod_paridera = cod_paridera;
        this.cod_cubricion = cod_cubricion;
        this.cod_itaca = cod_itaca;
        this.raza = raza;
        this.color = color;
        this.estado = estado;
        this.fecha_actualizacion = fecha_actualizacion;
        this.sincronizado = sincronizado;
    }


    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getnDisponibles() {
        return nDisponibles;
    }

    public void setnDisponibles(int nDisponibles) {
        this.nDisponibles = nDisponibles;
    }

    public int getnIniciales() {
        return nIniciales;
    }

    public void setnIniciales(int nIniciales) {
        this.nIniciales = nIniciales;
    }

    public String getCod_lote() {
        return cod_lote;
    }

    public void setCod_lote(String cod_lote) {
        this.cod_lote = cod_lote;
    }

    public String getCod_explotacion() {
        return cod_explotacion;
    }

    public void setCod_explotacion(String cod_explotacion) {
        this.cod_explotacion = cod_explotacion;
    }

    public String getCod_paridera() {
        return cod_paridera;
    }

    public void setCod_paridera(String cod_paridera) {
        this.cod_paridera = cod_paridera;
    }

    public String getCod_cubricion() {
        return cod_cubricion;
    }

    public void setCod_cubricion(String cod_cubricion) {
        this.cod_cubricion = cod_cubricion;
    }

    public String getCod_itaca() {
        return cod_itaca;
    }

    public void setCod_itaca(String cod_itaca) {
        this.cod_itaca = cod_itaca;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public String getFecha_actualizacion() {
        return fecha_actualizacion;
    }

    public void setFecha_actualizacion(String fecha_actualizacion) {
        this.fecha_actualizacion = fecha_actualizacion;
    }
}
