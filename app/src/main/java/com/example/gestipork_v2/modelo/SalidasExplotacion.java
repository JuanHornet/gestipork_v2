package com.example.gestipork_v2.modelo;

import java.util.Date;

public class SalidasExplotacion {

    private int id, nAnimales;
    private String tipoSalida, tipoAlimentacion, id_lote, id_explotacion, observacion;
    private Date fechaSalida;

    public SalidasExplotacion() {
    }

    public SalidasExplotacion(int id, int nAnimales, String tipoSalida, String tipoAlimentacion, String id_lote, String id_explotacion, String observacion, Date fechaSalida) {
        this.id = id;
        this.nAnimales = nAnimales;
        this.tipoSalida = tipoSalida;
        this.tipoAlimentacion = tipoAlimentacion;
        this.id_lote = id_lote;
        this.id_explotacion = id_explotacion;
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

    public String getid_lote() {
        return id_lote;
    }

    public void setid_lote(String id_lote) {
        this.id_lote = id_lote;
    }

    public String getId_explotacion() {
        return id_explotacion;
    }

    public void setId_explotacion(String id_explotacion) {
        this.id_explotacion = id_explotacion;
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
