package com.example.gestipork_v2.modelo.tabs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Accion {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("id_lote")
    @Expose
    private String id_lote;

    @SerializedName("id_explotacion")
    @Expose
    private String id_explotacion;

    @SerializedName("tipo")
    @Expose
    private String tipo;

    @SerializedName("fecha")
    @Expose
    private String fecha;

    @SerializedName("cantidad")
    @Expose
    private int cantidad;

    @SerializedName("observaciones")
    @Expose
    private String observaciones;

    @SerializedName("sincronizado")
    @Expose
    private int sincronizado;

    @SerializedName("fecha_actualizacion")
    @Expose
    private String fechaActualizacion;

    @SerializedName("eliminado")
    @Expose
    private int eliminado;

    @SerializedName("fecha_eliminado")
    @Expose
    private String fechaEliminado;

    // Constructor completo
    public Accion(String id, String id_lote, String id_explotacion, String tipo, String fecha,
                  int cantidad, String observaciones, int sincronizado, String fechaActualizacion,
                  int eliminado, String fechaEliminado) {
        this.id = id;
        this.id_lote = id_lote;
        this.id_explotacion = id_explotacion;
        this.tipo = tipo;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
        this.eliminado = eliminado;
        this.fechaEliminado = fechaEliminado;
    }

    // Constructor reducido
    public Accion(String id, String tipo, String fecha, int cantidad, String observaciones) {
        this.id = id;
        this.tipo = tipo;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getId_lote() { return id_lote; }
    public void setId_lote(String id_lote) { this.id_lote = id_lote; }

    public String getId_explotacion() { return id_explotacion; }
    public void setId_explotacion(String id_explotacion) { this.id_explotacion = id_explotacion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getSincronizado() { return sincronizado; }
    public void setSincronizado(int sincronizado) { this.sincronizado = sincronizado; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public int getEliminado() { return eliminado; }
    public void setEliminado(int eliminado) { this.eliminado = eliminado; }

    public String getFechaEliminado() { return fechaEliminado; }
    public void setFechaEliminado(String fechaEliminado) { this.fechaEliminado = fechaEliminado; }
}
