package com.example.gifpe.projetosolidao10933instituicao.Fragments;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.example.gifpe.projetosolidao10933instituicao.Controllers.ExpandableListAdapter;
import com.example.gifpe.projetosolidao10933instituicao.Controllers.Participantes;
import com.example.gifpe.projetosolidao10933instituicao.Models.Encontro;
import com.example.gifpe.projetosolidao10933instituicao.Models.Instituicao;
import com.example.gifpe.projetosolidao10933instituicao.Models.Participante;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventosFragment extends Fragment {

    private FirebaseDatabase database;
    private StorageReference mStorageRef;
    private DatabaseReference myRef;
    private DatabaseReference ref;
    private ListView listaEncontros;
    private String mUser, idSenior;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ExpandableListAdapter listAdapter, listSearch;
    ExpandableListView expListView;
    List<Participante> listParticpantes, listPartiSearch;
    HashMap<String, List<Encontro>> listDataChild;
    private ImageView ivFotoPerfil;
    private EditText etSearch;
    private String dataEncontroMArcado;
    final List<String> datasEncontros = new ArrayList<>();
    HashMap<String, List<String>> datasEmQueFoiAgendada = new HashMap<String, List<String>>();
    private int count = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eventos, null);


    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        user = mAuth.getCurrentUser();
        mUser = user.getUid();
        etSearch = view.findViewById(R.id.editText);
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + mUser + "/");
        ivFotoPerfil = (ImageView) view.findViewById(R.id.imagemPerfil);

        // get the listview
        expListView = view.findViewById(R.id.lvListarEncontros);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(), listParticpantes, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
//                Toast.makeText(getActivity(), listParticpantes.get(i).getNif() + ":" + listDataChild.get(listParticpantes.get(i).getNif()).get(i1).getData(), Toast.LENGTH_SHORT).show();

                Fragment fragment = new DetalheEventosFragment();
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Bundle bund = new Bundle();
                Log.d("GIIL", datasEmQueFoiAgendada.toString());
                bund.putString("dataEncontroParticipante", datasEmQueFoiAgendada.get(listParticpantes.get(i).getNif()).get(i1));//datasEncontros.get(i1)
                bund.putString("nomeParticipante", listParticpantes.get(i).getNome());
                bund.putString("nifParticipante", listParticpantes.get(i).getNif());
                bund.putString("dataParticipante", listDataChild.get(listParticpantes.get(i).getNif()).get(i1).getData());
                bund.putString("estadoParticipante", listDataChild.get(listParticpantes.get(i).getNif()).get(i1).getEstado());
                bund.putString("horaParticipante", listDataChild.get(listParticpantes.get(i).getNif()).get(i1).getHora());
                fragment.setArguments(bund);
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

                return true;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().equals("")) {
                    //reset ListView
                    expListView.setAdapter(listAdapter);
                } else {
                    searchITem(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });//Realizar o search

        //region Quando há um novo pedido, envia uma nova notificação
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    //Encontro encontro= child.getValue(Encontro.class);
//                    addNotification();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //endregion
    }

    public void searchITem(String textoSearch) {
        listPartiSearch = new ArrayList<Participante>();
        for (Participante item : listParticpantes) {
            if ((item.getNome().contains(textoSearch))) {
                listPartiSearch.add(item);
            }
        }
        listSearch = new ExpandableListAdapter(getContext(), listPartiSearch, listDataChild);
        listSearch.notifyDataSetChanged();
        expListView.setAdapter(listSearch);

        //teste
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Fragment fragment = new DetalheEventosFragment();
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Bundle bund = new Bundle();
                bund.putString("dataEncontroParticipante", datasEmQueFoiAgendada.get(listParticpantes.get(i).getNif()).get(i1));
                bund.putString("nomeParticipante", listPartiSearch.get(i).getNome());
                bund.putString("nifParticipante", listPartiSearch.get(i).getNif());
                bund.putString("dataParticipante", listDataChild.get(listPartiSearch.get(i).getNif()).get(i1).getData());
                bund.putString("estadoParticipante", listDataChild.get(listPartiSearch.get(i).getNif()).get(i1).getEstado());
                bund.putString("horaParticipante", listDataChild.get(listPartiSearch.get(i).getNif()).get(i1).getHora());
                fragment.setArguments(bund);
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

                return true;
            }
        });
    }

    private void prepareListData() {
        listParticpantes = new ArrayList<Participante>();
        listDataChild = new HashMap<String, List<Encontro>>();

        myRef.addChildEventListener(new ChildEventListener() {
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

                List<Encontro> encontros;
                List<String> encontrosDatasAgendadas;
                for (Participante item : listParticpantes) {
                    encontros = new ArrayList<>();
                    encontrosDatasAgendadas= new ArrayList<>();

                    encontros = BuscarEncontros(item.getNif());
                    encontrosDatasAgendadas= BuscarEncontrosAgendados(item.getNif());

                    datasEmQueFoiAgendada.put(item.getNif(),encontrosDatasAgendadas);


                    listDataChild.put(item.getNif(), encontros); // Header, Child data
                }

                listAdapter.notifyDataSetChanged();
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

    public List<Encontro> BuscarEncontros(final String item) {
        final List<Encontro> listEncontros = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + item);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String s) {
                Encontro encontro = new Encontro();

                encontro.setData(ds.child("data").getValue().toString());
                encontro.setEstado(ds.child("estado").getValue().toString());
                encontro.setHora(ds.child("horas").getValue().toString());

//                dataEncontroMArcado=ds.getKey().toString();//manda  a data em que foi marcado o encontro, não a data do encontro.
                datasEncontros.add(ds.getKey().toString());

                listEncontros.add(encontro);

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

        return listEncontros;
    }

    public List<String> BuscarEncontrosAgendados(final String item) {
        final List<String> listEncontros = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + item);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String s) {

                listEncontros.add(ds.getKey().toString());

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

        return listEncontros;
    }

    public void addNotification() {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "001")
                .setSmallIcon(R.drawable.teamwork)
                .setContentTitle("Pedido de Missão")
                .setContentText("Um dos teus seniores agendou uma missão! Anda ver!")
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);//Mandar para um layout

//        Intent notificationIntent= new Intent(getContext(), EventosFragment.class);
//        PendingIntent contentIntent= PendingIntent.getActivity(getContext(),0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getActivity());
        manager.notify(001, builder.build());
    }
}
