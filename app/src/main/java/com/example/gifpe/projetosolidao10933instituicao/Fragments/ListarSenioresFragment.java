package com.example.gifpe.projetosolidao10933instituicao.Fragments;

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
import android.widget.ListView;

import com.example.gifpe.projetosolidao10933instituicao.Controllers.ObjetosListViewsSeniores;
import com.example.gifpe.projetosolidao10933instituicao.Controllers.ObjetosListViewsVoluntarios;
import com.example.gifpe.projetosolidao10933instituicao.Models.ObjetosListViewSenior;
import com.example.gifpe.projetosolidao10933instituicao.Models.ObjetosListViewVoluntario;
import com.example.gifpe.projetosolidao10933instituicao.Models.Voluntario;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class ListarSenioresFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ListView listaSeniores;
    private String mUser;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText editTextSeniores;
    ArrayList<ObjetosListViewSenior> objetos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listar_seniores, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUser = user.getUid();
        listaSeniores = view.findViewById(R.id.lvListaSeniores);
        editTextSeniores = view.findViewById(R.id.editTextSeniores);
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + mUser + "/");

        objetos = new ArrayList<ObjetosListViewSenior>();
        final ObjetosListViewsSeniores adapter = new ObjetosListViewsSeniores(getContext(), R.layout.fragment_listar_seniores_detalhado, objetos);
        listaSeniores.setAdapter(adapter);
        listaSeniores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment fragment = new ListarSenioresDetalhadoFragment();//
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Bundle bund = new Bundle();
                bund.putString("nifSenior", objetos.get(i).getIvFotoPerfilSenior());
                fragment.setArguments(bund);
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot voluntariosSnapshot = dataSnapshot.child("Participantes");
                Iterable<DataSnapshot> participanteChildreen = voluntariosSnapshot.getChildren();
                Voluntario voluntario;
                for (DataSnapshot nifs : participanteChildreen) {
                    final ObjetosListViewSenior obj = new ObjetosListViewSenior();

                    obj.setNomeSenior(nifs.child("nome").getValue().toString());
                    obj.setIvFotoPerfilSenior(nifs.child("nif").getValue().toString());

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

        editTextSeniores.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().equals("")) {
                    //reset ListView
                    listaSeniores.setAdapter(adapter);
                } else {
                    searchITem(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });//Realizar o search
    }

    private void searchITem(String textoSearch) {
        final ArrayList<ObjetosListViewSenior> listVoluntSearch = new ArrayList<ObjetosListViewSenior>();
        for (ObjetosListViewSenior item : objetos) {
            if ((item.getNomeSenior().contains(textoSearch))) {
                listVoluntSearch.add(item);
            }
        }
        final ObjetosListViewsSeniores adapterr = new ObjetosListViewsSeniores(getContext(), R.layout.fragment_listar_seniores_detalhado_itens, listVoluntSearch);
        adapterr.notifyDataSetChanged();
        listaSeniores.setAdapter(adapterr);

        listaSeniores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Fragment fragment = new ListarSenioresDetalhadoFragment();//
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Bundle bund = new Bundle();
                bund.putString("nifSenior", listVoluntSearch.get(i).getIvFotoPerfilSenior());
                fragment.setArguments(bund);
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            }
        });
    }
}
