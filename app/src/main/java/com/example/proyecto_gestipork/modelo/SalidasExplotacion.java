package com.example.proyecto_gestipork.modelo;

import java.util.Date;

public class SalidasExplotacion {

    private int id, nAnimales;
    private String tipoSalida, tipoAlimentacion, cod_lote, cod_explotacion, observacion;
    private Date fechaSalida;

    public SalidasExplotacion() {
    }

    public SalidasExplotacion(int id, int nAnimales, String tipoSalida, String tipoAlimentacion, String cod_lote, String cod_explotacion, String observacion, Date fechaSalida) {
        this.id = id;
        this.nAnimales = nAnimales;
        this.tipoSalida = tipoSalida;
        this.tipoAlimentacion = tipoAlimentacion;
        this.cod_lote = cod_lote;
        this.cod_explotacion = cod_explotacion;
        this.observacion = observacion;
        this.fechaSalida = fechaSalida;
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

    public String getTipoSalida() {
        return tipoSalida;
    }

    public void setTipoSalida(String tipoSalida) {
        this.tipoSalida = tipoSalida;
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

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
}
