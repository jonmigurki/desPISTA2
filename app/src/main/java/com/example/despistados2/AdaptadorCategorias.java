package com.example.despistados2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdaptadorCategorias extends BaseAdapter {

    //Clase que se encarga de incluir todas las categorías en el ListView

    private Context contexto;
    private LayoutInflater inflater;
    private String[] categorias;

    public AdaptadorCategorias(Context applicationContext, String[] c) {


        contexto = applicationContext;
        categorias = c;

        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return categorias.length;
    }

    @Override
    public Object getItem(int position) {
        return categorias[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView=inflater.inflate(R.layout.fila_categoria_nivel,null);
        TextView categoria = (TextView) convertView.findViewById(R.id.etiqueta);
        categoria.setText(categorias[position]);

        return convertView;
    }
}
