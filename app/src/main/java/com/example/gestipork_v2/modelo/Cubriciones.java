package com.example.gestipork_v2.modelo;

import java.util.Date;

public class Cubriciones {

    private int id;
    private String cod_cubricion, cod_explotacion, cod_lote;
    private int nMadres, nPadres;
    private Date fechaInicioCubricion, fechaFinCubricion;

    public Cubriciones() {
    }

    public Cubriciones(int id, String cod_cubricion, String cod_explotacion, String cod_lote, int nMadres, int nPadres, Date fechaInicioCubricion, Date fechaFinCubricion) {
        this.id = id;
        this.cod_cubricion = cod_cubricion;
        this.cod_explotacion = cod_explotacion;
        this.cod_lote = cod_lote;
        this.nMadres = nMadres;
        this.nPadres = nPadres;
        this.fechaInicioCubricion = fechaInicioCubricion;
        this.fechaFinCubricion = fechaFinCubricion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCod_cubricion() {
        return cod_cubricion;
    }

    public void setCod_cubricion(String cod_cubricion) {
        this.cod_cubricion = cod_cubricion;
    }

    public String getCod_explotacion() {
        return cod_explotacion;
    }

    public void setCod_explotacion(String cod_explotacion) {
        this.cod_explotacion = cod_explotacion;
    }

    public String getCod_lote() {
        return cod_lote;
    }

    public void setCod_lote(String cod_lote) {
        this.cod_lote = cod_lote;
    }

    public int getnMadres() {
        return nMadres;
    }

    public void setnMadres(int nMadres) {
        this.nMadres = nMadres;
    }

    public int getnPadres() {
        return nPadres;
    }

    public void setnPadres(int nPadres) {
        this.nPadres = nPadres;
    }

    public Date getFechaInicioCubricion() {
        return fechaInicioCubricion;
    }

    public void setFechaInicioCubricion(Date fechaInicioCubricion) {
        this.fechaInicioCubricion = fechaInicioCubricion;
    }

    public Date getFechaFinCubricion() {
        return fechaFinCubricion;
    }

    public void setFechaFinCubricion(Date fechaFinCubricion) {
        this.fechaFinCubricion = fechaFinCubricion;
    }
}
