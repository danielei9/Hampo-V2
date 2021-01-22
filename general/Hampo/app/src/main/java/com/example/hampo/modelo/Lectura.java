package com.example.hampo.modelo;

public class Lectura {
    private String temperatura;
    private String  humedad;
    private String  iluminacion;
    private String  bebida;
    private String  comida;
    private String  actividad;
    // private double adiestramiento;
    private String  distancia;
    private long tiempo;

    public Lectura(String  temperatura, String  humedad, String  iluminacion, String  bebida, String  comida, String  actividad, String  distancia, long tiempo) {
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.iluminacion = iluminacion;
        this.bebida = bebida;
        this.comida = comida;
        this.actividad = actividad;
        this.distancia = distancia;
        this.tiempo = tiempo;
    }

    public String  getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String  temperatura) {
        this.temperatura = temperatura;
    }

    public String  getHumedad() {
        return humedad;
    }

    public void setHumedad(String  humedad) {
        this.humedad = humedad;
    }

    public String  getIluminacion() {
        return iluminacion;
    }

    public void setIluminacion(String  iluminacion) {
        this.iluminacion = iluminacion;
    }

    public String  getBebida() {
        return bebida;
    }

    public void setBebida(String  bebida) {
        this.bebida = bebida;
    }

    public String  getComida() {
        return comida;
    }

    public void setComida(String  comida) {
        this.comida = comida;
    }

    public String  getActividad() {
        return actividad;
    }

    public void setActividad(String  actividad) {
        this.actividad = actividad;
    }


    public String  getDistancia() {
        return distancia;
    }

    public void setDistancia(String  distancia) {
        this.distancia = distancia;
    }

    public long getTiempo() {
        return tiempo;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }
}
