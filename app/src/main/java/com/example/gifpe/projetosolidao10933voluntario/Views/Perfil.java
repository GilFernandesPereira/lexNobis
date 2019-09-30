package com.example.gifpe.projetosolidao10933voluntario.Views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933voluntario.Controllers.Animadores;
import com.example.gifpe.projetosolidao10933voluntario.Models.Animador;
import com.example.gifpe.projetosolidao10933voluntario.Models.Instituicao;
import com.example.gifpe.projetosolidao10933voluntario.R;
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

import static java.security.AccessController.getContext;

public class Perfil extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference myRef, ref;
    private String mUserPhone, nrCompleto, IDFINAL, valueNif, nomeInstituicao;
    private EditText etNome, etNif, etDataNasc, etContacto, etMorada;
    private boolean condicao = false;
    private boolean update = true;
    private Button btnEditarAnimador;
    private Spinner sp;
    private List<String> listSexo;
    StorageReference storage;
    private ImageView imgVoluntarioEditar;
    private static final int GALLERY_INTENT = 2;
    Uri filePath;
    ArrayAdapter<CharSequence> adapter;
    private ImageView ivFotoPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUserPhone = user.getPhoneNumber();
        etNome = findViewById(R.id.etNomeVoluntarioEditar);
        etNif = findViewById(R.id.etNifEditar);
        etDataNasc = findViewById(R.id.etDataNascimentoEditar);
        etMorada = findViewById(R.id.etMoradaEditar);
        etContacto = findViewById(R.id.etNrTelemovelEditar);
        btnEditarAnimador = findViewById(R.id.btnEditarAnimador);
        sp = findViewById(R.id.spGeneroVoluntarioEditar);
        ivFotoPerfil= findViewById(R.id.ivFotoPerfil);

        ref = FirebaseDatabase.getInstance().getReference().child("Instituicoes");
        ref.addValueEventListener(new ValueEventListener() {
            boolean aux = false;
            String numeroVerifica;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    numeroVerifica = ds.getValue().toString();

                    //Vai buscar o id da respetiva instituicao
                    String substr = numeroVerifica.substring(numeroVerifica.indexOf(" idInstituicao=") + 1);
                    String substrr = substr.substring(14, 42);
                    nrCompleto = mUserPhone.substring(4, 13);
                    Log.d("!?!?!?!?!?!?!", nrCompleto);
                    if (numeroVerifica.contains(nrCompleto)) {
                        IDFINAL = substrr;
                        Animador();
                        NomeInst();
                        aux = true;
                        break;
                    }
                }
                if (!aux)
                    Toast.makeText(getApplicationContext(), "Surgiu um problema!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        findViewById(R.id.btnEditarAnimador).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!condicao) {
                    etNome.setEnabled(true);
                    etNif.setEnabled(true);
                    etContacto.setEnabled(true);
                    etDataNasc.setEnabled(true);
                    etMorada.setEnabled(true);
                    sp.setEnabled(true);
                    adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.genero, R.layout.support_simple_spinner_dropdown_item);
                    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    sp.setAdapter(adapter);
                    btnEditarAnimador.setText("Atualizar Perfil");
                    condicao = true;
                } else {
                    String nif = etNif.getText().toString().trim();
                    String nrTelemovel = etContacto.getText().toString().trim();
                    String nome = etNome.getText().toString().trim();
                    String morada = etMorada.getText().toString().trim();
                    String dataNascimento = etDataNasc.getText().toString().trim();
                    String itemSelecionadoGenero = sp.getSelectedItem().toString();
                    Animador animador = new Animador(nome, nrTelemovel, nif, morada, IDFINAL, itemSelecionadoGenero, dataNascimento);
                    update=new Animadores(view.getContext()).AtualizaPerfil(animador,filePath,nomeInstituicao,valueNif);

                    if (update) {
                        Toast.makeText(view.getContext(), "Atualização Concluída!", Toast.LENGTH_SHORT).show();
                        etNome.setEnabled(false);
                        etNif.setEnabled(false);
                        etContacto.setEnabled(false);
                        etDataNasc.setEnabled(false);
                        etMorada.setEnabled(false);
                        sp.setEnabled(false);
                        btnEditarAnimador.setText("Editar Perfil");
                        condicao = false;
                    } else {
                        Toast.makeText(view.getContext(), "Não foi possível Atualizar!", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        findViewById(R.id.btnEditarFoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
    }

    public void Animador() {
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + IDFINAL + "/");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot participantesSnapshot = dataSnapshot.child("Voluntários");
                Iterable<DataSnapshot> partiChildreen = participantesSnapshot.getChildren();
                listSexo = new ArrayList<String>();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listSexo);
                Animador animador;
                for (DataSnapshot ds : partiChildreen) {
                    animador = new Animador();
                    if (ds.getValue().toString().contains(nrCompleto)) {
                        animador.setNome(ds.child("nome").getValue().toString());
                        animador.setNif(ds.child("nif").getValue().toString());
                        animador.setNrTelemovel(ds.child("nrTelemovel").getValue().toString());
                        animador.setDataNascimentos(ds.child("dataDeNascimento").getValue().toString());
                        animador.setSexo(ds.child("sexo").getValue().toString());
                        animador.setMorada(ds.child("morada").getValue().toString());
                        listSexo.add(animador.getSexo());
                        Log.d("##############45", animador.getNif());

                        etNome.setText(animador.getNome());
                        etNif.setText(animador.getNif());
                        etMorada.setText(animador.getMorada());
                        etContacto.setText(animador.getNrTelemovel());
                        etDataNasc.setText(animador.getDataNascimentos());
                        sp.setAdapter(adapter);
                        valueNif=animador.getNif();

                        //Carregar Foto
                        storage = FirebaseStorage.getInstance().getReference().child(valueNif);
                        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("DOWNLOAD", uri.toString());
                                Picasso.with(getApplicationContext()).load(uri.toString()).into(ivFotoPerfil);
                            }
                        });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            filePath = data.getData();
            try{
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),filePath);
                imgVoluntarioEditar.setImageBitmap(bitmap);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String NomeInst(){
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + IDFINAL + "/");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Instituicao inst=new Instituicao();
                    inst.setNome(ds.child("/Informações da Empresa").getValue(Instituicao.class).getNome());
                    nomeInstituicao=inst.getNome();
//                    Log.d("VOLUNTYY", nomeInstituicao);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return nomeInstituicao;
    }
}
