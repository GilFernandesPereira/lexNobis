package com.example.gifpe.projetosolidao10933.Views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933.Models.Participante;
import com.example.gifpe.projetosolidao10933.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Ativar_conta extends AppCompatActivity implements View.OnClickListener {

    private String number, numeroVerifica;
    private EditText etNrTelemovel;
    private CardView cv;
    private DatabaseReference myRef;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ativar_conta);
        findViewById(R.id.btnContinuarParaCodigo).setOnClickListener(this);
        etNrTelemovel = findViewById(R.id.etNrTelemovel);
        ref = FirebaseDatabase.getInstance().getReference().child("Instituicoes");//.child("/Informações da Empresa")
        //podes apagar depois
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {


                    //Instituicao inst=new Instituicao();
                    //inst.setNome(ds.child("/Informações da Empresa").getValue(Instituicao.class).getNome());
                    numeroVerifica = ds.getValue().toString();//child("nrTelemovel")
                    //nomeInstituicao=inst.getNome();
//                    if(numeroVerifica.contains("913294382")) {
//                        Toast.makeText(getApplicationContext(),"Existe!",Toast.LENGTH_SHORT).show();
//                    }
                    //Log.d("################3", numeroVerifica);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
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
                ref.addValueEventListener(new ValueEventListener() {
                    boolean aux=false;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            numeroVerifica = ds.getValue().toString();//child("nrTelemovel")
                            Log.d("################3", numeroVerifica);

                            //region Descobrir o NIf do Senior
                            String nifTodo=numeroVerifica.substring(numeroVerifica.indexOf(" nif="));
                            String ApenasONif=nifTodo.substring(5,14);
                            Log.d("=!=!=!=!=!", ApenasONif);
                            //endregion
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
                        if(!aux)Toast.makeText(getApplicationContext(),"Não se encontra registado na plataforma!",Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(getApplicationContext(), Menu_Paciente.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
