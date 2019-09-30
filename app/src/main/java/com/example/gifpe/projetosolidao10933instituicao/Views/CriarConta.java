package com.example.gifpe.projetosolidao10933instituicao.Views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933instituicao.Controllers.Instituicoes;
import com.example.gifpe.projetosolidao10933instituicao.Models.Instituicao;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class CriarConta extends AppCompatActivity implements View.OnClickListener{

    private EditText etNomeEmpresa;
    private EditText etEmail;
    private EditText etPass;
    private EditText etMorada;
    private EditText etCodigoPostal;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);
        findViewById(R.id.btnCriarConta).setOnClickListener(this);
        findViewById(R.id.tvLogin).setOnClickListener(this);
        etNomeEmpresa=(EditText)findViewById(R.id.etNomeEmpresa);
        etEmail=(EditText)findViewById(R.id.etEmail);
        etPass=(EditText)findViewById(R.id.etPass);
        etMorada=(EditText)findViewById(R.id.etMorada);
        etCodigoPostal=(EditText)findViewById(R.id.etCodigoPostal);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCriarConta:
                String email=etEmail.getText().toString().trim();
                String senha=etPass.getText().toString().trim();
                String nome=etNomeEmpresa.getText().toString().trim();
                String morada=etMorada.getText().toString().trim();
                String codigoPostal=etCodigoPostal.getText().toString().trim();
                if (email.isEmpty()) {
                    etEmail.setError("O email é obrigatorio!");
                    etEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    etEmail.setError("Introduza um email válido!");
                    etEmail.requestFocus();
                    return;
                }
                if (senha.length()<5){
                    etPass.setError("É necessário mais de 5 letras!");
                    etPass.requestFocus();
                    return;
                }
                if (senha.isEmpty()) {
                    etPass.setError("A Password é obrigatória!");
                    etPass.requestFocus();
                    return;
                }
                if (nome.isEmpty()) {
                    etNomeEmpresa.setError("O nome da instiuição é obrigatório!");
                    etNomeEmpresa.requestFocus();
                    return;
                }
                if (morada.isEmpty()) {
                    etMorada.setError("A morada é obrigatória!");
                    etMorada.requestFocus();
                    return;
                }
                if (codigoPostal.isEmpty()) {
                    etCodigoPostal.setError("O Código-Postal é obrigatório!");
                    etCodigoPostal.requestFocus();
                    return;
                }
                Instituicao inst=new Instituicao(nome,email,senha,morada,codigoPostal);



                new Instituicoes(getApplication()).AddInstituicao(email,senha,inst);
                break;
            case R.id.tvLogin:
                Intent Login = new Intent(CriarConta.this, Login.class);
                CriarConta.this.startActivity(Login);
                break;
        }
    }
}
