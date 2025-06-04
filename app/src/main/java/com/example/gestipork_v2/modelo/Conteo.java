package com.example.gestipork_v2.modelo;

public class Conteo {
    private int id;
    private String codExplotacion;
    private String codLote;
    private int nAnimales;
    private String observaciones;
    private String fecha;
    private int sincronizado; // nuevo
    private String fechaActualizacion; // nuevo

    public Conteo(int id, String codExplotacion, String codLote, int nAnimales, String observaciones, String fecha, int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.codExplotacion = codExplotacion;
        this.codLote = codLote;
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

    public String getCodExplotacion() { return codExplotacion; }
    public void setCodExplotacion(String codExplotacion) { this.codExplotacion = codExplotacion; }

    public String getCodLote() { return codLote; }
    public void setCodLote(String codLote) { this.codLote = codLote; }

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
