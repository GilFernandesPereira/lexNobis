package com.example.gifpe.projetosolidao10933.Models;

import android.support.constraint.solver.widgets.Snapshot;

public class Participante{

    //region Atributos

    String  nome,dataNascimentos;
    String morada;
    String idInstituicao;
    String contacto, nif;

    //endregion

    //region Propriedades

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimentos() {
        return dataNascimentos;
    }

    public void setDataNascimentos(String dataNascimentos) {
        this.dataNascimentos = dataNascimentos;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getidInstituicao() {
        return idInstituicao;
    }

    public void setidInstituicao(String idInstituicao) {
        this.idInstituicao = idInstituicao;
    }

    //endregion

    //region Construtores

    public Participante(){

    }

    //endregion

    //region Verificação se são iguais

    public static boolean operator (Participante x, Participante y){

        if(((x.nome).equals(y.nome))&&((x.dataNascimentos).equals(y.dataNascimentos))&&(x.nif==y.nif)) {
            return true;
        }else{
            return false;
        }
    }
    //endregion
}
