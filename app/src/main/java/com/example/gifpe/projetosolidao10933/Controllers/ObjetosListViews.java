package com.example.gifpe.projetosolidao10933.Controllers;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gifpe.projetosolidao10933.Models.ObjetosListView;
import com.example.gifpe.projetosolidao10933.R;

import java.util.ArrayList;

public class ObjetosListViews extends ArrayAdapter<ObjetosListView>{

    Context context;
    int resource;
    ArrayList<ObjetosListView> objetosListView =null;
    public ObjetosListViews( Context context, int resource, ArrayList<ObjetosListView> objetosListView) {
        super(context, resource, objetosListView);
        this.context=context;
        this.resource=resource;
        this.objetosListView=objetosListView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ObjetosListView objetos= objetosListView.get(position);
        if(convertView==null) { // apenas para instanciar uma vez
            convertView= LayoutInflater.from(context).inflate(R.layout.activity_listar__itens__encontros,parent,false);
        }

        TextView tvData=(TextView)convertView.findViewById(R.id.tvData);
        TextView tvHora=(TextView)convertView.findViewById(R.id.tvHora);
        TextView tvEstado=(TextView)convertView.findViewById(R.id.tvEstado);
        TextView tvVerifica=convertView.findViewById(R.id.tvVerifica);
        ImageView ivEstado = (ImageView)convertView.findViewById(R.id.ivEstado);
        ImageView ivData = (ImageView) convertView.findViewById(R.id.ivData);
        ImageView ivHora = (ImageView) convertView.findViewById(R.id.ivHora);

        ivEstado.setImageResource(R.drawable.openicon);
        ivData.setImageResource(R.drawable.dataicon);
        ivHora.setImageResource(R.drawable.relogioicon);

        tvData.setText(objetos.getData());
        tvHora.setText(objetos.getHoras());
        tvEstado.setText(objetos.getEstado());
        tvVerifica.setText(objetos.getVerificaFeedback());
        return convertView;
    }
}
