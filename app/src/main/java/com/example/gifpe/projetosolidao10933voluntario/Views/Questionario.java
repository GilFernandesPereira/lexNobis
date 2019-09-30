package com.example.gifpe.projetosolidao10933voluntario.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933voluntario.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Questionario extends AppCompatActivity {

    RatingBar mRatingBar;
    TextView mRatingScale;
    Button mSendFeedback, btnMissaoNaorealizada;
    DatabaseReference myRef, ref, refNome, refToken, refNif, refPriori;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String nifParticipante, IDFINAL, dataEncontro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionario);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingScale = (TextView) findViewById(R.id.tvRatingScale);
        mSendFeedback = (Button) findViewById(R.id.btnEnviarFeedback);
        btnMissaoNaorealizada=(Button) findViewById(R.id.btnMissaoNaoRealizada);
        nifParticipante = getIntent().getStringExtra("nifAnimador");
        IDFINAL=getIntent().getStringExtra("IDFINAL");
        dataEncontro=getIntent().getStringExtra("dataEncontroMarcado");
        myRef= FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontro + "/feedbackAnimador");
        ref=FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontro + "/estado");
        refNome=FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontro + "/voluntario");
        refToken=FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontro + "/tokenAnimador");
        refNif=FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontro + "/nifVoluntario");
        refPriori=FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontro + "/prioridade");

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        mRatingScale.setText("Muito mal");
                        break;
                    case 2:
                        mRatingScale.setText("Mal");
                        break;
                    case 3:
                        mRatingScale.setText("Bem");
                        break;
                    case 4:
                        mRatingScale.setText("Muito Bem");
                        break;
                    case 5:
                        mRatingScale.setText("Fantástico");
                        break;
                    default:
                        mRatingScale.setText("");
                }
            }
        });

        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRatingScale.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Por favor, diga como correu a missão", Toast.LENGTH_LONG).show();
                } else {
//                    mFeedback.setText("");
                    String respAnimador=mRatingScale.getText().toString();
                    myRef.setValue(respAnimador).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ref.setValue("Terminado");
                            Toast.makeText(getApplicationContext(), "Questionário submetido com sucesso!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Menu_Animador.class);
                            startActivity(intent);
                        }
                    });

                }
            }
        });

        btnMissaoNaorealizada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refPriori.setValue(0);
                ref.setValue("Em aberto").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        refNome.removeValue();
                        refNif.removeValue();
                        refToken.removeValue();
                        Toast.makeText(getApplicationContext(),"Missão Cancelada", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Menu_Animador.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
