package com.example.gestipork_v2.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Conteo {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("id_explotacion")
    private String id_explotacion;

    @Expose
    @SerializedName("id_lote")
    private String id_lote;

    @Expose
    @SerializedName("nAnimales")
    private int nAnimales;

    @Expose
    @SerializedName("observaciones")
    private String observaciones;

    @Expose
    @SerializedName("fecha")
    private String fecha;

    @Expose
    @SerializedName("sincronizado")
    private int sincronizado;

    @Expose
    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    @Expose
    @SerializedName("eliminado")
    private int eliminado;

    @Expose
    @SerializedName("fecha_eliminado")
    private String fechaEliminado;

    public Conteo() {
    }

    public Conteo(String id, String id_explotacion, String id_lote, int nAnimales, String observaciones, String fecha,
                  int sincronizado, String fechaActualizacion, int eliminado, String fechaEliminado) {
        this.id = id;
        this.id_explotacion = id_explotacion;
        this.id_lote = id_lote;
        this.nAnimales = nAnimales;
        this.observaciones = observaciones;
        this.fecha = fecha;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
        this.eliminado = eliminado;
        this.fechaEliminado = fechaEliminado;
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_explotacion() {
        return id_explotacion;
    }

    public void setId_explotacion(String id_explotacion) {
        this.id_explotacion = id_explotacion;
    }

    public String getId_lote() {
        return id_lote;
    }

    public void setId_lote(String id_lote) {
        this.id_lote = id_lote;
    }

    public int getnAnimales() {
        return nAnimales;
    }

    public void setnAnimales(int nAnimales) {
        this.nAnimales = nAnimales;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public int getEliminado() {
        return eliminado;
    }

    public void setEliminado(int eliminado) {
        this.eliminado = eliminado;
    }

    public String getFechaEliminado() {
        return fechaEliminado;
    }

    public void setFechaEliminado(String fechaEliminado) {
        this.fechaEliminado = fechaEliminado;
    }
}
