package com.example.gestipork_v2.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cubriciones {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("cod_cubricion")
    private String cod_cubricion;

    @Expose
    @SerializedName("id_explotacion")
    private String id_explotacion;

    @Expose
    @SerializedName("id_lote")
    private String id_lote;

    @Expose
    @SerializedName("nmadres") // <-- minúsculas
    private int nMadres;

    @Expose
    @SerializedName("npadres") // <-- minúsculas
    private int nPadres;

    @Expose
    @SerializedName("fechainiciocubricion") // <-- minúsculas
    private String fechaInicioCubricion;

    @Expose
    @SerializedName("fechafincubricion") // <-- minúsculas
    private String fechaFinCubricion;

    @Expose
    @SerializedName("fecha_actualizacion")
    private String fechaActualizacion;

    @Expose(deserialize = true, serialize = false) // Este campo es solo local
    private int sincronizado;


    public Cubriciones() {
    }

    public Cubriciones(String id, String cod_cubricion, String id_explotacion, String id_lote,
                       int nMadres, int nPadres, String fechaInicioCubricion, String fechaFinCubricion,
                       int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.cod_cubricion = cod_cubricion;
        this.id_explotacion = id_explotacion;
        this.id_lote = id_lote;
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

    public String getId_explotacion() { return id_explotacion; }

    public void setId_explotacion(String id_explotacion) { this.id_explotacion = id_explotacion; }

    public String getid_lote() { return id_lote; }

    public void setid_lote(String id_lote) { this.id_lote = id_lote; }

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
