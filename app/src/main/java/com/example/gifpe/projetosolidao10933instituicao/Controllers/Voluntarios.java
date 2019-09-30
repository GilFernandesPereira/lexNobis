package com.example.gifpe.projetosolidao10933instituicao.Controllers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.gifpe.projetosolidao10933instituicao.Models.Instituicao;
import com.example.gifpe.projetosolidao10933instituicao.Models.Participante;
import com.example.gifpe.projetosolidao10933instituicao.Models.Voluntario;
import com.example.gifpe.projetosolidao10933instituicao.Views.MenuPrincipal;
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

public class Voluntarios {

    FirebaseStorage storage;
    private String nomeInstituicao;
    private String userID;
    private StorageReference storageReference;
    DatabaseReference ref;
    private Context context;
    public Voluntarios(Context context){this.context = context;}

    public Boolean AddVoluntario(Voluntario voluntario, Uri file){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        try{
//            pb.setVisibility(View.VISIBLE);

            final FirebaseUser user= firebaseAuth.getCurrentUser();
            userID=user.getUid();
            ref = FirebaseDatabase.getInstance().getReference().child("Instituicoes").child(userID);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            teste(voluntario,database,file);

            Intent Menu = new Intent(context, MenuPrincipal.class);
            context.startActivity(Menu);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void teste(final Voluntario voluntario, final FirebaseDatabase fireBase, final Uri file){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Instituicao inst=new Instituicao();
                    inst.setNome(ds.child("/Informações da Empresa").getValue(Instituicao.class).getNome());
                    nomeInstituicao=inst.getNome();
                    Log.d("################3",nomeInstituicao);
                }
                DatabaseReference myRef = fireBase.getReference("Instituicoes"+"/"+userID+"/"+nomeInstituicao+"/Voluntários"+"/"+voluntario.getNif().toString());
                myRef.setValue(voluntario);
                uploadImage(file,voluntario.getNif().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void uploadImage(Uri filePath, String nif){
        if (filePath!= null){
            StorageReference ref= storageReference.child(nif);
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }

    public boolean AtualizaPerfil(final Voluntario voluntario, Uri uri, String nomeInst, String nif){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        try{
            final FirebaseUser user= firebaseAuth.getCurrentUser();
            userID=user.getUid();
            if(uri==null) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Instituicoes" + "/" + userID + "/" + nomeInst + "/Voluntários" + "/" + voluntario.getNif().toString());
                myRef.setValue(voluntario);
                return true;
            }else {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Instituicoes" + "/" + userID + "/" + nomeInst + "/Voluntários" + "/" + voluntario.getNif().toString());
                myRef.setValue(voluntario);
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
