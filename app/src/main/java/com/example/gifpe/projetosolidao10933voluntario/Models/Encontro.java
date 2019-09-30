package com.example.gifpe.projetosolidao10933voluntario.Models;

public class Encontro {
    //region Atributos

    String data;
    String horas;
    String estado;
    String nifVoluntario;
    //endregion

    //region Propriedades

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHoras() {
        return horas;
    }

    public void setHoras(String horas) {
        this.horas = horas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNifVoluntario() {
        return nifVoluntario;
    }

    public void setNifVoluntario(String nifVoluntario) {
        this.nifVoluntario = nifVoluntario;
    }
    //endregion

    //region Construtores

    public Encontro(String d, String h, String Estado){
        this.data=d;
        this.horas=h;
        this.estado=Estado;
    }

    public Encontro(){}

    //endregion
}
