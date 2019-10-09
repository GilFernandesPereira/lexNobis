package com.example.gifpe.projetosolidao10933voluntario.Views;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933voluntario.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Ativar_Conta_Codigo extends AppCompatActivity implements View.OnClickListener {

    private String verificaoID, idDainstituicao;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private EditText etCodigoAtivacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ativar__conta__codigo);
        findViewById(R.id.btnAtivarConta).setOnClickListener(this);
        etCodigoAtivacao = findViewById(R.id.etCodigoAtivar);
        mAuth = FirebaseAuth.getInstance();
        String nrTelemovel = getIntent().getStringExtra("nrTelemovel");
        idDainstituicao = getIntent().getStringExtra("idDaInstituicao");
        Log.d("=================", idDainstituicao);
        enviarVerificacao(nrTelemovel);
    }

    private void enviarVerificacao(String nrTelemovel) {
//        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                nrTelemovel,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificaoID = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                etCodigoAtivacao.setText(code);
                verificaCodigo(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verificaCodigo(String codigo) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificaoID, codigo);
        signWithcredential(credential);
    }

    private void signWithcredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Deu Certo", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Menu_Animador.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("idDaInstituicao", idDainstituicao);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Deu errado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAtivarConta:

                String codigo = etCodigoAtivacao.getText().toString();
                if (codigo.isEmpty() || codigo.length() < 6) {
                    etCodigoAtivacao.setError("É necessário o codigo...");
                    etCodigoAtivacao.requestFocus();
                    return;
                }
                verificaCodigo(codigo);
                break;
        }
    }
    //PAra não voltar a pagina outra vez
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), Menu_Animador.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
