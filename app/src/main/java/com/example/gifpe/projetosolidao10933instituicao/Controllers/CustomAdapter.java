package com.example.gifpe.projetosolidao10933instituicao.Controllers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gifpe.projetosolidao10933instituicao.Models.ViewHolder;
import com.example.gifpe.projetosolidao10933instituicao.R;

public class CustomAdapter extends ArrayAdapter<String> {
    ArrayAdapter<CharSequence> spinnerTitles;
    Drawable ivgenero;
    Context mContext;
    public CustomAdapter(@NonNull Context context,ArrayAdapter<CharSequence> titles, Drawable genero) {
        super(context, R.layout.custom_spinner_row);
        this.spinnerTitles = titles;
        this.ivgenero=genero;
        this.mContext=context;
    }

//    @Override
//    public int getCount() {
//        return spinnerTitles.getCount();
//    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.custom_spinner_row, parent, false);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.tvGeneros);
            mViewHolder.ivGenero= (ImageView) convertView.findViewById(R.id.ivGenero);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.mName.setText(spinnerTitles.getItem(position));
        mViewHolder.ivGenero.setImageResource(R.drawable.ic_action_genero);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}
