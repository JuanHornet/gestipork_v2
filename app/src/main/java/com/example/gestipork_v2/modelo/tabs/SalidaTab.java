package com.example.gestipork_v2.modelo.tabs;

public class SalidaTab {
    private String motivo;
    private String fecha;
    private int cantidad;

    public SalidaTab(String motivo, String fecha, int cantidad) {
        this.motivo = motivo;
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public String getMotivo() { return motivo; }
    public String getFecha() { return fecha; }
    public int getCantidad() { return cantidad; }
}

