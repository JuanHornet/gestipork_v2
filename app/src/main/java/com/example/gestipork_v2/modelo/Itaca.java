package com.example.gestipork_v2.modelo;

import java.util.Date;

public class Itaca {

    private int id;
    private String cod_itaca,DCER;
    private int nAnimales, nMadres, nPadres;
    private Date fechaPNacimiento, fechaUNacimiento;
    private String raza;
    private String color;
    private int crotalesSolicitados;
    private String cod_lote, cod_explotacion;
    private int sincronizado; // nuevo
    private String fechaActualizacion; // nuevo

    public Itaca(int id, String cod_itaca, String DCER, int nAnimales, int nMadres, int nPadres, Date fechaPNacimiento, Date fechaUNacimiento, String raza, String color, int crotalesSolicitados, String cod_lote, String cod_explotacion, int sincronizado, String fechaActualizacion) {
        this.id = id;
        this.cod_itaca = cod_itaca;
        this.DCER = DCER;
        this.nAnimales = nAnimales;
        this.nMadres = nMadres;
        this.nPadres = nPadres;
        this.fechaPNacimiento = fechaPNacimiento;
        this.fechaUNacimiento = fechaUNacimiento;
        this.raza = raza;
        this.color = color;
        this.crotalesSolicitados = crotalesSolicitados;
        this.cod_lote = cod_lote;
        this.cod_explotacion = cod_explotacion;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Itaca() {}

    public String getDCER() {
        return DCER;
    }

    public void setDCER(String DCER) {
        this.DCER = DCER;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCod_itaca() {
        return cod_itaca;
    }

    public void setCod_itaca(String cod_itaca) {
        this.cod_itaca = cod_itaca;
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

    public Date getFechaPNacimiento() {
        return fechaPNacimiento;
    }

    public void setFechaPNacimiento(Date fechaPNacimiento) {
        this.fechaPNacimiento = fechaPNacimiento;
    }

    public Date getFechaUNacimiento() {
        return fechaUNacimiento;
    }

    public void setFechaUNacimiento(Date fechaUNacimiento) {
        this.fechaUNacimiento = fechaUNacimiento;
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
