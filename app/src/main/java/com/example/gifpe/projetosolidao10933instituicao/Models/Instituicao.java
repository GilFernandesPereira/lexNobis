package com.example.gifpe.projetosolidao10933instituicao.Models;

public class Instituicao {
    //region Atributos

    String nome;
    String email;
    String password;
    String morada;
    String codigoPostal;


    //endregion

    //region Propriedades

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //endregion

    //region Construtores


    public Instituicao(String nome,String email,String password,String morada,String codigoPostal){
        this.nome=nome;
        this.email=email;
        this.password=password;
        this.morada=morada;
        this.codigoPostal=codigoPostal;
    }
    public Instituicao(){}

    //endregion

    //region Verificação se são iguais


    //endregion
}
