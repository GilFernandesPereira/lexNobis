package com.example.gifpe.projetosolidao10933instituicao.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933instituicao.Controllers.Voluntarios;
import com.example.gifpe.projetosolidao10933instituicao.Models.Voluntario;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.app.Activity.RESULT_OK;

public class AddVoluntarioFragment extends Fragment implements View.OnClickListener{

    //Declaração de Variáveis
    EditText etNome,etNif, etDataNascimento,etNrTelemovel,etMorada;
    ImageView imgVoluntario;
    ArrayAdapter<CharSequence> adapterSexo;
    private Spinner spinnerGenero;
    private static final int GALLERY_INTENT=2;
    private String mUserID;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    Uri filePath;
    Boolean addVoluntario;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_voluntario,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        view.findViewById(R.id.btnAddVoluntario).setOnClickListener((View.OnClickListener) this);
        view.findViewById(R.id.btnUploadFotoVoluntario).setOnClickListener((View.OnClickListener) this);
        etNome=view.findViewById(R.id.etNomeVoluntario);
        etNif=view.findViewById(R.id.etNifVoluntario);
        etDataNascimento=view.findViewById(R.id.etDataNascimentoVoluntario);
        etNrTelemovel=view.findViewById(R.id.etNrTelemovelVoluntario);
        etMorada=view.findViewById(R.id.etMoradaVoluntario);
        spinnerGenero=view.findViewById(R.id.spGeneroVoluntario);
        imgVoluntario=view.findViewById(R.id.imgVoluntario);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUserID=user.getUid();

        //Meter dentro do spiner quais os tipos de gêneros
        adapterSexo=ArrayAdapter.createFromResource(getContext(), R.array.genero, R.layout.support_simple_spinner_dropdown_item);
        adapterSexo.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapterSexo);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            filePath = data.getData();
            try{
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filePath);
                imgVoluntario.setImageBitmap(bitmap);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUploadFotoVoluntario:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
                break;
            case R.id.btnAddVoluntario:
                String nome=etNome.getText().toString().trim();
                String nrtelemovel=etNrTelemovel.getText().toString().trim();
                String nif=etNif.getText().toString().trim();
                String morada=etMorada.getText().toString().trim();
                String dataNascimento=etDataNascimento.getText().toString().trim();
                String itemSelecionadoGenero=spinnerGenero.getSelectedItem().toString();

                //Retrições
                if(imgVoluntario==null){
                    Toast.makeText(getContext(), "É necessário Adicionar Fotografia", Toast.LENGTH_SHORT).show();
                    imgVoluntario.requestFocus();
                    return;
                }
                if (filePath==null){
                    Toast.makeText(getContext(), "É necessário Adicionar Fotografia", Toast.LENGTH_SHORT).show();
                    imgVoluntario.requestFocus();
                    return;
                }
                if (nome.isEmpty()) {
                    etNome.setError("O Nome é obrigatorio!");
                    etNome.requestFocus();
                    return;
                }
                if (!Patterns.PHONE.matcher(nrtelemovel).matches()){
                    etNrTelemovel.setError("Introduza um nrº de telemóvel válido!");
                    etNrTelemovel.requestFocus();
                    return;
                }
                if (nrtelemovel.isEmpty()){
                    etNrTelemovel.setError("O nrº de telemóvel é obrigatorio!");
                    etNrTelemovel.requestFocus();
                    return;
                }
                if (nif.isEmpty()) {
                    etNif.setError("O NIF é obrigatório!");
                    etNif.requestFocus();
                    return;
                }
                if (dataNascimento.isEmpty()) {
                    etDataNascimento.setError("A data de nascimento é obrigatória!");
                    etDataNascimento.requestFocus();
                    return;
                }
                if (morada.isEmpty()) {
                    etMorada.setError("A morada é obrigatória!");
                    etMorada.requestFocus();
                    return;
                }
                Voluntario voluntario= new Voluntario(nome,nrtelemovel,morada,itemSelecionadoGenero,nif,dataNascimento,mUserID);
                addVoluntario=new Voluntarios(getContext()).AddVoluntario(voluntario,filePath);
                if (addVoluntario=true){
                    Toast.makeText(getContext(), "Adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
