package com.example.gestipork_v2.modelo.tabs;

public class Accion {
    private String id; // UUID
    private String id_lote;
    private String id_explotacion;
    private String tipo;
    private String fecha;
    private int cantidad;
    private String observaciones;
    private int sincronizado;
    private String fechaActualizacion;

    public Accion(String id, String id_lote, String id_explotacion, String tipo, String fecha, int cantidad, String observaciones, int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.id_lote = id_lote;
        this.id_explotacion = id_explotacion;
        this.tipo = tipo;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
    }
    public Accion(String id, String tipo, String fecha, int cantidad, String observaciones) {
        this.id = id;
        this.tipo = tipo;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
    }


    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
