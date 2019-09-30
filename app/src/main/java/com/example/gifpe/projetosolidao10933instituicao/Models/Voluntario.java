package com.example.gifpe.projetosolidao10933instituicao.Models;

import android.net.Uri;
public class Voluntario {

    //region Atributos
    String nome;
    String nrTelemovel;
    String morada;
    String sexo;
    String nif;
    String dataDeNascimento;
    String idInstituicao;
    String estado;
    Uri urlImage;
    //endregion

    //region Propriedades

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNrTelemovel() {
        return nrTelemovel;
    }

    public void setNrTelemovel(String nrTelemovel) {
        this.nrTelemovel = nrTelemovel;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getDataDeNascimento() {
        return dataDeNascimento;
    }

    public void setDataDeNascimento(String dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public String getIdInstituicao() {
        return idInstituicao;
    }

    public void setIdInstituicao(String idInstituicao) {
        this.idInstituicao = idInstituicao;
    }

    public Uri getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(Uri urlImage) {
        this.urlImage = urlImage;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    //endregion

    //region Construtores

    public Voluntario(String nome, String nrTele, String morada, String sexo, String nif, String dataNasc, String id){
        this.nome=nome;
        this.nrTelemovel=nrTele;
        this.morada=morada;
        this.sexo=sexo;
        this.nif=nif;
        this.dataDeNascimento=dataNasc;
        this.idInstituicao=id;
    }
    public Voluntario(){}

    //endregion
}
