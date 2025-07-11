package com.example.gestipork_v2.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Parideras {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("nacidosvivos")
    @Expose
    private int nacidosVivos;

    @SerializedName("nparidas")
    @Expose
    private int nParidas;

    @SerializedName("nvacias")
    @Expose
    private int nVacias;

    @SerializedName("cod_paridera")
    @Expose
    private String cod_paridera;

    @SerializedName("id_explotacion")
    @Expose
    private String id_explotacion;

    @SerializedName("id_lote")
    @Expose
    private String id_lote;

    @SerializedName("fechainicioparidera")
    @Expose
    private String fechaInicioParidera;

    @SerializedName("fechafinparidera")
    @Expose
    private String fechaFinParidera;

    @SerializedName("sincronizado")
    @Expose
    private int sincronizado;

    @SerializedName("fecha_actualizacion")
    @Expose
    private String fechaActualizacion;

    public Parideras() {}

    public Parideras(String id, int nacidosVivos, int nParidas, int nVacias, String cod_paridera, String id_explotacion,
                     String id_lote, String fechaInicioParidera, String fechaFinParidera,
                     int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.nacidosVivos = nacidosVivos;
        this.nParidas = nParidas;
        this.nVacias = nVacias;
        this.cod_paridera = cod_paridera;
        this.id_explotacion = id_explotacion;
        this.id_lote = id_lote;
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

    public String getId_explotacion() {
        return id_explotacion;
    }

    public void setId_explotacion(String id_explotacion) {
        this.id_explotacion = id_explotacion;
    }

    public String getid_lote() {
        return id_lote;
    }

    public void setid_lote(String id_lote) {
        this.id_lote = id_lote;
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
