package com.example.gestipork_v2.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Itaca {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("cod_itaca")
    @Expose
    private String cod_itaca;

    @SerializedName("DCER")
    @Expose
    private String DCER;

    @SerializedName("nAnimales")
    @Expose
    private int nAnimales;

    @SerializedName("nMadres")
    @Expose
    private int nMadres;

    @SerializedName("nPadres")
    @Expose
    private int nPadres;

    @SerializedName("fechaPNacimiento")
    @Expose
    private String fechaPNacimiento;


    @SerializedName("fechaUltNacimiento")
    @Expose
    private String fechaUltNacimiento;

    @SerializedName("raza")
    @Expose
    private String raza;

    @SerializedName("color")
    @Expose
    private String color;

    @SerializedName("crotalesSolicitados")
    @Expose
    private int crotalesSolicitados;

    @SerializedName("cod_lote")
    @Expose
    private String cod_lote;

    @SerializedName("cod_explotacion")
    @Expose
    private String cod_explotacion;

    @SerializedName("sincronizado")
    @Expose
    private int sincronizado;

    @SerializedName("fechaActualizacion")
    @Expose
    private String fechaActualizacion;

    public Itaca() {}

    public Itaca(String id, String cod_itaca, String DCER, int nAnimales, int nMadres, int nPadres,
                 String fechaPNacimiento, String fechaUltNacimiento, String raza, String color,
                 int crotalesSolicitados, String cod_lote, String cod_explotacion,
                 int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.cod_itaca = cod_itaca;
        this.DCER = DCER;
        this.nAnimales = nAnimales;
        this.nMadres = nMadres;
        this.nPadres = nPadres;
        this.fechaPNacimiento = fechaPNacimiento;
        this.fechaUltNacimiento = fechaUltNacimiento;
        this.raza = raza;
        this.color = color;
        this.crotalesSolicitados = crotalesSolicitados;
        this.cod_lote = cod_lote;
        this.cod_explotacion = cod_explotacion;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCod_itaca() {
        return cod_itaca;
    }

    public void setCod_itaca(String cod_itaca) {
        this.cod_itaca = cod_itaca;
    }

    public String getDCER() {
        return DCER;
    }

    public void setDCER(String DCER) {
        this.DCER = DCER;
    }

    public int getnAnimales() {
        return nAnimales;
    }

    public void setnAnimales(int nAnimales) {
        this.nAnimales = nAnimales;
    }

    public int getnMadres() {
        return nMadres;
    }

    public void setnMadres(int nMadres) {
        this.nMadres = nMadres;
    }

    public int getnPadres() {
        return nPadres;
    }

    public void setnPadres(int nPadres) {
        this.nPadres = nPadres;
    }

    public String getFechaPNacimiento() {
        return fechaPNacimiento;
    }

    public void setFechaPNacimiento(String fechaPNacimiento) {
        this.fechaPNacimiento = fechaPNacimiento;
    }

    public String getFechaUltNacimiento() {
        return fechaUltNacimiento;
    }

    public void setFechaUltNacimiento(String fechaUNacimiento) {
        this.fechaUltNacimiento = fechaUNacimiento;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCrotalesSolicitados() {
        return crotalesSolicitados;
    }

    public void setCrotalesSolicitados(int crotalesSolicitados) {
        this.crotalesSolicitados = crotalesSolicitados;
    }

    public String getCod_lote() {
        return cod_lote;
    }

    public void setCod_lote(String cod_lote) {
        this.cod_lote = cod_lote;
    }

    public String getCod_explotacion() {
        return cod_explotacion;
    }

    public void setCod_explotacion(String cod_explotacion) {
        this.cod_explotacion = cod_explotacion;
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
