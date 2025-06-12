package com.example.gestipork_v2.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cubriciones {

    @Expose
    private String id;

    @Expose
    @SerializedName("cod_cubricion")
    private String cod_cubricion;

    @Expose
    @SerializedName("cod_explotacion")
    private String cod_explotacion;

    @Expose
    @SerializedName("cod_lote")
    private String cod_lote;

    @Expose
    @SerializedName("nMadres")
    private int nMadres;

    @Expose
    @SerializedName("nPadres")
    private int nPadres;

    @Expose
    @SerializedName("fechaInicioCubricion")
    private String fechaInicioCubricion;

    @Expose
    @SerializedName("fechaFinCubricion")
    private String fechaFinCubricion;

    @Expose
    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    @Expose(serialize = false) // Solo local, no se envía a Supabase
    private int sincronizado;

    public Cubriciones() {
    }

    public Cubriciones(String id, String cod_cubricion, String cod_explotacion, String cod_lote,
                       int nMadres, int nPadres, String fechaInicioCubricion, String fechaFinCubricion,
                       int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.cod_cubricion = cod_cubricion;
        this.cod_explotacion = cod_explotacion;
        this.cod_lote = cod_lote;
        this.nMadres = nMadres;
        this.nPadres = nPadres;
        this.fechaInicioCubricion = fechaInicioCubricion;
        this.fechaFinCubricion = fechaFinCubricion;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Getters y setters

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getCod_cubricion() { return cod_cubricion; }

    public void setCod_cubricion(String cod_cubricion) { this.cod_cubricion = cod_cubricion; }

    public String getCod_explotacion() { return cod_explotacion; }

    public void setCod_explotacion(String cod_explotacion) { this.cod_explotacion = cod_explotacion; }

    public String getCod_lote() { return cod_lote; }

    public void setCod_lote(String cod_lote) { this.cod_lote = cod_lote; }

    public int getnMadres() { return nMadres; }

    public void setnMadres(int nMadres) { this.nMadres = nMadres; }

    public int getnPadres() { return nPadres; }

    public void setnPadres(int nPadres) { this.nPadres = nPadres; }

    public String getFechaInicioCubricion() { return fechaInicioCubricion; }

    public void setFechaInicioCubricion(String fechaInicioCubricion) { this.fechaInicioCubricion = fechaInicioCubricion; }

    public String getFechaFinCubricion() { return fechaFinCubricion; }

    public void setFechaFinCubricion(String fechaFinCubricion) { this.fechaFinCubricion = fechaFinCubricion; }

    public int getSincronizado() { return sincronizado; }

    public void setSincronizado(int sincronizado) { this.sincronizado = sincronizado; }

    public String getFechaActualizacion() { return fechaActualizacion; }

    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
