package com.example.remindme;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

public class Recordatorio {
    private int idRecordatorio;
    private String recordatorio;
    private Date fecha;
    private int hora;
    private int minutos;

    public Recordatorio(int idRecordatorio, String recordatorio, Date fecha, int hora, int minutos) {
        this.idRecordatorio = idRecordatorio;
        this.recordatorio = recordatorio;
        this.fecha = fecha;
        this.hora = hora;
        this.minutos = minutos;
    }

    public int getIdRecordatorio() {
        return idRecordatorio;
    }

    public void setIdRecordatorio(int idRecordatorio) {
        this.idRecordatorio = idRecordatorio;
    }

    public String getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(String recordatorio) {
        this.recordatorio = recordatorio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinutos() {
        return minutos;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }
}