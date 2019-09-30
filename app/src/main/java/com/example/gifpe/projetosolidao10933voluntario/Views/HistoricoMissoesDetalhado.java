package com.example.gifpe.projetosolidao10933voluntario.Views;

import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933voluntario.Models.Animador;
import com.example.gifpe.projetosolidao10933voluntario.Models.Encontro;
import com.example.gifpe.projetosolidao10933voluntario.Models.ObjetosListView;
import com.example.gifpe.projetosolidao10933voluntario.Models.Participante;
import com.example.gifpe.projetosolidao10933voluntario.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HistoricoMissoesDetalhado extends AppCompatActivity implements View.OnClickListener {

    StorageReference storage;
    private String nifParticipante, IDFINAL, dataEncontro, estadoEncontro, horasEncontro, nifAnimador, dataEncontroMarcado, moradaAnimador, codigoPostal;
    private ImageView ivFotoPerfil;
    private TextView tvNomePaciente, tvData, tvEstado, tvHoras;
    DatabaseReference myRef, reff, refNome,refToken,refNif, refPriori;
    private Button btnConfirmar, btnRecusar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_missoes_detalhado);

        //region Declaração de variaveis
        nifParticipante = getIntent().getStringExtra("Nif");
        IDFINAL=getIntent().getStringExtra("IDFINAL");
        dataEncontro=getIntent().getStringExtra("Data");
        estadoEncontro=getIntent().getStringExtra("Estado");
        horasEncontro=getIntent().getStringExtra("Horas");
        nifAnimador=getIntent().getStringExtra("nifAnimador");
        dataEncontroMarcado=getIntent().getStringExtra("dataEncontroMarcado");
        ivFotoPerfil = findViewById(R.id.ivFotoPerfilPaciente);
        tvNomePaciente=findViewById(R.id.tvNomePaciente);
        tvData=findViewById(R.id.tvData);
        tvEstado=findViewById(R.id.tvEstado);
        tvHoras=findViewById(R.id.tvHoras);
        btnConfirmar=findViewById(R.id.btnAceitarMissao);
        btnRecusar=findViewById(R.id.btnRecusarMissao);
        findViewById(R.id.btnInformacaoPaciente).setOnClickListener(this);
        findViewById(R.id.btnAceitarMissao).setOnClickListener(this);
        findViewById(R.id.btnRecusarMissao).setOnClickListener(this);
        findViewById(R.id.btnVoltar).setOnClickListener(this);
        //endregion

        //Carregar Foto
        storage = FirebaseStorage.getInstance().getReference().child(nifParticipante);
        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("DOWNLOAD", uri.toString());
                Picasso.with(getApplicationContext()).load(uri.toString()).into(ivFotoPerfil);
            }
        });

        //Carregar Data/Estado/Horas
        tvData.setText(dataEncontro);
        tvHoras.setText(horasEncontro);
        tvEstado.setText(estadoEncontro);

        RetrieveNomePaciente();

        if(estadoEncontro.contains("Confirmado")){
            btnRecusar.setVisibility(View.INVISIBLE);
            btnConfirmar.setVisibility(View.INVISIBLE);
        }
        if (estadoEncontro.contains("Confirmado")){
            btnConfirmar.setVisibility(View.VISIBLE);
            btnConfirmar.setText("Responder a Questionário");
            btnConfirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentt = new Intent(getApplicationContext(), Questionario.class);
                    intentt.putExtra("IDFINAL",IDFINAL);
                    intentt.putExtra("nifAnimador", nifParticipante);
                    intentt.putExtra("dataEncontroMarcado", dataEncontroMarcado);
                    startActivity(intentt);
                }
            });
        }
        if (estadoEncontro.contains("Terminado")){
            btnConfirmar.setVisibility(View.INVISIBLE);
            btnRecusar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnInformacaoPaciente:
                RetrieveInformationPaciente();
                break;
            case R.id.btnAceitarMissao:
                reff = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontroMarcado + "/estado");
                refPriori = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontroMarcado + "/prioridade");
                refPriori.setValue(2);
                reff.setValue("Confirmado").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Missão Confirmada com Sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), Menu_Animador.class);
                        startActivity(intent);
                    }
                });
                break;
            case R.id.btnRecusarMissao:
                refNome=FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontroMarcado + "/voluntario");
                refToken=FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontroMarcado + "/tokenAnimador");
                refPriori = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontroMarcado + "/prioridade");
                refPriori.setValue(0);
                refNif=FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontroMarcado + "/nifVoluntario");
                reff = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontroMarcado + "/estado");
                reff.setValue("Em aberto").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        refNome.removeValue();
                        refNif.removeValue();
                        refToken.removeValue();
                        Toast.makeText(getApplicationContext(),"Missão Recusada com Sucesso!", Toast.LENGTH_SHORT).show();
