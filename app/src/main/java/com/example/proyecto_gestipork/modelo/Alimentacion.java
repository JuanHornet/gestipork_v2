package com.example.proyecto_gestipork.modelo;

import java.util.Date;

public class Alimentacion {

    private int id, nAnimales;
    private String tipoAlimentacion, cod_lote, cod_explotacion;
    private Date fechaInicioAlimentacion;

    public Alimentacion() {
    }

    public Alimentacion(int id, int nAnimales, String tipoAlimentacion, String cod_lote, String cod_explotacion, Date fechaInicioAlimentacion) {
        this.id = id;
        this.nAnimales = nAnimales;
        this.tipoAlimentacion = tipoAlimentacion;
        this.cod_lote = cod_lote;
        this.cod_explotacion = cod_explotacion;
        this.fechaInicioAlimentacion = fechaInicioAlimentacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getnAnimales() {
        return nAnimales;
    }

    public void setnAnimales(int nAnimales) {
        this.nAnimales = nAnimales;
    }

    public String getTipoAlimentacion() {
        return tipoAlimentacion;
    }

    public void setTipoAlimentacion(String tipoAlimentacion) {
        this.tipoAlimentacion = tipoAlimentacion;
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

    public Date getFechaInicioAlimentacion() {
        return fechaInicioAlimentacion;
    }

    public void setFechaInicioAlimentacion(Date fechaInicioAlimentacion) {
        this.fechaInicioAlimentacion = fechaInicioAlimentacion;
    }
}
