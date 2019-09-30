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

import com.example.gifpe.projetosolidao10933instituicao.Models.ObjetosListViewSenior;
import com.example.gifpe.projetosolidao10933instituicao.Models.ObjetosListViewVoluntario;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ObjetosListViewsSeniores extends ArrayAdapter<ObjetosListViewSenior> {
    Context context;
    int resource;
    StorageReference storage;
    ArrayList<ObjetosListViewSenior> objetosListViewSenior =null;
    public ObjetosListViewsSeniores( Context context, int resource, ArrayList<ObjetosListViewSenior> objetosListViewSenior) {
        super(context, resource, objetosListViewSenior);
        this.context=context;
        this.resource=resource;
        this.objetosListViewSenior=objetosListViewSenior;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ObjetosListViewSenior objetos= objetosListViewSenior.get(position);
        if(convertView==null) { // apenas para instanciar uma vez
            convertView= LayoutInflater.from(context).inflate(R.layout.fragment_listar_seniores_detalhado,parent,false);
        }

        TextView tvNomeSenior=convertView.findViewById(R.id.tvNomeSenior);
        final ImageView ivPerfilSenior =convertView.findViewById(R.id.ivfotoPerfilSenior);

        storage = FirebaseStorage.getInstance().getReference().child(objetos.getIvFotoPerfilSenior());
        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("DOWNLOAD",uri.toString());
                Picasso.with(context).load(uri.toString()).into(ivPerfilSenior);
            }
        });

        tvNomeSenior.setText(objetos.getNomeSenior());

        return convertView;
    }
}
