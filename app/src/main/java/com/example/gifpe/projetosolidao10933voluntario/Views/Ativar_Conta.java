package com.example.gifpe.projetosolidao10933voluntario.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933voluntario.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Ativar_Conta extends AppCompatActivity implements View.OnClickListener {

    private String number, numeroVerifica;
    private EditText etNrTelemovel;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ativar__conta);

        myRef= FirebaseDatabase.getInstance().getReference().child("Instituicoes");
        findViewById(R.id.btnContinuarParaCodigo).setOnClickListener(this);
        etNrTelemovel= findViewById(R.id.etNrTelemovel);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnContinuarParaCodigo:
                number=etNrTelemovel.getText().toString().trim();
                if(number.isEmpty()){
                    etNrTelemovel.setError("O numero de telemóvel é obrigatório!");
                    etNrTelemovel.requestFocus();
                    return;
                }
                if (!Patterns.PHONE.matcher(number).matches()){
                    etNrTelemovel.setError("Introduza um nrº de telemóvel válido!");
                    etNrTelemovel.requestFocus();
                    return;
                }
                if(number.length()!=9){
                    etNrTelemovel.setError("São necessários 9 numeros!");
                    etNrTelemovel.requestFocus();
                    return;
                }
                myRef.addValueEventListener(new ValueEventListener() {
                    boolean aux=false;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            numeroVerifica = ds.getValue().toString();//child("nrTelemovel")

                            Log.d("################3", numeroVerifica);
                            //Atraves das linhas de baixo ficamos com o id da instituicao
                            String substr=numeroVerifica.substring(numeroVerifica.indexOf(" idInstituicao=") + 1);
                            String substrr=substr.substring(14,42);
                            Log.d("!?!?!?!?!?!?!", substrr);
                            if(numeroVerifica.contains(number)) {
                                Intent AtivarContaCodigo = new Intent(getApplicationContext(), Ativar_Conta_Codigo.class);
                                AtivarContaCodigo.putExtra("nrTelemovel", number);
                                AtivarContaCodigo.putExtra("idDaInstituicao", substrr);
                                startActivity(AtivarContaCodigo);
                                aux=true;
                                break;
                            }
                        }
                        if(!aux) Toast.makeText(getApplicationContext(),"Não se encontra registado na plataforma!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
                break;
        }
    }

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
