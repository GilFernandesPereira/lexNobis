package com.example.gifpe.projetosolidao10933instituicao.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933instituicao.Controllers.CardViewItens;
import com.example.gifpe.projetosolidao10933instituicao.Controllers.CardViewItensVoluntario;
import com.example.gifpe.projetosolidao10933instituicao.Controllers.Instituicoes;
import com.example.gifpe.projetosolidao10933instituicao.Controllers.Voluntarios;
import com.example.gifpe.projetosolidao10933instituicao.Models.Encontro;
import com.example.gifpe.projetosolidao10933instituicao.Models.Instituicao;
import com.example.gifpe.projetosolidao10933instituicao.Models.ObjetosListViewVoluntario;
import com.example.gifpe.projetosolidao10933instituicao.Models.Participante;
import com.example.gifpe.projetosolidao10933instituicao.Models.Voluntario;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.example.gifpe.projetosolidao10933instituicao.Views.Login;
import com.example.gifpe.projetosolidao10933instituicao.Views.MenuPrincipal;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ListarVoluntarioDetalhadoFragment extends Fragment {

    private EditText etNomeVoluntario, etNrTelemovelVoluntario, etNifVoluntario, etDataVoluntario, etMoradaVoluntario;
    private DatabaseReference myRef, reff;
    private String mUser;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String valueNif, nomeInstituicao, estadoVoluntario;
    private Button btnEditarVolunt, btnEditarFotoVoluntario, btnDesativarVolunt;
    private Boolean condicao = false;
    private Boolean update;
    private Spinner sp;
    private List<String> listSexo;
    StorageReference storage;
    private ImageView imgVoluntarioEditar;
    private static final int GALLERY_INTENT = 2;
    Uri filePath;
    ArrayAdapter<CharSequence> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listar_voluntario_detalhado, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Declaração de Variavéis
        etNomeVoluntario = view.findViewById(R.id.etNomeVoluntarioEditar);
        etNifVoluntario = view.findViewById(R.id.etNifVoluntarioEditar);
        etNrTelemovelVoluntario = view.findViewById(R.id.etNrTelemovelVoluntarioEditar);
        etDataVoluntario = view.findViewById(R.id.etDataNascimentoVoluntarioEditar);
        etMoradaVoluntario = view.findViewById(R.id.etMoradaVoluntarioEditar);
        imgVoluntarioEditar = view.findViewById(R.id.imgVoluntarioEditar);
        btnEditarVolunt = view.findViewById(R.id.btnEditarVoluntario);
        btnEditarFotoVoluntario = view.findViewById(R.id.btnEditarFotoVoluntario);
        btnDesativarVolunt = view.findViewById(R.id.btnDesativarVoluntario);
        sp = view.findViewById(R.id.spGeneroVoluntarioEditar);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUser = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + mUser + "/");
        //endregion

        valueNif = getArguments().getString("nifVoluntario");

        BuscarInformacoesBD();
        NomeInst();//Vai buscar o nome da inst para depeois ser utilizado no controller

        view.findViewById(R.id.btnEditarVoluntario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!condicao) {
                    etNomeVoluntario.setEnabled(true);
                    //etNifVoluntario.setEnabled(true);
                    etNrTelemovelVoluntario.setEnabled(true);
                    etDataVoluntario.setEnabled(true);
                    etMoradaVoluntario.setEnabled(true);
                    sp.setEnabled(true);
                    adapter = ArrayAdapter.createFromResource(getContext(), R.array.genero, R.layout.support_simple_spinner_dropdown_item);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    sp.setAdapter(adapter);
                    btnEditarVolunt.setText("Atualizar Conta");
                    condicao = true;
                } else {
                    String nif = etNifVoluntario.getText().toString().trim();
                    String nrTelemovel = etNrTelemovelVoluntario.getText().toString().trim();
                    String nome = etNomeVoluntario.getText().toString().trim();
                    String morada = etMoradaVoluntario.getText().toString().trim();
                    String dataNascimento = etDataVoluntario.getText().toString().trim();
                    String itemSelecionadoGenero = sp.getSelectedItem().toString();
                    Voluntario volunt = new Voluntario(nome, nrTelemovel, morada, itemSelecionadoGenero, nif, dataNascimento, mUser);
                    update = new Voluntarios(getContext()).AtualizaPerfil(volunt, filePath, nomeInstituicao, valueNif);

                    if (update) {
                        Toast.makeText(getActivity(), "Atualização Concluída!", Toast.LENGTH_SHORT).show();
                        etNomeVoluntario.setEnabled(false);
                        //etNifVoluntario.setEnabled(false);
                        etNrTelemovelVoluntario.setEnabled(false);
                        etDataVoluntario.setEnabled(false);
                        etMoradaVoluntario.setEnabled(false);
                        sp.setEnabled(false);
                        btnEditarVolunt.setText("Editar Conta");
                        condicao = false;
                    } else {
                        Toast.makeText(getActivity(), "Não foi possível Atualizar!", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        view.findViewById(R.id.btnEditarFotoVoluntario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        view.findViewById(R.id.btnDesativarVoluntario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setMessage("Tem a certeza que pretende desativar este animador?")
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                reff = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + mUser + "/" + nomeInstituicao + "/" + "Voluntários/" + etNifVoluntario.getText().toString() + "/" + "estado");
                                reff.setValue("Desativado").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Fragment fragment = new MenuFragment();
                                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                                        Toast.makeText(getContext(), "Confirmou", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imgVoluntarioEditar.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void BuscarInformacoesBD() {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot voluntariosSnapshot = dataSnapshot.child("Voluntários");
                Iterable<DataSnapshot> voluntariosChildreen = voluntariosSnapshot.getChildren();
                listSexo = new ArrayList<String>();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listSexo);
                Voluntario voluntario;
                for (DataSnapshot nifs : voluntariosChildreen) {
                    voluntario = new Voluntario();
                    if (nifs.getKey().toString().contains(valueNif)) {
                        voluntario.setNome(nifs.child("nome").getValue().toString());
                        voluntario.setNrTelemovel(nifs.child("nrTelemovel").getValue().toString());
                        voluntario.setNif(nifs.child("nif").getValue().toString());
                        voluntario.setMorada(nifs.child("morada").getValue().toString());
                        voluntario.setDataDeNascimento(nifs.child("dataDeNascimento").getValue().toString());
                        voluntario.setSexo(nifs.child("sexo").getValue().toString());
//                        voluntario.setEstado(nifs.child("estado").getValue().toString());
                        listSexo.add(voluntario.getSexo());

                        etNomeVoluntario.setText(voluntario.getNome());
                        etNifVoluntario.setText(voluntario.getNif());
                        etMoradaVoluntario.setText(voluntario.getMorada());
                        etNrTelemovelVoluntario.setText(voluntario.getNrTelemovel());
                        etDataVoluntario.setText(voluntario.getDataDeNascimento());
//                        estadoVoluntario = voluntario.getEstado();//Fazer Estado depois
                        sp.setAdapter(adapter);
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
                Picasso.with(getContext()).load(uri.toString()).into(imgVoluntarioEditar);
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