//                        FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nifParticipante + "/" + dataEncontroMarcado + "/nifVoluntario").removeValue();
                        Intent intent = new Intent(getApplicationContext(), Menu_Animador.class);
                        startActivity(intent);
                    }
                });
                break;
            case R.id.btnVoltar:
                Intent Voltar= new Intent(getApplicationContext(), Menu_Animador.class);
                startActivity(Voltar);
                break;
        }
    }

    private void RetrieveInformationPaciente() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = HistoricoMissoesDetalhado.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.activity_informacoes_participante,null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);

        final EditText etNomeParticipante = dialogView.findViewById(R.id.etNomeParticipanteEditar);
        final EditText etNrtelemovelParticipante = dialogView.findViewById(R.id.etNrTelemovelParticipanteEditar);
        final EditText etDataNascimentoParticipanteEditar = dialogView.findViewById(R.id.etDataNascimentoParticipanteEditar);
        final EditText etGeneroParticipanteEditar = dialogView.findViewById(R.id.etGeneroParticipanteEditar);
        final EditText etMoradaParticipanteEditar = dialogView.findViewById(R.id.etMoradaParticipanteEditar);
        final EditText etProblemasSaudeParticipanteEditar = dialogView.findViewById(R.id.etProblemasSaudeParticipanteEditar);
        final Button btnVoltar= dialogView.findViewById(R.id.btnVoltarAtras);
        final AlertDialog b = dialog.create();


        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + IDFINAL + "/");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot participantesSnapshot = dataSnapshot.child("Participantes");
                Iterable<DataSnapshot> partiChildreen = participantesSnapshot.getChildren();
                Participante participante;
                for (DataSnapshot ds : partiChildreen) {
                    participante = new Participante();
                    if(ds.getValue().toString().contains(nifParticipante)) {
                        participante.setNome(ds.child("nome").getValue().toString());
                        participante.setNrTelemovel(ds.child("contacto").getValue().toString());
                        participante.setDataNascimentos(ds.child("dataDeNascimento").getValue().toString());
                        participante.setSexo(ds.child("sexo").getValue().toString());
                        participante.setMorada(ds.child("morada").getValue().toString());
                        participante.setDoenca(ds.child("doenca").getValue().toString());

                        etNomeParticipante.setText(participante.getNome());
                        etNrtelemovelParticipante.setText(participante.getNrTelemovel());
                        etDataNascimentoParticipanteEditar.setText(participante.getDataNascimentos());
                        etGeneroParticipanteEditar.setText(participante.getSexo());
                        etMoradaParticipanteEditar.setText(participante.getMorada());
                        etProblemasSaudeParticipanteEditar.setText(participante.getDoenca());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.dismiss();
            }
        });

        b.show();
    }

    private void RetrieveNomePaciente(){
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + IDFINAL + "/");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot participantesSnapshot = dataSnapshot.child("Participantes");
                Iterable<DataSnapshot> partiChildreen = participantesSnapshot.getChildren();
                Participante participante;
                for (DataSnapshot ds : partiChildreen) {
                    participante = new Participante();
                    if(ds.getValue().toString().contains(nifParticipante)) {
                        participante.setNome(ds.child("nome").getValue().toString());
                        participante.setMorada(ds.child("morada").getValue().toString());
                        participante.setCodigoPostal(ds.child("codigoPostal").getValue().toString());

                        tvNomePaciente.setText(participante.getNome());
                        moradaAnimador=participante.getMorada();
                        codigoPostal=participante.getCodigoPostal();

                        Fragment fragment= new fragment_google_maps();
                        android.support.v4.app.FragmentManager fragmentManager= getSupportFragmentManager();
                        Bundle bund = new Bundle();
                        bund.putString("moradaAnimador", moradaAnimador);
                        bund.putString("codigoPostal", codigoPostal);
                        fragment.setArguments(bund);
                        fragmentManager.beginTransaction().replace(R.id.firstLayout,fragment).commit();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
