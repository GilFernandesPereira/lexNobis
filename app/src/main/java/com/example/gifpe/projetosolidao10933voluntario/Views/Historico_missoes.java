package com.example.gifpe.projetosolidao10933voluntario.Views;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933voluntario.Controllers.ObjetosListViews;
import com.example.gifpe.projetosolidao10933voluntario.Models.Animador;
import com.example.gifpe.projetosolidao10933voluntario.Models.Encontro;
import com.example.gifpe.projetosolidao10933voluntario.Models.ObjetosListView;
import com.example.gifpe.projetosolidao10933voluntario.Models.Participante;
import com.example.gifpe.projetosolidao10933voluntario.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class Historico_missoes extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef, ref, reff;
    private String nrCompleto, mUserPhone, IDFINAL, NIFAnimador, NIFParticipante;
    private ListView lvEncontros;
    private EditText etProcurarPeloEstado;
    private ArrayList<String> listNifsSeniores;
    ArrayList<ObjetosListView> objetos;
    ObjetosListViews adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_missoes);

        //region Declaração de Variaveis
        listNifsSeniores= new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUserPhone = user.getPhoneNumber();
        lvEncontros = findViewById(R.id.lvHistoricoMissoes);
        IDFINAL = getIntent().getStringExtra("IDFINAL");
        NIFAnimador = getIntent().getStringExtra("NIFAnimador");

        objetos = new ArrayList<ObjetosListView>();
        adapter = new ObjetosListViews(getApplicationContext(), R.layout.activity_historico_missoes_itens, objetos);
        lvEncontros.setAdapter(adapter);
        etProcurarPeloEstado=findViewById(R.id.etProcurarPeloEstado);
        //endregion

        etProcurarPeloEstado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    //reset ListView
                    lvEncontros.setAdapter(adapter);
                }else{
                    searchITem(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }); // Realiza o Search

        reff = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + IDFINAL + "/");
        prepareListData();

        lvEncontros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intentt = new Intent(getApplicationContext(), HistoricoMissoesDetalhado.class);
                intentt.putExtra("Data", objetos.get(i).getData());
                intentt.putExtra("Horas", objetos.get(i).getHoras());
                intentt.putExtra("Estado", objetos.get(i).getEstado());
                intentt.putExtra("Nif", listNifsSeniores.get(i).toString());
                intentt.putExtra("IDFINAL",IDFINAL);
                intentt.putExtra("nifAnimador", NIFAnimador);
                intentt.putExtra("dataEncontroMarcado", objetos.get(i).getDataEncontroMarcado());
                startActivity(intentt);

                Toast.makeText(getApplicationContext(),objetos.get(i).getData(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void searchITem(String textoSearch){
        final ArrayList<ObjetosListView> listSearch= new ArrayList<ObjetosListView>();
        for (ObjetosListView item : objetos) {
            if ((item.getEstado().contains(textoSearch))) {
                listSearch.add(item);
            }
        }
        final ObjetosListViews adapterr = new ObjetosListViews(getApplicationContext(), R.layout.activity_historico_missoes_itens, listSearch);
        adapterr.notifyDataSetChanged();
        lvEncontros.setAdapter(adapterr);

        lvEncontros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intentt = new Intent(getApplicationContext(), HistoricoMissoesDetalhado.class);
                intentt.putExtra("Data", listSearch.get(i).getData());
                intentt.putExtra("Horas", listSearch.get(i).getHoras());
                intentt.putExtra("Estado", listSearch.get(i).getEstado());
                intentt.putExtra("Nif", NIFParticipante);
                intentt.putExtra("IDFINAL",IDFINAL);
                intentt.putExtra("nifAnimador", NIFAnimador);
                intentt.putExtra("dataEncontroMarcado", listSearch.get(i).getDataEncontroMarcado());
                startActivity(intentt);

                Toast.makeText(getApplicationContext(),objetos.get(i).getData(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void prepareListData() {
        final ArrayList<Participante> listParticpantes = new ArrayList<Participante>();
        reff.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot participantesSnapshot = dataSnapshot.child("Participantes");
                Iterable<DataSnapshot> participanteChildreen = participantesSnapshot.getChildren();
                Participante participante;
                for (DataSnapshot nifs : participanteChildreen) {
                    participante = new Participante();
                    participante.setNome(nifs.child("nome").getValue().toString());
                    participante.setNif(nifs.getKey().toString());

                    listParticpantes.add(participante);


                    Log.d("?#?#?#?#", nifs.getKey().toString());//nifs.getKey().toString()- vais buscar o NIF, ou seja, o pai!!
                }

//                List<Encontro> encontros;
                for (Participante item : listParticpantes) {

                    BuscarInformacaoSobreEncontroNaBD(item.getNif());
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

    public void BuscarInformacaoSobreEncontroNaBD(final String nif) {
        myRef = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + IDFINAL + "/" + nif);
        NIFParticipante=nif;
        myRef.addChildEventListener(new ChildEventListener() {
            Encontro encontro;
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ObjetosListView obj = new ObjetosListView();
                encontro = new Encontro();

                encontro.setData(dataSnapshot.child("data").getValue().toString());
                encontro.setHoras(dataSnapshot.child("horas").getValue().toString());
                encontro.setEstado(dataSnapshot.child("estado").getValue().toString());

                if(dataSnapshot.child("nifVoluntario").getValue()==null){
//                    Toast.makeText(getApplicationContext(),"Deu Null",Toast.LENGTH_SHORT).show();
                    Log.d("","");
                }
                else if (dataSnapshot.child("nifVoluntario").getValue().toString().contains(NIFAnimador)) {
//
                    obj.setData(encontro.getData());
                    obj.setEstado(encontro.getEstado());
                    obj.setHoras(encontro.getHoras());
                    obj.setDataEncontroMarcado(dataSnapshot.getKey().toString());
                    listNifsSeniores.add(nif);
                    Log.d("NIDIDIDI", nif);

                    //teste
//                    String data=obj.getData().toString();
//                    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
//                    String substr=mydate.substring(mydate.indexOf(""));
//                    String substrr=substr.substring(0,10);
//                    DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
//                    Date d1 = f.parse(data, new ParsePosition(0));
//                    Date d2 = f.parse(substrr, new ParsePosition(0));
////                    Toast.makeText(getApplicationContext(),data,Toast.LENGTH_SHORT).show();
                    String estad=dataSnapshot.child("estado").getValue().toString();
                    if (estad.contains("Confirmado"))
                    {

                        obj.setTvQuestionarioDisponivel("Questionário Disponível");
                    }

                    objetos.add(obj);

                    adapter.notifyDataSetChanged();
                }

                Log.d("veOqueDa", encontro.getData());
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

    }
}
