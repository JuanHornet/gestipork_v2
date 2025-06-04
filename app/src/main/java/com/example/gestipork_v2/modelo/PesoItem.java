package com.example.gestipork_v2.modelo;

public class PesoItem {
    private int id;       // ID en la BD
    private int pesoKg;   // Peso del animal

    private int sincronizado; // nuevo
    private String fechaActualizacion; // nuevo

    public PesoItem(int id, int pesoKg) {
        this.id = id;
        this.pesoKg = pesoKg;
    }

    public PesoItem(int id, int pesoKg, int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.pesoKg = pesoKg;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
    }

    public int getId() {
        return id;
    }

    public int getPesoKg() {
        return pesoKg;
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
