package com.example.gifpe.projetosolidao10933voluntario.Controllers;

import com.example.gifpe.projetosolidao10933voluntario.Models.ObjetosListView;
import com.example.gifpe.projetosolidao10933voluntario.R;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ObjetosListViews extends ArrayAdapter<ObjetosListView>{

    Context context;
    int resource;
    ArrayList<ObjetosListView> objetosListView =null;

    public ObjetosListViews( Context context, int resource, ArrayList<ObjetosListView> objectosListView) {
        super(context, resource, objectosListView);

        this.context=context;
        this.objetosListView=objectosListView;
        this.resource=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ObjetosListView objetos= objetosListView.get(position);
        if(convertView==null) { // apenas para instanciar uma vez
            convertView= LayoutInflater.from(context).inflate(R.layout.activity_historico_missoes_itens,parent,false);
        }

        TextView tvData=(TextView)convertView.findViewById(R.id.tvData);
        TextView tvEstado=(TextView)convertView.findViewById(R.id.tvEstado);
        TextView tvQuestionarioDisponivel=(TextView) convertView.findViewById(R.id.tvQuestionarioDisponivel);
        ImageView ivEstado = (ImageView)convertView.findViewById(R.id.ivEstado);
        ImageView ivData = (ImageView) convertView.findViewById(R.id.ivData);

        ivEstado.setImageResource(R.drawable.ic_estado);
        ivData.setImageResource(R.drawable.ic_data);

        tvData.setText(objetos.getData());
        tvEstado.setText(objetos.getEstado());
        tvQuestionarioDisponivel.setText(objetos.getTvQuestionarioDisponivel());

        return convertView;
    }
}
