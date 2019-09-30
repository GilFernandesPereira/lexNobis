package com.example.gifpe.projetosolidao10933instituicao.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933instituicao.Controllers.CardViewItens;
import com.example.gifpe.projetosolidao10933instituicao.Controllers.Instituicoes;
import com.example.gifpe.projetosolidao10933instituicao.Models.Instituicao;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PerfilFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil,null);
    }
    private EditText etNomeInst;
    private FirebaseAuth mAuth;
    private EditText etEmailEmpresa;
    private EditText etPass;
    private EditText etMorada;
    private EditText etCodigoPostal;
    private FirebaseUser user;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DataSnapshot dataSnapshot;
    private TextView tvEditar;
    private Button btnEditarConta;
    private String mUserID;
    private Boolean condicao=false;
    private Boolean update;
    public TextInputLayout tilPassWord;
    private String senhaEntidade;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUserID=user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Instituicoes").child(mUserID);
        etNomeInst=(EditText)view.findViewById(R.id.etNomeEmpresaEditar);
        etEmailEmpresa=(EditText)view.findViewById(R.id.etEmailEditar);
        //etPass=(EditText)view.findViewById(R.id.etPassEditar);
        etMorada=(EditText)view.findViewById(R.id.etMoradaEditar);
        etCodigoPostal=(EditText)view.findViewById(R.id.etCodigoPostalEditar);
        btnEditarConta=view.findViewById(R.id.btnEditarConta);
//        tilPassWord.findViewById(R.id.tilPass);
        view.findViewById(R.id.btnEditarConta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!condicao) {
                    etNomeInst.setEnabled(false);
                    etEmailEmpresa.setEnabled(true);
                    //etPass.setEnabled(true);
                    etMorada.setEnabled(true);
                    etCodigoPostal.setEnabled(true);
//                    tilPassWord.setPasswordVisibilityToggleEnabled(true);
                    btnEditarConta.setText("Atualizar Conta");
                    condicao=true;
                }else{
                    String email=etEmailEmpresa.getText().toString().trim();
                    //String senha=etPass.getText().toString().trim();
                    String nome=etNomeInst.getText().toString().trim();
                    String morada=etMorada.getText().toString().trim();
                    String codigoPostal=etCodigoPostal.getText().toString().trim();
                    Instituicao inst= new Instituicao(nome,email,senhaEntidade,morada,codigoPostal);
                    update=new Instituicoes(getContext()).AtualizaPerfil(email,senhaEntidade,nome,inst);

                    if(update){
                        Toast.makeText(getActivity(),"Atualização Concluída!", Toast.LENGTH_SHORT).show();
                        etNomeInst.setEnabled(false);
                        etEmailEmpresa.setEnabled(false);
                        //etPass.setEnabled(false);
                        etMorada.setEnabled(false);
                        etCodigoPostal.setEnabled(false);
                        btnEditarConta.setText("Editar Conta");
                        condicao=false;
                    }else{
                        Toast.makeText(getActivity(),"Não foi possível Atualizar!", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ShowData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        view.findViewById(R.id.btnTeste).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getActivity(),"Teste!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }
    private void ShowData(DataSnapshot data){
        for(DataSnapshot ds: data.getChildren()){
            Instituicao inst=new Instituicao();
            inst.setNome(ds.child("/Informações da Empresa").getValue(Instituicao.class).getNome());
            inst.setEmail(ds.child("/Informações da Empresa").getValue(Instituicao.class).getEmail());
            inst.setPassword(ds.child("/Informações da Empresa").getValue(Instituicao.class).getPassword());
            inst.setMorada(ds.child("/Informações da Empresa").getValue(Instituicao.class).getMorada());
            inst.setCodigoPostal(ds.child("/Informações da Empresa").getValue(Instituicao.class).getCodigoPostal());
            etNomeInst.setText(inst.getNome());
            etEmailEmpresa.setText(inst.getEmail());
            senhaEntidade=inst.getPassword();
            etMorada.setText(inst.getMorada());
            etCodigoPostal.setText(inst.getCodigoPostal());
        }
    }

}
