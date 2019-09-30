package com.example.gifpe.projetosolidao10933instituicao.Controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933instituicao.Models.Instituicao;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.example.gifpe.projetosolidao10933instituicao.Views.CriarConta;
import com.example.gifpe.projetosolidao10933instituicao.Views.Login;
import com.example.gifpe.projetosolidao10933instituicao.Views.MenuPrincipal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Instituicoes {
    private Context context;
    ProgressBar progressBar;

    public Instituicoes(Context context) {
        this.context = context;
    }

    public void AddInstituicao(String email, String pass, final Instituicao instituicao) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        try {
            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                final String userID;
                                final FirebaseUser user = firebaseAuth.getCurrentUser();
                                userID = user.getUid();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("Instituicoes" + "/" + userID + "/" + instituicao.getNome() + "/Informações da Empresa");//instituicao.getNome()+"/"+userID+"/Informações da Empresa"
                                myRef.setValue(instituicao);
                                Log.d("################3", "Deu certo");
                                Toast.makeText(context, "Criado com sucesso!", Toast.LENGTH_SHORT).show();
                                Intent Menu = new Intent(context, MenuPrincipal.class);
                                context.startActivity(Menu);

                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(context, "Já está registado!", Toast.LENGTH_SHORT).show();
                                    Log.d("################3", "Já existe");

                                } else {
                                    Log.d("################3", "Erro");
                                    Toast.makeText(context, "ERRO!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean AtualizaPerfil(String email, String senha, String nomeInstituicao, final Instituicao instituicao) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        try {
            final String userID;
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            userID = user.getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Instituicoes" + "/" + userID + "/" + instituicao.getNome() + "/Informações da Empresa");//Tira o ("Instituicoes"+"/")
            myRef.setValue(instituicao);
            update(email);
            //updateSenha(senha);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void update(String email) {


        FirebaseUser userr = FirebaseAuth.getInstance().getCurrentUser();
        userr.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("", "User email address updated.");

                        }
                    }
                });
        //----------------------------------------------------------\\

    }
}
