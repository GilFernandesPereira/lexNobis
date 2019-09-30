package com.example.gifpe.projetosolidao10933instituicao.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933instituicao.Controllers.Participantes;
import com.example.gifpe.projetosolidao10933instituicao.Controllers.Voluntarios;
import com.example.gifpe.projetosolidao10933instituicao.Models.Instituicao;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListarSenioresDetalhadoFragment extends Fragment {

    //region Variaveis
    private EditText etNomeSeniorEditar, etNrTelemovelSeniorEditar, etNifSeniorEditar, etDataNascimentoSeniorEditar, etPesoSeniorEditar, etMoradaSeniorEditar, etCodigoPostalSeniorEditar, etProblemasSaudeSeniorEditar;
    private DatabaseReference myRef, reff;
    private String mUser;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String valueNif, nomeInstituicao, estadoSenior;
    private Button btnEditarSenior, btnEditarFotoSenior, btnDesativarSenior;
    private Boolean condicao = false;
    private Boolean update;
    private Spinner spGenero, spEstado;
    private ImageView imgSeniorEditar;
    private List<String> listSexo, listEstado;
    StorageReference storage;
    private static final int GALLERY_INTENT = 2;
    Uri filePath;
    ArrayAdapter<CharSequence> adapter, adapterr;
    //endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listar_seniores_detalhado_itens,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Declaração de Variaveis
        etNomeSeniorEditar=view.findViewById(R.id.etNomeSeniorEditar);
        etNrTelemovelSeniorEditar=view.findViewById(R.id.etNrTelemovelSeniorEditar);
        etNifSeniorEditar=view.findViewById(R.id.etNifSeniorEditar);
        etDataNascimentoSeniorEditar=view.findViewById(R.id.etDataNascimentoSeniorEditar);
        etPesoSeniorEditar=view.findViewById(R.id.etPesoSeniorEditar);
        etMoradaSeniorEditar=view.findViewById(R.id.etMoradaSeniorEditar);
        etCodigoPostalSeniorEditar=view.findViewById(R.id.etCodigoPostalSeniorEditar);
        etProblemasSaudeSeniorEditar=view.findViewById(R.id.etProblemasSaudeSeniorEditar);
        imgSeniorEditar = view.findViewById(R.id.imgSeniorEditar);
        btnEditarSenior = view.findViewById(R.id.btnEditarSenior);
        btnEditarFotoSenior = view.findViewById(R.id.btnEditarFotoSenior);
        btnDesativarSenior = view.findViewById(R.id.btnDesativarSenior);
        spGenero = view.findViewById(R.id.spGeneroSeniorEditar);
        spEstado = view.findViewById(R.id.spEstadoCivilSeniorEditar);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUser = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + mUser + "/");
        //endregion

        valueNif = getArguments().getString("nifSenior");
        BuscarInformacoesBD();
        NomeInst();

        view.findViewById(R.id.btnEditarSenior).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!condicao) {
                    etNomeSeniorEditar.setEnabled(true);
                    //etNifSeniorEditar.setEnabled(true);
                    etNrTelemovelSeniorEditar.setEnabled(true);
                    etDataNascimentoSeniorEditar.setEnabled(true);
                    etMoradaSeniorEditar.setEnabled(true);
                    etPesoSeniorEditar.setEnabled(true);
                    etCodigoPostalSeniorEditar.setEnabled(true);
                    etProblemasSaudeSeniorEditar.setEnabled(true);
                    spGenero.setEnabled(true);
                    adapter = ArrayAdapter.createFromResource(getContext(), R.array.genero, R.layout.support_simple_spinner_dropdown_item);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spGenero.setAdapter(adapter);
                    spEstado.setEnabled(true);
                    adapterr=ArrayAdapter.createFromResource(getContext(), R.array.estadoCivil, R.layout.support_simple_spinner_dropdown_item);
                    adapterr.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spEstado.setAdapter(adapterr);
                    btnEditarSenior.setText("Atualizar Conta");
                    condicao = true;
                } else {
                    String nif = etNifSeniorEditar.getText().toString().trim();
                    String nrTelemovel = etNrTelemovelSeniorEditar.getText().toString().trim();
                    String nome = etNomeSeniorEditar.getText().toString().trim();
                    String morada = etMoradaSeniorEditar.getText().toString().trim();
                    String dataNascimento = etDataNascimentoSeniorEditar.getText().toString().trim();
                    String peso= etPesoSeniorEditar.getText().toString().trim();
                    String codigoPostal= etCodigoPostalSeniorEditar.getText().toString().trim();
                    String problemasSaude=etProblemasSaudeSeniorEditar.getText().toString().trim();
                    String itemSelecionadoGenero = spGenero.getSelectedItem().toString();
                    String itemSelecionadoEstado = spEstado.getSelectedItem().toString();
                    Participante participante = new Participante(nome, nrTelemovel, morada, codigoPostal, itemSelecionadoGenero, peso, itemSelecionadoEstado, nif, dataNascimento, problemasSaude, mUser);
                    update = new Participantes(getContext()).AtualizaPerfil(participante, filePath, nomeInstituicao, valueNif);

                    if (update) {
                        Toast.makeText(getActivity(), "Atualização Concluída!", Toast.LENGTH_SHORT).show();
                        etNomeSeniorEditar.setEnabled(false);
                        //etNifSeniorEditar.setEnabled(false);
                        etNrTelemovelSeniorEditar.setEnabled(false);
                        etDataNascimentoSeniorEditar.setEnabled(false);
                        etMoradaSeniorEditar.setEnabled(false);
                        etPesoSeniorEditar.setEnabled(false);
                        etCodigoPostalSeniorEditar.setEnabled(false);
                        etProblemasSaudeSeniorEditar.setEnabled(false);
                        spGenero.setEnabled(false);
                        spEstado.setEnabled(false);
                        btnEditarSenior.setText("Editar Conta");
                        condicao = false;
                    } else {
                        Toast.makeText(getActivity(), "Não foi possível Atualizar!", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    private void BuscarInformacoesBD() {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot participantesSnapshot = dataSnapshot.child("Participantes");
                Iterable<DataSnapshot> participantesChildreen = participantesSnapshot.getChildren();
                listSexo = new ArrayList<String>();
                listEstado= new ArrayList<String>();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listSexo);
                ArrayAdapter<String> adapterEstado = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listEstado);
                Participante participante;
                for (DataSnapshot nifs : participantesChildreen) {
                    participante = new Participante();
                    if (nifs.getKey().toString().contains(valueNif)) {
                        participante.setNome(nifs.child("nome").getValue().toString());
                        participante.setContacto(nifs.child("contacto").getValue().toString());
                        participante.setNif(nifs.child("nif").getValue().toString());
                        participante.setPeso(nifs.child("peso").getValue().toString());
                        participante.setCodigoPostal(nifs.child("codigoPostal").getValue().toString());
                        participante.setMorada(nifs.child("morada").getValue().toString());
                        participante.setDataDeNascimento(nifs.child("dataDeNascimento").getValue().toString());
                        participante.setSexo(nifs.child("sexo").getValue().toString());
                        participante.setEstadoCivil(nifs.child("estadoCivil").getValue().toString());
                        participante.setDoenca(nifs.child("doenca").getValue().toString());
                        listSexo.add(participante.getSexo());
                        listEstado.add(participante.getEstadoCivil());

                        etNomeSeniorEditar.setText(participante.getNome());
                        etNifSeniorEditar.setText(participante.getNif());
                        etMoradaSeniorEditar.setText(participante.getMorada());
                        etPesoSeniorEditar.setText(participante.getPeso());
                        etProblemasSaudeSeniorEditar.setText(participante.getDoenca());
                        etCodigoPostalSeniorEditar.setText(participante.getCodigoPostal());
                        etNrTelemovelSeniorEditar.setText(participante.getContacto());
                        etDataNascimentoSeniorEditar.setText(participante.getDataDeNascimento());
                        spEstado.setAdapter(adapterEstado);
                        spGenero.setAdapter(adapter);
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

        storage = FirebaseStorage.getInstance().getReference().child(valueNif);
        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("DOWNLOAD", uri.toString());
                Picasso.with(getContext()).load(uri.toString()).into(imgSeniorEditar);
            }
        });
    }

    private String NomeInst() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Instituicao inst = new Instituicao();
                    inst.setNome(ds.child("/Informações da Empresa").getValue(Instituicao.class).getNome());
                    nomeInstituicao = inst.getNome();
                    Log.d("VOLUNTYY", nomeInstituicao);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return nomeInstituicao;
    }
}


