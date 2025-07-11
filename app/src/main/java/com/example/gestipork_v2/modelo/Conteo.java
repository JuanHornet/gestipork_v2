package com.example.gestipork_v2.modelo;

public class Conteo {
    private int id;
    private String id_explotacion;
    private String id_lote;
    private int nAnimales;
    private String observaciones;
    private String fecha;
    private int sincronizado; // nuevo
    private String fechaActualizacion; // nuevo

    public Conteo(int id, String id_explotacion, String id_lote, int nAnimales, String observaciones, String fecha, int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.id_explotacion = id_explotacion;
        this.id_lote = id_lote;
        this.nAnimales = nAnimales;
        this.observaciones = observaciones;
        this.fecha = fecha;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Conteo() {

    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getId_explotacion() { return id_explotacion; }
    public void setId_explotacion(String id_explotacion) { this.id_explotacion = id_explotacion; }

    public String getId_lote() { return id_lote; }
    public void setId_lote(String id_lote) { this.id_lote = id_lote; }

    public int getnAnimales() { return nAnimales; }
    public void setnAnimales(int nAnimales) { this.nAnimales = nAnimales; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

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
