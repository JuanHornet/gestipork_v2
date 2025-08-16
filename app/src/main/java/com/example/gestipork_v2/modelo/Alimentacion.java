package com.example.gestipork_v2.modelo;

import java.util.Date;

public class Alimentacion {

    private int nAnimales;
    private String tipoAlimentacion, id_lote, id_explotacion, id;
    private Date fechaInicioAlimentacion;
    private int sincronizado;
    private String fechaActualizacion;

    public Alimentacion() {
    }

    public Alimentacion(String id, int nAnimales, String tipoAlimentacion, String id_lote, String id_explotacion, Date fechaInicioAlimentacion) {
        this.id = id;
        this.nAnimales = nAnimales;
        this.tipoAlimentacion = tipoAlimentacion;
        this.id_lote = id_lote;
        this.id_explotacion = id_explotacion;
        this.fechaInicioAlimentacion = fechaInicioAlimentacion;
    }

    public Alimentacion(String id, int nAnimales, String tipoAlimentacion, String id_lote, String id_explotacion, Date fechaInicioAlimentacion, int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.nAnimales = nAnimales;
        this.tipoAlimentacion = tipoAlimentacion;
        this.id_lote = id_lote;
        this.id_explotacion = id_explotacion;
        this.fechaInicioAlimentacion = fechaInicioAlimentacion;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
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

    public String getId_lote() {
        return id_lote;
    }

    public void setId_lote(String id_lote) {
        this.id_lote = id_lote;
    }

    public String getId_explotacion() {
        return id_explotacion;
    }

    public void setId_explotacion(String id_explotacion) {
        this.id_explotacion = id_explotacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFechaInicioAlimentacion() {
        return fechaInicioAlimentacion;
    }

    public void setFechaInicioAlimentacion(Date fechaInicioAlimentacion) {
        this.fechaInicioAlimentacion = fechaInicioAlimentacion;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }

    public String getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
