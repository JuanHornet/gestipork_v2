package com.example.proyecto_gestipork.modelo;

public class Conteo {
    private int id;
    private String codExplotacion;
    private String codLote;
    private int nAnimales;
    private String observaciones;
    private String fecha;

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
}
