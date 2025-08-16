package com.example.gestipork_v2.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SalidasExplotacion {

    @SerializedName("id")
    @Expose
    private String id; // UUID

    @SerializedName("id_lote")
    @Expose
    private String id_lote;

    @SerializedName("id_explotacion")
    @Expose
    private String id_explotacion;

    @SerializedName("tipo_salida")
    @Expose
    private String tipoSalida;

    @SerializedName("tipo_alimentacion")
    @Expose
    private String tipoAlimentacion;

    @SerializedName("fecha_salida")
    @Expose
    private String fechaSalida;  // en formato ISO: yyyy-MM-dd

    @SerializedName("n_animales")
    @Expose
    private int nAnimales;

    @SerializedName("observacion")
    @Expose
    private String observacion;

    @SerializedName("fecha_actualizacion")
    @Expose
    private String fechaActualizacion;

    @SerializedName("sincronizado")
    @Expose
    private int sincronizado;

    @SerializedName("eliminado")
    @Expose
    private int eliminado;

    @SerializedName("fecha_eliminado")
    @Expose
    private String fechaEliminado;

    public SalidasExplotacion() {
    }

    public SalidasExplotacion(String id, String id_lote, String id_explotacion, String tipoSalida, String tipoAlimentacion,
                              String fechaSalida, int nAnimales, String observacion,
                              int sincronizado, String fechaActualizacion,
                              int eliminado, String fechaEliminado) {
        this.id = id;
        this.id_lote = id_lote;
        this.id_explotacion = id_explotacion;
        this.tipoSalida = tipoSalida;
        this.tipoAlimentacion = tipoAlimentacion;
        this.fechaSalida = fechaSalida;
        this.nAnimales = nAnimales;
        this.observacion = observacion;
        this.sincronizado = sincronizado;
        this.fechaActualizacion = fechaActualizacion;
        this.eliminado = eliminado;
        this.fechaEliminado = fechaEliminado;
    }
    public SalidasExplotacion(
            String id,
            int nAnimales,
            String tipoSalida,
            String tipoAlimentacion,
            String id_lote,
            String id_explotacion,
            String observacion,
            java.util.Date fechaDate
    ) {
        this.id = id;
        this.nAnimales = nAnimales;
        this.tipoSalida = tipoSalida;
        this.tipoAlimentacion = tipoAlimentacion;
        this.id_lote = id_lote;
        this.id_explotacion = id_explotacion;
        this.observacion = observacion;

        // Convertir fecha Date a formato ISO yyyy-MM-dd
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        this.fechaSalida = (fechaDate != null) ? sdf.format(fechaDate) : null;

        this.sincronizado = 1; // Al cargar desde SQLite, ya está sincronizado
        this.fechaActualizacion = null;
        this.eliminado = 0;
        this.fechaEliminado = null;
    }


    // Getters y setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getId_lote() { return id_lote; }
    public void setId_lote(String id_lote) { this.id_lote = id_lote; }

    public String getId_explotacion() { return id_explotacion; }
    public void setId_explotacion(String id_explotacion) { this.id_explotacion = id_explotacion; }

    public String getTipoSalida() { return tipoSalida; }
    public void setTipoSalida(String tipoSalida) { this.tipoSalida = tipoSalida; }

    public String getTipoAlimentacion() { return tipoAlimentacion; }
    public void setTipoAlimentacion(String tipoAlimentacion) { this.tipoAlimentacion = tipoAlimentacion; }

    public String getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(String fechaSalida) { this.fechaSalida = fechaSalida; }

    public int getnAnimales() { return nAnimales; }
    public void setnAnimales(int nAnimales) { this.nAnimales = nAnimales; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public int getSincronizado() { return sincronizado; }
    public void setSincronizado(int sincronizado) { this.sincronizado = sincronizado; }

    public int getEliminado() { return eliminado; }
    public void setEliminado(int eliminado) { this.eliminado = eliminado; }

    public String getFechaEliminado() { return fechaEliminado; }
    public void setFechaEliminado(String fechaEliminado) { this.fechaEliminado = fechaEliminado; }
}
