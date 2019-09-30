package com.example.gifpe.projetosolidao10933voluntario.Models;

public class Participante {

    //region Atributos

    String  nome,dataDeNascimento;
    String morada;
    String idInstituicao;
    String doenca;
    String sexo;
    String nrTelemovel;
    String nif;

    String codigoPostal;

    //endregion

    //region Propriedades

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimentos() {
        return dataDeNascimento;
    }

    public void setDataNascimentos(String dataDeNascimento) {
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

    public String getDoenca() {
        return doenca;
    }

    public void setDoenca(String doenca) {
        this.doenca = doenca;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
    //endregion

    //region Construtores

    public Participante(){

    }

    public Participante(String nome, String contacto, String nif, String morada, String idInstituicao, String sexo, String dataNascimentos){
        this.nome=nome;
        this.dataDeNascimento=dataNascimentos;
        this.nrTelemovel=contacto;
        this.morada=morada;
        this.nif=nif;
        this.sexo=sexo;
        this.idInstituicao=idInstituicao;
    }

    //endregion
}
