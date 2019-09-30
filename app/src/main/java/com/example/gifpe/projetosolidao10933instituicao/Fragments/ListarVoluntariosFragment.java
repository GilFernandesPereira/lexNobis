package com.example.gifpe.projetosolidao10933instituicao.Fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.gifpe.projetosolidao10933instituicao.Controllers.ExpandableListAdapter;
import com.example.gifpe.projetosolidao10933instituicao.Controllers.ObjetosListViewsVoluntarios;
import com.example.gifpe.projetosolidao10933instituicao.Models.Encontro;
import com.example.gifpe.projetosolidao10933instituicao.Models.ObjetosListViewVoluntario;
import com.example.gifpe.projetosolidao10933instituicao.Models.Participante;
import com.example.gifpe.projetosolidao10933instituicao.Models.Voluntario;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListarVoluntariosFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ListView listaVoluntarios;
    private String mUser;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText editTextvolunt;
    ArrayList<ObjetosListViewVoluntario> objetos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listar_voluntarios,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUser=user.getUid();
        editTextvolunt= view.findViewById(R.id.editTextvolunt);
        listaVoluntarios=view.findViewById(R.id.lvListaVoluntarios);
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + mUser + "/");

        objetos = new ArrayList<ObjetosListViewVoluntario>();
        final ObjetosListViewsVoluntarios adapter = new ObjetosListViewsVoluntarios(getContext(), R.layout.fragment_listar_voluntario_detalhado_itens, objetos);
        listaVoluntarios.setAdapter(adapter);
        listaVoluntarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment fragment= new ListarVoluntarioDetalhadoFragment();//
                android.support.v4.app.FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                Bundle bund = new Bundle();
                bund.putString("nifVoluntario", objetos.get(i).getIvFotoPerfilVolunt());
                fragment.setArguments(bund);
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot voluntariosSnapshot = dataSnapshot.child("Voluntários");
                Iterable<DataSnapshot> participanteChildreen = voluntariosSnapshot.getChildren();
                Voluntario voluntario;
                for (DataSnapshot nifs : participanteChildreen) {
                    final ObjetosListViewVoluntario obj= new ObjetosListViewVoluntario();

                    obj.setNomeVoluntario(nifs.child("nome").getValue().toString());
                    obj.setIvFotoPerfilVolunt(nifs.child("nif").getValue().toString());

                    objetos.add(obj);
                }
                adapter.notifyDataSetChanged();
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

        editTextvolunt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().equals("")){
                    //reset ListView
                    listaVoluntarios.setAdapter(adapter);
                }else{
                    searchITem(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });//Realizar o search
    }

    public  void searchITem (String textoSearch) {
        final ArrayList<ObjetosListViewVoluntario> listVoluntSearch= new ArrayList<ObjetosListViewVoluntario>();
        for (ObjetosListViewVoluntario item : objetos) {
            if ((item.getNomeVoluntario().contains(textoSearch))) {
                listVoluntSearch.add(item);
            }
        }
        final ObjetosListViewsVoluntarios adapterr = new ObjetosListViewsVoluntarios(getContext(), R.layout.fragment_listar_voluntario_detalhado_itens, listVoluntSearch);
        adapterr.notifyDataSetChanged();
        listaVoluntarios.setAdapter(adapterr);

        listaVoluntarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment fragment= new ListarVoluntarioDetalhadoFragment();//
                android.support.v4.app.FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                Bundle bund = new Bundle();
                bund.putString("nifVoluntario", listVoluntSearch.get(i).getIvFotoPerfilVolunt());
                fragment.setArguments(bund);
                fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
            }
        });
    }
}
