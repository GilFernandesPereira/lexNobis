package com.example.gifpe.projetosolidao10933instituicao.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gifpe.projetosolidao10933instituicao.Controllers.Participantes;
import com.example.gifpe.projetosolidao10933instituicao.Models.Participante;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.app.Activity.RESULT_OK;

public class AddParticipanteFragment extends Fragment implements View.OnClickListener {

    //region Variaveis
    private Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    private Spinner spinnerEstadoCivil;
    ArrayAdapter<CharSequence> adaptercivil;
    ImageView imgPaciente;
    EditText etNomeParticipante;
    EditText etNumeroTelemovelParticipante;
    EditText etNifParticipante;
    EditText etDataNascimentoParticipante;
    EditText etMoradaParticipante;
    EditText etCodigoPostalParticipante;
    EditText etPesoParticipante;
    EditText etProblemasParticipante;
    String itemSelecionadoGenero;
    String itemSelecionadoEstadoCivil;
    private String mUserID;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private static final int GALLERY_INTENT=2;
    Uri filePath;
    Boolean addpartici;
    FirebaseStorage storage;
    private StorageReference storageReference;
    //endregion

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_participante,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Declaracao Variaveis
        imgPaciente=(ImageView) view.findViewById(R.id.imgPaciente);
        etNomeParticipante=(EditText)view.findViewById(R.id.etNomePacienteParticipante);
        etNumeroTelemovelParticipante=(EditText)view.findViewById(R.id.etNrTelemovelParticipante);
        etNifParticipante=(EditText)view.findViewById(R.id.etNifParticipante);
        etDataNascimentoParticipante=(EditText)view.findViewById(R.id.etDataNascimentoParticipante);
        etMoradaParticipante=(EditText)view.findViewById(R.id.etMoradaParticipante);
        etCodigoPostalParticipante=(EditText)view.findViewById(R.id.etCodigoPostalParticipante);
        etPesoParticipante=(EditText)view.findViewById(R.id.etPesoParticipante);
        etProblemasParticipante=(EditText)view.findViewById(R.id.etSaudeProblemasParticipante);
        view.findViewById(R.id.btnUploadFoto).setOnClickListener((View.OnClickListener) this);
        view.findViewById(R.id.btnAddParticipante).setOnClickListener((View.OnClickListener) this);
        spinner=(Spinner)view.findViewById(R.id.spGeneroParticipante);
        spinnerEstadoCivil=(Spinner)view.findViewById(R.id.spEstadoCivilParticipante);
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mUserID=user.getUid();
        //endregion

        adapter=ArrayAdapter.createFromResource(getContext(), R.array.genero, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        adaptercivil=ArrayAdapter.createFromResource(getContext(), R.array.estadoCivil, R.layout.support_simple_spinner_dropdown_item);
        adaptercivil.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerEstadoCivil.setAdapter(adaptercivil);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            filePath = data.getData();
            try{
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filePath);
                imgPaciente.setImageBitmap(bitmap);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnUploadFoto:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
                break;
            case R.id.btnAddParticipante:

                //region Restrições
                String nome=etNomeParticipante.getText().toString().trim();
                String nrtelemovel=etNumeroTelemovelParticipante.getText().toString().trim();
                String nif=etNifParticipante.getText().toString().trim();
                String morada=etMoradaParticipante.getText().toString().trim();
                String codigoPostal=etCodigoPostalParticipante.getText().toString().trim();
                String dataNascimento=etDataNascimentoParticipante.getText().toString().trim();
                String peso=etPesoParticipante.getText().toString().trim();
                String problemasDeSaude=etProblemasParticipante.getText().toString().trim();
                String estadoCivil=spinnerEstadoCivil.toString().trim();
                String genero=spinner.toString().trim();
                if(imgPaciente==null){
                    Toast.makeText(getContext(), "É necessário adicionar uma fotografia!", Toast.LENGTH_SHORT).show();
                    imgPaciente.requestFocus();
                    return;
                }
                if (filePath==null){
                    Toast.makeText(getContext(), "É necessário adicionar uma fotografia!", Toast.LENGTH_SHORT).show();
                    imgPaciente.requestFocus();
                    return;
                }
                if (nome.isEmpty()) {
                    etNomeParticipante.setError("O Nome é obrigatorio!");
                    etNomeParticipante.requestFocus();
                    return;
                }
                if (!Patterns.PHONE.matcher(nrtelemovel).matches()){
                    etNumeroTelemovelParticipante.setError("Introduza um nrº de telemóvel válido!");
                    etNumeroTelemovelParticipante.requestFocus();
                    return;
                }
                if (nrtelemovel.isEmpty()){
                    etNumeroTelemovelParticipante.setError("O nrº de telemóvel é obrigatorio!");
                    etNumeroTelemovelParticipante.requestFocus();
                    return;
                }
                if (nif.isEmpty()) {
                    etNifParticipante.setError("O NIF é obrigatório!");
                    etNifParticipante.requestFocus();
                    return;
                }
                if (dataNascimento.isEmpty()) {
                    etDataNascimentoParticipante.setError("A data de nascimento é obrigatório!");
                    etDataNascimentoParticipante.requestFocus();
                    return;
                }
                if (morada.isEmpty()) {
                    etMoradaParticipante.setError("A morada é obrigatória!");
                    etMoradaParticipante.requestFocus();
                    return;
                }
                if (codigoPostal.isEmpty()) {
                    etCodigoPostalParticipante.setError("O Código-Postal é obrigatório!");
                    etCodigoPostalParticipante.requestFocus();
                    return;
                }
                if (peso.isEmpty()) {
                    etPesoParticipante.setError("O peso é obrigatória!");
                    etPesoParticipante.requestFocus();
                    return;
                }
                if(problemasDeSaude.isEmpty()){
                    problemasDeSaude="Não tem doenças";
                }
                itemSelecionadoEstadoCivil=spinnerEstadoCivil.getSelectedItem().toString();
                itemSelecionadoGenero=spinner.getSelectedItem().toString();
                //endregion

                Participante participante= new Participante(nome,nrtelemovel,morada,codigoPostal,itemSelecionadoGenero,peso,itemSelecionadoEstadoCivil,nif,dataNascimento,problemasDeSaude,mUserID);
                addpartici=new Participantes(getContext()).AddParticipante(participante,filePath);
                if (addpartici==true){
                    // enviarVerificacao(nrtelemovel);
//                    uploadImage();
                    Toast.makeText(getContext(),"Participante criado com sucesso!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
