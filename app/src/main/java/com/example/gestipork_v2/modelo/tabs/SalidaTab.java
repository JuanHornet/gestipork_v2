package com.example.gestipork_v2.modelo.tabs;

public class SalidaTab {
    private String motivo;
    private String fecha;
    private int cantidad;
    private int sincronizado;
    private String fechaActualizacion;

    public SalidaTab(String motivo, String fecha, int cantidad) {
        this.motivo = motivo;
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public SalidaTab(String motivo, String fecha, int cantidad, int sincronizado, String fechaActualizacion) {
        this.motivo = motivo;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getMotivo() { return motivo; }
    public String getFecha() { return fecha; }
    public int getCantidad() { return cantidad; }

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

