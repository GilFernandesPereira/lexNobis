package com.example.gifpe.projetosolidao10933voluntario.Controllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.gifpe.projetosolidao10933voluntario.Models.Animador;
import com.example.gifpe.projetosolidao10933voluntario.Views.Menu_Animador;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Animadores {private Context context;
    private Intent data;
    private FirebaseUser user;
    private DataSnapshot dataSnapshot;
    FirebaseStorage storage;
    private StorageReference storageReference;
    DatabaseReference ref;
    private String nomeInstituicao;

    public Animadores (Context context){this.context = context;}

    public boolean AtualizaPerfil(final Animador animador, Uri uri, String nomeInst, String nif ){
        try{
            if(uri==null) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Instituicoes" + "/" + animador.getidInstituicao().toString() + "/" + nomeInst + "/Voluntários" + "/" + animador.getNif().toString());
                myRef.setValue(animador);
                return true;
            }else {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Instituicoes" + "/" + animador.getidInstituicao().toString() + "/" + nomeInst + "/Voluntários" + "/" + animador.getNif().toString());
                myRef.setValue(animador);
                StorageReference ref = FirebaseStorage.getInstance().getReference().child(nif);
                ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                return true;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
