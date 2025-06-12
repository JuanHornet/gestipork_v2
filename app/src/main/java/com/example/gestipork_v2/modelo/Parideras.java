package com.example.gestipork_v2.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parideras {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("nacidosVivos")
    @Expose
    private int nacidosVivos;

    @SerializedName("nParidas")
    @Expose
    private int nParidas;

    @SerializedName("nVacias")
    @Expose
    private int nVacias;

    @SerializedName("cod_paridera")
    @Expose
    private String cod_paridera;

    @SerializedName("cod_explotacion")
    @Expose
    private String cod_explotacion;

    @SerializedName("cod_lote")
    @Expose
    private String cod_lote;

    @SerializedName("fechaInicioParidera")
    @Expose
    private String fechaInicioParidera;

    @SerializedName("fechaFinParidera")
    @Expose
    private String fechaFinParidera;

    @SerializedName("sincronizado")
    @Expose
    private int sincronizado;

    @SerializedName("fecha_actualizacion")
    @Expose
    private String fechaActualizacion;

    public Parideras() {}

    public Parideras(String id, int nacidosVivos, int nParidas, int nVacias, String cod_paridera, String cod_explotacion,
                     String cod_lote, String fechaInicioParidera, String fechaFinParidera,
                     int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.nacidosVivos = nacidosVivos;
        this.nParidas = nParidas;
        this.nVacias = nVacias;
        this.cod_paridera = cod_paridera;
        this.cod_explotacion = cod_explotacion;
        this.cod_lote = cod_lote;
        this.fechaInicioParidera = fechaInicioParidera;
        this.fechaFinParidera = fechaFinParidera;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNacidosVivos() {
        return nacidosVivos;
    }

    public void setNacidosVivos(int nacidosVivos) {
        this.nacidosVivos = nacidosVivos;
    }

    public int getnParidas() {
        return nParidas;
    }

    public void setnParidas(int nParidas) {
        this.nParidas = nParidas;
    }

    public int getnVacias() {
        return nVacias;
    }

    public void setnVacias(int nVacias) {
        this.nVacias = nVacias;
    }

    public String getCod_paridera() {
        return cod_paridera;
    }

    public void setCod_paridera(String cod_paridera) {
        this.cod_paridera = cod_paridera;
    }

    public String getCod_explotacion() {
        return cod_explotacion;
    }

    public void setCod_explotacion(String cod_explotacion) {
        this.cod_explotacion = cod_explotacion;
    }

    public String getCod_lote() {
        return cod_lote;
    }

    public void setCod_lote(String cod_lote) {
        this.cod_lote = cod_lote;
    }

    public String getFechaInicioParidera() {
        return fechaInicioParidera;
    }

    public void setFechaInicioParidera(String fechaInicioParidera) {
        this.fechaInicioParidera = fechaInicioParidera;
    }

    public String getFechaFinParidera() {
        return fechaFinParidera;
    }

    public void setFechaFinParidera(String fechaFinParidera) {
        this.fechaFinParidera = fechaFinParidera;
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
