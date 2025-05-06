package com.example.proyecto_gestipork.modelo.tabs;

public class Accion {
    private int id;
    private String tipo;
    private String fecha;
    private int cantidad;

    public Accion(int id, String tipo, String fecha, int cantidad) {
        this.id = id;
        this.tipo = tipo;
        this.fecha = fecha;
        this.cantidad = cantidad;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public int getCantidad() {
        return cantidad;
    }
}


