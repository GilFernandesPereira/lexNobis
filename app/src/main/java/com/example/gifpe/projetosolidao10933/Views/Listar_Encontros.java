package com.example.gifpe.projetosolidao10933.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933.Controllers.ObjetosListViews;
import com.example.gifpe.projetosolidao10933.Models.ObjetosListView;
import com.example.gifpe.projetosolidao10933.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Listar_Encontros extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ListView listaEncontros;
    private String idInstituicao, mUser, nifSenior;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String dataQueFoiAgendadaMissao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar__encontros);
        idInstituicao = getIntent().getStringExtra("idDaInstituicao");
        nifSenior = getIntent().getStringExtra("nif");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUser = user.getUid();

        if(idInstituicao==null) {
            Intent intent = new Intent(Listar_Encontros.this, Menu_Paciente.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(),"Desculpe, verifique de novo as suas missões!",Toast.LENGTH_SHORT).show();
        }

        listaEncontros = (ListView) findViewById(R.id.lvListaEncontros);
        myRef = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + idInstituicao + "/" + nifSenior);
//        final ArrayList<String> encontros = new ArrayList<>();
//        final ArrayList<String> estado = new ArrayList<>();
//        final ArrayList<String> horas = new ArrayList<>();

        final ArrayList<ObjetosListView> objetos = new ArrayList<ObjetosListView>();
        final ObjetosListViews adapter = new ObjetosListViews(getApplicationContext(), R.layout.activity_listar__itens__encontros, objetos);
        listaEncontros.setAdapter(adapter);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ObjetosListView obj = new ObjetosListView();

                obj.setData(dataSnapshot.child("data").getValue().toString());
                obj.setEstado(dataSnapshot.child("estado").getValue().toString());
                obj.setHoras(dataSnapshot.child("horas").getValue().toString());
                obj.setDataQueFoiAgendadaMissao(dataSnapshot.getKey().toString());

                //Teste
                if(obj.getEstado().contains("Terminado")){
                    if (dataSnapshot.child("feedbackSenior").getValue()!=null){
                        obj.setVerificaFeedback("");
                    }else{
                        obj.setVerificaFeedback("Questionário disponível");
                    }
                }

                objetos.add(obj);

                adapter.notifyDataSetChanged();//manda atualizar o arraylist
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

        listaEncontros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),objetos.get(i).getDataQueFoiAgendadaMissao(),Toast.LENGTH_SHORT).show();
                if ((objetos.get(i).getEstado().toString().contains("Terminado")) && (objetos.get(i).getVerificaFeedback().toString()!="")) {
                    Intent listar_itens_encontro = new Intent(Listar_Encontros.this, Questionario.class);
                    listar_itens_encontro.putExtra("idDaInstituicao", idInstituicao);
                    listar_itens_encontro.putExtra("nif", nifSenior);
                    listar_itens_encontro.putExtra("dataQueFoiAgendadaMissao", objetos.get(i).getDataQueFoiAgendadaMissao().toString());
                    startActivity(listar_itens_encontro);
                }
            }
        });


    }
}
