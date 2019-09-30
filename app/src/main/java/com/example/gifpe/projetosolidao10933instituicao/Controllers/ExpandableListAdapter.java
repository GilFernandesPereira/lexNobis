package com.example.gifpe.projetosolidao10933instituicao.Controllers;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gifpe.projetosolidao10933instituicao.Models.Encontro;
import com.example.gifpe.projetosolidao10933instituicao.Models.Participante;
import com.example.gifpe.projetosolidao10933instituicao.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<Participante> _listDataHeader; // Lista de cabeçalhos (Vai ser o nome e as fotos)
    private HashMap<String, List<Encontro>> _listDataChild;// Aqui vai ser a data dos encontros, etc

    public ExpandableListAdapter(Context context, List<Participante> listDataHeader, HashMap<String, List<Encontro>> listChildData) {
        this._context = context;
        _listDataHeader = listDataHeader;
        _listDataChild = listChildData;
    }

    @Override
    public Encontro getChild(int groupPosition, int childPosititon) {
        return _listDataChild.get(_listDataHeader.get(groupPosition).getNif())
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Encontro childText = getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.activity_list_item, null);
        }

        TextView tvData = convertView.findViewById(R.id.tvData);
        TextView tvEstado = convertView.findViewById(R.id.tvEstado);
        ImageView ivEstado = convertView.findViewById(R.id.ivEstado);
        ImageView ivData = convertView.findViewById(R.id.ivData);

        tvData.setText(childText.getData());
        tvEstado.setText(childText.getEstado());
        ivEstado.setImageResource(R.drawable.ic_radio_button_estado);
        ivData.setImageResource(R.drawable.ic_date_calendario);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return _listDataChild.get(_listDataHeader.get(groupPosition).getNif())
                .size();
    }

    @Override
    public Participante getGroup(int groupPosition) {
        return _listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return _listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Participante headerTitle = getGroup(groupPosition);
//        Uri profileImage= (Uri) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.activity_list_group, null);
        }

        TextView tvNomeSenior = convertView.findViewById(R.id.tvNomeSenior);
        final ImageView ivFotoPerfil = convertView.findViewById(R.id.imagemPerfil);
        tvNomeSenior.setTypeface(null, Typeface.BOLD);
        tvNomeSenior.setText(headerTitle.getNome());
        String nif= headerTitle.getNif();
        StorageReference storage = FirebaseStorage.getInstance().getReference().child(nif);
        final View finalConvertView = convertView;
        storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("DOWNLOAD",uri.toString());
                Picasso.with(finalConvertView.getContext()).load(uri.toString()).into(ivFotoPerfil);
            }
        });
//        Picasso.with(convertView.getContext()).load("https://firebasestorage.googleapis.com/v0/b/tese-ipca-solidao-10933.appspot.com/o/"+nif+"?alt=media&token=191dcb26-d014-44d5-b071-718eb8ac4b78").into(ivFotoPerfil);
//        ivFotoPerfil.setImageURI(headerTitle.getUrlImage());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

