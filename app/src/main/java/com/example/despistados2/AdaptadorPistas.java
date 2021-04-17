package com.example.despistados2;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdaptadorPistas extends BaseAdapter {

    //Clase que se encarga de incluir todas las pistas en el ListView

    private Context contexto;
    private LayoutInflater inflater;
    private String[] pistas;

    public AdaptadorPistas(Context applicationContext, String[] n) {
        contexto = applicationContext;
        pistas = n;

        inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return pistas.length;
    }

    @Override
    public Object getItem(int position) {
        return pistas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView=inflater.inflate(R.layout.fila_pista,null);
        TextView categoria = (TextView) convertView.findViewById(R.id.etiqueta);
        categoria.setText(pistas[position]);

        //Si la pista de la posición 4 (pista número 5 => Imagen)
        // El texto ("Pulsa aquí para ver la imagen pista") lo ponga en cursiva
        if(position==4){categoria.setTypeface(categoria.getTypeface(), Typeface.ITALIC);}

        return convertView;
    }
}
