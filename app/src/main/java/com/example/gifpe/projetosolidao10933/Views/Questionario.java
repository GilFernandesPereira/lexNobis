package com.example.gifpe.projetosolidao10933.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Questionario extends AppCompatActivity {

    RatingBar mRatingBar;
    TextView mRatingScale;
    Button mSendFeedback;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String nifParticipante, idDaInstituicao, dataQueFoiAgendadaMissao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionario);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingScale = (TextView) findViewById(R.id.tvRatingScale);
        mSendFeedback = (Button) findViewById(R.id.btnEnviarFeedback);
        nifParticipante = getIntent().getStringExtra("nif");
        idDaInstituicao=getIntent().getStringExtra("idDaInstituicao");
        dataQueFoiAgendadaMissao=getIntent().getStringExtra("dataQueFoiAgendadaMissao");
        myRef= FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + idDaInstituicao + "/" + nifParticipante + "/" + dataQueFoiAgendadaMissao + "/feedbackSenior");

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
                    Toast.makeText(getApplicationContext(), "Please fill in feedback text box", Toast.LENGTH_LONG).show();
                } else {
//                    mFeedback.setText("");
                    String respAnimador=mRatingScale.getText().toString();
                    myRef.setValue(respAnimador).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Questionário submetido com sucesso!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), Menu_Paciente.class);
                            startActivity(intent);
                        }
                    });

                }
            }
        });
    }
}
