package com.example.gestipork_v2.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Nota {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("id_lote")
    private String id_lote;

    @Expose
    @SerializedName("id_explotacion")
    private String id_explotacion;

    @Expose
    @SerializedName("fecha")
    private String fecha;

    @Expose
    @SerializedName("observacion")
    private String observacion;

    @Expose(deserialize = true, serialize = false)
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

    public Nota() {
    }

    public Nota(String id, String id_lote, String id_explotacion, String fecha, String observacion, int sincronizado, String fechaActualizacion, int eliminado, String fechaEliminado) {
        this.id = id;
        this.id_lote = id_lote;
        this.id_explotacion = id_explotacion;
        this.fecha = fecha;
        this.observacion = observacion;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
        this.eliminado = eliminado;
        this.fechaEliminado = fechaEliminado;
    }

    public Nota(String id, String id_lote, String id_explotacion, String fecha, String observacion) {
        this.id = id;
        this.id_lote = id_lote;
        this.id_explotacion = id_explotacion;
        this.fecha = fecha;
        this.observacion = observacion;
    }

    public Nota(String id, String id_lote, String id_explotacion, String fecha, String observacion, int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.id_lote = id_lote;
        this.id_explotacion = id_explotacion;
        this.fecha = fecha;
        this.observacion = observacion;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_lote() {
        return id_lote;
    }

    public void setId_lote(String id_lote) {
        this.id_lote = id_lote;
    }

    public String getId_explotacion() {
        return id_explotacion;
    }

    public void setId_explotacion(String id_explotacion) {
        this.id_explotacion = id_explotacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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
