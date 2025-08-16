package com.example.gestipork_v2.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EliminacionPendiente {

    @Expose
    @SerializedName("id")
    private String id;

    @Expose
    @SerializedName("id_registro")
    private String idRegistro;

    @Expose
    @SerializedName("tabla")
    private String tabla;

    @Expose
    @SerializedName("fecha_eliminado")
    private String fechaEliminado;

    @Expose
    private int sincronizado;

    public EliminacionPendiente() {
    }

    public EliminacionPendiente(String id, String idRegistro, String tabla, String fechaEliminado, int sincronizado) {
        this.id = id;
        this.idRegistro = idRegistro;
        this.tabla = tabla;
        this.fechaEliminado = fechaEliminado;
        this.sincronizado = sincronizado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(String idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getTabla() {
        return tabla;
    }

    public void setTabla(String tabla) {
        this.tabla = tabla;
    }

    public String getFechaEliminado() {
        return fechaEliminado;
    }

    public void setFechaEliminado(String fechaEliminado) {
        this.fechaEliminado = fechaEliminado;
    }

    public int getSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(int sincronizado) {
        this.sincronizado = sincronizado;
    }
}
