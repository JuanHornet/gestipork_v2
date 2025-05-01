package com.example.proyecto_gestipork.modelo;

import java.util.Date;

public class Itaca {

    private int id;
    private String cod_itaca;
    private int nAnimales, nMadres, nPadres;
    private Date fechaPNacimiento, fechaUltNacimiento;
    private String raza;
    private String color;
    private int crotalesSolicitados;
    private String cod_lote;

    public Itaca() {}

    public Itaca(int id, String cod_itaca, int nAnimales, int nMadres, int nPadres,
                 Date fechaPNacimiento, Date fechaUltNacimiento, String raza,
                 String color, int crotalesSolicitados, String cod_lote) {
        this.id = id;
        this.cod_itaca = cod_itaca;
        this.nAnimales = nAnimales;
        this.nMadres = nMadres;
        this.nPadres = nPadres;
        this.fechaPNacimiento = fechaPNacimiento;
        this.fechaUltNacimiento = fechaUltNacimiento;
        this.raza = raza;
        this.color = color;
        this.crotalesSolicitados = crotalesSolicitados;
        this.cod_lote = cod_lote;
    }

    // Getters y Setters

    public Date getFechaPNacimiento() { return fechaPNacimiento; }
    public void setFechaPNacimiento(Date fechaPNacimiento) { this.fechaPNacimiento = fechaPNacimiento; }

    public Date getFechaUltNacimiento() { return fechaUltNacimiento; }
    public void setFechaUltNacimiento(Date fechaUltNacimiento) { this.fechaUltNacimiento = fechaUltNacimiento; }

    // Resto igual...
}
