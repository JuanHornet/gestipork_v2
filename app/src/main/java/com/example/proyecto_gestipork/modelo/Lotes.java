package com.example.proyecto_gestipork.modelo;

public class Lotes {

    private int id, nDisponibles, nIniciales;
    private String cod_lote, cod_explotacion, cod_paridera, cod_cubricion, cod_itaca, raza, color;
    private boolean estado;

    public Lotes() {
    }

    public Lotes(int id, int nDisponibles, int nIniciales, String cod_lote, String cod_explotacion, String cod_paridera, String cod_cubricion, String cod_itaca, String raza, String color, boolean estado) {
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
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}