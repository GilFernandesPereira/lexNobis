package com.example.gifpe.projetosolidao10933voluntario.Models;

public class Animador {

    //region Atributos

    String  nome,dataDeNascimento;
    String morada;
    String idInstituicao;
    String sexo;
    String nrTelemovel, nif;

    //endregion

    //region Propriedades

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getdataDeNascimento() {
        return dataDeNascimento;
    }

    public void setdataDeNascimento(String dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getNrTelemovel() {
        return nrTelemovel;
    }

    public void setNrTelemovel(String nrTelemovel) {
        this.nrTelemovel = nrTelemovel;
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

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }


    //endregion

    //region Construtores

    public Animador(){

    }

    public Animador(String nome, String contacto, String nif, String morada, String idInstituicao, String sexo, String dataDeNascimento){
        this.nome=nome;
        this.dataDeNascimento=dataDeNascimento;
        this.nrTelemovel=contacto;
        this.morada=morada;
        this.nif=nif;
        this.sexo=sexo;
        this.idInstituicao=idInstituicao;
    }

    //endregion
}
