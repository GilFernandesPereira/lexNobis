package com.example.gifpe.projetosolidao10933instituicao.Controllers;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gifpe.projetosolidao10933instituicao.Models.ObjetosListViewVoluntario;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ObjetosListViewsVoluntarios extends ArrayAdapter<ObjetosListViewVoluntario> {
    Context context;
    int resource;
    StorageReference storage;
    ArrayList<ObjetosListViewVoluntario> objetosListViewVoluntario =null;
    public ObjetosListViewsVoluntarios( Context context, int resource, ArrayList<ObjetosListViewVoluntario> objetosListViewVoluntario) {
        super(context, resource, objetosListViewVoluntario);
        this.context=context;
        this.resource=resource;
        this.objetosListViewVoluntario=objetosListViewVoluntario;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ObjetosListViewVoluntario objetos= objetosListViewVoluntario.get(position);
        if(convertView==null) { // apenas para instanciar uma vez
            convertView= LayoutInflater.from(context).inflate(R.layout.fragment_listar_voluntario_detalhado_itens,parent,false);
        }

        TextView tvNomeVoluntario=convertView.findViewById(R.id.tvNomeVoluntario);
        final ImageView ivPerfilVoluntario =convertView.findViewById(R.id.ivfotoPerfilEncontro);

        storage = FirebaseStorage.getInstance().getReference().child(objetos.getIvFotoPerfilVolunt());
        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("DOWNLOAD",uri.toString());
                Picasso.with(context).load(uri.toString()).into(ivPerfilVoluntario);
            }
        });

        tvNomeVoluntario.setText(objetos.getNomeVoluntario());

        return convertView;
    }
}
