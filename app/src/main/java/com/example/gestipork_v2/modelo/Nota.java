package com.example.gestipork_v2.modelo;

public class Nota {
    private int id;
    private String codLote;
    private String codExplotacion;
    private String fecha;
    private String observacion;

    public Nota(int id, String codLote, String codExplotacion, String fecha, String observacion) {
        this.id = id;
        this.codLote = codLote;
        this.codExplotacion = codExplotacion;
        this.fecha = fecha;
        this.observacion = observacion;
    }

    public int getId() {
        return id;
    }

    public String getCodLote() {
        return codLote;
    }

    public String getCodExplotacion() {
        return codExplotacion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCodLote(String codLote) {
        this.codLote = codLote;
    }

    public void setCodExplotacion(String codExplotacion) {
        this.codExplotacion = codExplotacion;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
