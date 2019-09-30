package com.example.gifpe.projetosolidao10933instituicao.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933instituicao.Controllers.CardViewItens;
import com.example.gifpe.projetosolidao10933instituicao.Models.Encontro;
import com.example.gifpe.projetosolidao10933instituicao.Models.Participante;
import com.example.gifpe.projetosolidao10933instituicao.Models.Voluntario;
import com.example.gifpe.projetosolidao10933instituicao.R;
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
import java.util.HashMap;
import java.util.List;

public class DetalheEventosFragment extends Fragment implements View.OnClickListener {

    private FirebaseDatabase database;
    private StorageReference mStorageRef;
    private DatabaseReference myRef, auxRef, stateRef, removeReff, prioridadeReff;
    private DatabaseReference ref, refToken;
    private String mUser, valueDataEncontro, value, nomeVoluntario, valueEstado;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ImageView ivFotoPerfilEncontro;
    private TextView tvData, tvHora, tvEstado, tvNome, tvEditarVoluntario;
    private Spinner spVoluntariosDisponiveis;
    private Button btnConfirmar;
    private Voluntario volunt;
    private Boolean condicao = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detalhe_encontro, null);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivFotoPerfilEncontro = view.findViewById(R.id.fotoPerfilEncontro);
        view.findViewById(R.id.btnConfirmar).setOnClickListener(this);
        view.findViewById(R.id.btnCancelar).setOnClickListener(this);
        tvData = view.findViewById(R.id.tvData);
        tvHora = view.findViewById(R.id.tvHora);
        tvEstado = view.findViewById(R.id.tvEstado);
        tvNome = view.findViewById(R.id.tvNomeDetalhePaciente);
        btnConfirmar = view.findViewById(R.id.btnConfirmar);
        spVoluntariosDisponiveis = view.findViewById(R.id.spVoluntariosDisponiveis);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        user = mAuth.getCurrentUser();
        mUser = user.getUid();
        valueDataEncontro = getArguments().getString("dataEncontroParticipante");//recebe o dia em que o encontro foi marcado, para depois ser armazenado na BD.
        String valueNome = getArguments().getString("nomeParticipante");
        String valueData = getArguments().getString("dataParticipante");
        valueEstado = getArguments().getString("estadoParticipante");
        String valueHora = getArguments().getString("horaParticipante");
        value = getArguments().getString("nifParticipante");
        StorageReference storage = FirebaseStorage.getInstance().getReference().child(value);
        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("DOWNLOAD", uri.toString());
                Picasso.with(view.getContext()).load(uri.toString()).into(ivFotoPerfilEncontro);
            }
        });

        //Distribuir iformações pelas TextViews
        tvData.setText(valueData);
        tvEstado.setText(valueEstado);
        tvHora.setText(valueHora);
        tvNome.setText(valueNome);
        myRef = FirebaseDatabase.getInstance().getReference().child("Instituicoes/" + mUser + "/");//Ligação à BD para buscar todos os voluntarios
        informacaoVoluntarios();

        auxRef = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + value + "/" + valueDataEncontro + "/voluntario");//Ligação a BD para guardar qual o voluntario responsavel pelo encontro

        ref = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + value + "/" + valueDataEncontro + "/nifVoluntario");//Ligação a BD para guardar qual o voluntario responsavel pelo encontro

        refToken = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + value + "/" + valueDataEncontro + "/tokenAnimador");

        stateRef = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + value + "/" + valueDataEncontro + "/estado");//Ligação à BD para alterar o estado. Aberto->Confirmado

        prioridadeReff = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + value + "/" + valueDataEncontro + "/prioridade");

        if (valueEstado.contains("Pendente")) {
            btnConfirmar.setText("Editar Voluntário");
            spVoluntariosDisponiveis.setEnabled(false);
            retrieveVoluntario();
            ClickBotao(view);

        }else  if(valueEstado.contains("Confirmado")){
            btnConfirmar.setVisibility(View.INVISIBLE);
            retrieveVoluntario();
            spVoluntariosDisponiveis.setEnabled(false);
        } else if(valueEstado.contains("Terminado")){
            retrieveVoluntario();
            spVoluntariosDisponiveis.setEnabled(false);
            btnConfirmar.setText("Visualizar Feedback");
            btnConfirmar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    LayoutInflater inflater = DetalheEventosFragment.this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.activity_feedback,null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                    final EditText etFeedback = dialogView.findViewById(R.id.etFeedback);
                    final EditText etFeedbackSenior = dialogView.findViewById(R.id.etFeedbackSenior);
                    final Button btnVoltar = dialogView.findViewById(R.id.btnVoltarAtras);
                    final AlertDialog b = dialog.create();
                    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + value + "/" + valueDataEncontro + "/feedbackAnimador");
                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            etFeedback.setText(dataSnapshot.getValue(String.class));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    DatabaseReference firebasee = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + value + "/" + valueDataEncontro + "/feedbackSenior");
                    firebasee.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            etFeedbackSenior.setText(dataSnapshot.getValue(String.class));
                            if(dataSnapshot.getValue(String.class)==null){
                                etFeedbackSenior.setText("Ainda por responder!");
                            }
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
            });
        }
    }

    private void informacaoVoluntarios() {

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot voluntariosSnapshot = dataSnapshot.child("Voluntários");
                Iterable<DataSnapshot> voluntariosChildreen = voluntariosSnapshot.getChildren();
                List<String> productname = new ArrayList<String>();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, productname);
                Voluntario voluntario;
                for (DataSnapshot nifs : voluntariosChildreen) {
                    voluntario = new Voluntario();
                    voluntario.setNome(nifs.child("nome").getValue().toString());
//                    voluntario.setNif(nifs.getKey().toString());
                    productname.add(voluntario.getNome());

//                    Log.d("NomeVoluntario", nifs.child("nome").getValue().toString());//nifs.getKey().toString()- vais buscar o NIF, ou seja, o pai!!
                }
                spVoluntariosDisponiveis.setAdapter(adapter);
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

    private void retrieveVoluntario() {
        final List<String> listComNomeVoluntario = new ArrayList<String>();
        final ArrayAdapter<String> nomeVoluntEscolhido = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, listComNomeVoluntario);
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + value + "/" + valueDataEncontro + "/voluntario");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nomeVoluntario = dataSnapshot.getValue(String.class);
                listComNomeVoluntario.add(nomeVoluntario);
                //Log.d("VOLUNTARIO", nomeVoluntario);
                spVoluntariosDisponiveis.setAdapter(nomeVoluntEscolhido);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private Boolean ClickBotao(View view) {
        view.findViewById(R.id.btnConfirmar).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!condicao) {
                        informacaoVoluntarios();
                        spVoluntariosDisponiveis.setEnabled(true);
                        btnConfirmar.setText("Confirmar Voluntário");
                        condicao = true;
                    } else {
                        spVoluntariosDisponiveis.setEnabled(false);
                        btnConfirmar.setText("Editar Voluntário");
//                        retrieveVoluntario();
                        if (spVoluntariosDisponiveis.getSelectedItem() == null) {
                            Toast.makeText(getContext(), "É necessário atribuir um voluntário ao encontro!", Toast.LENGTH_SHORT).show();
                            spVoluntariosDisponiveis.requestFocus();
                        }
                        auxRef.setValue(spVoluntariosDisponiveis.getSelectedItem().toString());//Adicona Voluntário ao encontro
                        GuardarNaBDNifVolunt(spVoluntariosDisponiveis.getSelectedItem().toString());
                        stateRef.setValue("Pendente");
                        prioridadeReff.setValue(1);
                        Toast.makeText(getContext(), "Encontro editado com sucesso!", Toast.LENGTH_SHORT).show();
                        condicao = false;
                    }

                }
                return true;
            }
        });
        return false;
    }

    private void GuardarNaBDNifVolunt(final String nomeAnimador) {
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DataSnapshot voluntariosSnapshot = dataSnapshot.child("Voluntários");
                Iterable<DataSnapshot> voluntariosChildreen = voluntariosSnapshot.getChildren();
                Voluntario voluntario;
                for (DataSnapshot nifs : voluntariosChildreen) {
                    if (nifs.child("nome").getValue().toString() == nomeAnimador) {
                        voluntario = new Voluntario();
                        voluntario.setNome(nifs.child("nome").getValue().toString());
                        voluntario.setNif(nifs.child("nif").getValue().toString());


                        if (nifs.child("tokenAnimador").getValue(String.class)!=null){
                            refToken.setValue(nifs.child("tokenAnimador").getValue().toString());//envia token do animador--apagar se quiseres!
                        }else{
                            refToken.setValue("");
                        }

                        ref.setValue(voluntario.getNif());
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
            case R.id.btnConfirmar:
                if (spVoluntariosDisponiveis.getSelectedItem() == null) {
                    Toast.makeText(getContext(), "É necessário atribuir um voluntário ao encontro!", Toast.LENGTH_SHORT).show();
                    spVoluntariosDisponiveis.requestFocus();
                    return;
                }
                auxRef.setValue(spVoluntariosDisponiveis.getSelectedItem().toString());//Adicona Voluntário ao encontro
                stateRef.setValue("Pendente");
                prioridadeReff.setValue(1);
                GuardarNaBDNifVolunt(spVoluntariosDisponiveis.getSelectedItem().toString());
                tvEstado.setText("Pendente");//mandar para a TV estado Confirmado
                Toast.makeText(getContext(), "Encontro confirmado com sucesso!", Toast.LENGTH_SHORT).show();

                //Após Confirmar encontro, bloqueia o spinner
                String estado = tvEstado.getText().toString().trim();
                if (estado == "Pendente") {
                    btnConfirmar.setText("Editar Voluntário");
                    spVoluntariosDisponiveis.setEnabled(false);
                    retrieveVoluntario();
                    ClickBotao(view);
                }
                break;
            case R.id.btnCancelar:
                removeReff=FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + value + "/" + valueDataEncontro);
                removeReff.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Fragment fragment= new MenuFragment();
                        android.support.v4.app.FragmentManager fragmentManager= getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.container,fragment).commit();
                    }
                });
                break;
        }
    }

////    Enviar notificações
//
//    private void sendRegistrationToServer(String token) {
//        Log.d("TOKEN", "sendRegistrationToServer: sending token to server: " + token);
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ProjetoSolidao/" + mUser + "/" + value + "/" + valueDataEncontro + "/token");
//        reference.setValue(token);
//    }
//
//
//    private void initFCM(){
//        String token = FirebaseInstanceId.getInstance().getToken();
//        Log.d("Token Inicial", "initFCM: token: " + token);
//        sendRegistrationToServer(token);
//
//    }
}
