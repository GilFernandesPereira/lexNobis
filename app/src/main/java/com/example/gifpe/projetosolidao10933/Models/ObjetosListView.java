package com.example.gifpe.projetosolidao10933.Models;

import android.widget.ImageView;

public class ObjetosListView {

    //region Atributos

    public String data;
    public String horas;
    public String estado;
    public String dataQueFoiAgendadaMissao;
    public String verificaFeedback;
    public int ivData;
    public int ivestado;
    public int ivHora;


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

    public int getIvData() {
        return ivData;
    }

    public void setIvData(int ivData) {
        this.ivData = ivData;
    }

    public int getIvestado() {
        return ivestado;
    }

    public void setIvestado(int ivestado) {
        this.ivestado = ivestado;
    }

    public int getIvHora() {
        return ivHora;
    }

    public void setIvHora(int ivHora) {
        this.ivHora = ivHora;
    }

    public String getDataQueFoiAgendadaMissao() {
        return dataQueFoiAgendadaMissao;
    }

    public void setDataQueFoiAgendadaMissao(String dataQueFoiAgendadaMissao) {
        this.dataQueFoiAgendadaMissao = dataQueFoiAgendadaMissao;
    }

    public String getVerificaFeedback() {
        return verificaFeedback;
    }

    public void setVerificaFeedback(String verificaFeedback) {
        this.verificaFeedback = verificaFeedback;
    }
    //endregion

    //region Construtores


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
