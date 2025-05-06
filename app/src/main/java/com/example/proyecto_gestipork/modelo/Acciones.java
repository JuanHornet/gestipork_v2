package com.example.proyecto_gestipork.modelo;

import java.util.Date;

public class Acciones {

    private int id, nAanimales;
    private String tipoAccion, cod_lote, cod_explotacion, observacion;
    private Date fechaAccion;
    private boolean estado;

    public Acciones() {
    }

    public Acciones(int id, int nAanimales, String tipoAccion, String cod_lote, String cod_explotacion, String observacion, Date fechaAccion, boolean estado) {
        this.id = id;
        this.nAanimales = nAanimales;
        this.tipoAccion = tipoAccion;
        this.cod_lote = cod_lote;
        this.cod_explotacion = cod_explotacion;
        this.observacion = observacion;
        this.fechaAccion = fechaAccion;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getnAanimales() {
        return nAanimales;
    }

    public void setnAanimales(int nAanimales) {
        this.nAanimales = nAanimales;
    }

    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Date getFechaAccion() {
        return fechaAccion;
    }

    public void setFechaAccion(Date fechaAccion) {
        this.fechaAccion = fechaAccion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
