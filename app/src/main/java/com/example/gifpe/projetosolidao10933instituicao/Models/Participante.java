package com.example.gifpe.projetosolidao10933instituicao.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class Participante {
    //region Atributos

    String nome;
    String contacto;
    String morada;
    String codigoPostal;
    String sexo;
    String peso;
    String estadoCivil;
    String nif;
    String dataDeNascimento;
    String doenca;
    String idInstituicao;
    Uri urlImage;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    Bitmap bitmap;
    //endregion

    //region Propriedades


    public Uri getUrlImage() {
        return urlImage;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

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

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
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

    public String getDoenca() {
        return doenca;
    }

    public void setDoenca(String doenca) {
        this.doenca = doenca;
    }

    public String idInstituicao() {
        return idInstituicao;
    }

    public void idInstituicao(String IDdaInstituicao) {
        this.idInstituicao = IDdaInstituicao;
    }
    //endregion

    //region Construtores

    public void setUrlImage() {
        StorageReference storage = FirebaseStorage.getInstance().getReference().child(nif);
        String uriString = new String();
        uriString= storage.toString();
//        uriString = uriString.concat(storage.getPath());
        urlImage = Uri.parse(uriString+".jpeg");

        storage= FirebaseStorage.getInstance().getReference(nif +".jpeg");
        Log.d("ImageURL", storage.toString());
    }

    public Participante(String nome, String nrTele, String morada, String codigoPost, String sexo, String peso, String estadoCivil, String nif, String dataNasc, String doenca, String id){
        this.nome=nome;
        this.contacto=nrTele;
        this.morada=morada;
        this.codigoPostal=codigoPost;
        this.sexo=sexo;
        this.peso=peso;
        this.estadoCivil=estadoCivil;
        this.nif=nif;
        this.dataDeNascimento=dataNasc;
        this.doenca=doenca;
        this.idInstituicao=id;
    }

    public Participante(){}

    //endregion

}


