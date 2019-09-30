package com.example.gifpe.projetosolidao10933instituicao.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933instituicao.R;
import com.example.gifpe.projetosolidao10933instituicao.Views.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class AlterarPasswordFragment extends Fragment implements View.OnClickListener {

    private EditText etEmail;
    private Button btnEnviar;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alterarpass, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etEmail=view.findViewById(R.id.etEmaiParaEnviar);
        view.findViewById(R.id.btnEnviar).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEnviar:
                FirebaseAuth.getInstance().sendPasswordResetEmail(etEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Email Enviado", Toast.LENGTH_SHORT).show();
                                    mAuth.getInstance().signOut();
                                    Intent sair = new Intent(getContext(), Login.class);
                                    sair.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    getContext().startActivity(sair);
                                }else{
                                    Toast.makeText(getContext(), "Email Inválido", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
        }
    }
}
