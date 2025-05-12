package com.example.proyecto_gestipork.modelo;

public class PesoItem {
    private int id;       // ID en la BD
    private int pesoKg;   // Peso del animal

    public PesoItem(int id, int pesoKg) {
        this.id = id;
        this.pesoKg = pesoKg;
    }

    public int getId() {
        return id;
    }

    public int getPesoKg() {
        return pesoKg;
    }
}
