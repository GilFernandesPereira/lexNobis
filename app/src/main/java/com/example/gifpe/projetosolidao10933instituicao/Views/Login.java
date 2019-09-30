package com.example.gifpe.projetosolidao10933instituicao.Views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText etUtilizador;
    private EditText etPass;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Gil Pereira/ Instituiçao");

        progressBar=(ProgressBar) findViewById(R.id.progressbar);
        findViewById(R.id.tvCriarConta).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnCria).setOnClickListener(this);
        etUtilizador=(EditText)findViewById(R.id.etUtilizador);
        etPass=(EditText)findViewById(R.id.etSenha);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                String email = etUtilizador.getText().toString().trim();
                String pass = etPass.getText().toString().trim();
                if (email.isEmpty()) {
                    etUtilizador.setError("O email é obrigatorio!");
                    etUtilizador.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    etUtilizador.setError("Introduza um email válido!");
                    etUtilizador.requestFocus();
                    return;
                }
                if (pass.isEmpty()) {
                    etPass.setError("A Password é obrigatória!");
                    etPass.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){
                            Intent intent= new Intent(Login.this,MenuPrincipal.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.btnCria:
                Intent criarConta = new Intent(Login.this, CriarConta.class);
                Login.this.startActivity(criarConta);
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), MenuPrincipal.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
