package com.example.gifpe.projetosolidao10933voluntario.Views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933voluntario.Models.Animador;
import com.example.gifpe.projetosolidao10933voluntario.Models.Instituicao;
import com.example.gifpe.projetosolidao10933voluntario.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class Menu_Animador extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef, ref, myReff;
    private String nrCompleto, mUserPhone, IDFINAL, NIFAnimador, nomeInst, moradaAnimador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__animador);
        findViewById(R.id.btnHistoricoMissoes).setOnClickListener(this);
        findViewById(R.id.btnPerfil).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUserPhone=user.getPhoneNumber();

        ref = FirebaseDatabase.getInstance().getReference().child("Instituicoes");
        ref.addValueEventListener(new ValueEventListener() {
            boolean aux=false;
            String numeroVerifica;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    numeroVerifica = ds.getValue().toString();

                    //Vai buscar o id da respetiva instituicao
                    String substr=numeroVerifica.substring(numeroVerifica.indexOf(" idInstituicao=") + 1);
                    String substrr=substr.substring(14,42);
                    nrCompleto=mUserPhone.substring(4,13);
                    Log.d("!?!?!?!?!?!?!", nrCompleto);
                    if(numeroVerifica.contains(nrCompleto)) {
                        IDFINAL=substrr;
                        Animador();
                        BuscarNomeInst();//Envia e cria um token para os animadores.. Estar decomentado.
                        aux=true;
                        break;
                    }
                }
                if(!aux) Toast.makeText(getApplicationContext(),"Surgiu um problema!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public void Animador(){
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + IDFINAL + "/");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot participantesSnapshot = dataSnapshot.child("Voluntários");
                Iterable<DataSnapshot> partiChildreen = participantesSnapshot.getChildren();
                Animador animador;
                for (DataSnapshot ds : partiChildreen) {
                    animador = new Animador();
                    if(ds.getValue().toString().contains(nrCompleto)){
//                        animador.setNome(ds.child("nome").getValue().toString());
                        animador.setNif(ds.child("nif").getValue().toString());
//                        animador.setNrTelemovel(ds.child("nrTelemovel").getValue().toString());
//                        animador.setDataNascimentos(ds.child("dataDeNascimento").getValue().toString());
//                        animador.setidInstituicao(ds.child("idInstituicao").getValue().toString());
                        animador.setMorada(ds.child("morada").getValue().toString());
                        Log.d("##############45", animador.getNif());
                        NIFAnimador=animador.getNif();
                        moradaAnimador=animador.getMorada();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPerfil:
                Intent intent = new Intent(getApplicationContext(), Perfil.class);
                startActivity(intent);
                break;
            case R.id.btnHistoricoMissoes:
                Intent intentt = new Intent(getApplicationContext(), Historico_missoes.class);
                intentt.putExtra("IDFINAL", IDFINAL);
                intentt.putExtra("NIFAnimador", NIFAnimador);
                intentt.putExtra("MoradaAnimador", moradaAnimador);
                startActivity(intentt);
                break;
        }

    }

    public void BuscarNomeInst(){
        myReff = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + IDFINAL + "/");
        myReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Instituicao inst=new Instituicao();
                    inst.setNome(ds.child("/Informações da Empresa").getValue(Instituicao.class).getNome());
//                    inst.setEmail(ds.child("/Informações da Empresa").getValue(Instituicao.class).getEmail());
//                    inst.setPassword(ds.child("/Informações da Empresa").getValue(Instituicao.class).getPassword());
//                    inst.setMorada(ds.child("/Informações da Empresa").getValue(Instituicao.class).getMorada());
//                    inst.setCodigoPostal(ds.child("/Informações da Empresa").getValue(Instituicao.class).getCodigoPostal());

                    nomeInst=inst.getNome();

                }
                initFCM(nomeInst);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //region SaveToken methods

    private void sendRegistrationToServer(String token, String nomeEntidade) {
        Log.d("TOKEN", "sendRegistrationToServer: sending token to server: " + token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + IDFINAL + "/" + nomeEntidade + "/Voluntários/" + NIFAnimador + "/tokenAnimador");
        reference.setValue(token);
    }


    private void initFCM(String nomeInstt){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token Inicial", "initFCM: token: " + token);
        sendRegistrationToServer(token, nomeInstt);

    }

    //endregion
}
