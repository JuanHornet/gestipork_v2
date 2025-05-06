package com.example.proyecto_gestipork.modelo.tabs;

public class Accion {
    private int id;
    private String tipo;
    private String fecha;
    private int cantidad;
    private String observaciones;

    public Accion(int id, String tipo, String fecha, int cantidad) {
        this.id = id;
        this.tipo = tipo;
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public Accion(int id, String tipo, String fecha, int cantidad, String observaciones) {
        this.id = id;
        this.tipo = tipo;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
}


