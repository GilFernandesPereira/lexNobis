package com.example.gifpe.projetosolidao10933.Models;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.sql.Time;
import java.util.Date;

public class Encontro {

    //region Atributos

    String data;
    String horas;
    String estado;
    int prioridade;

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

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }
    //endregion

    //region Construtores

    public Encontro(String d, String h, String Estado, int priori){
        this.data=d;
        this.horas=h;
        this.estado=Estado;
        this.prioridade=priori;
    }

    //endregion

    //region Verificação se são iguais

//    public static boolean operator (Encontro x, Encontro y){
//
//        if(((x.horas).equals(y.nome))&&((x.dataNascimentos).equals(y.dataNascimentos))&&(x.nif==y.nif)) {
//            return true;
//        }else{
//            return false;
//        }
//    }
    //endregion
}
