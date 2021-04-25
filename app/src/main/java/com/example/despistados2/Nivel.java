package com.example.despistados2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

public class Nivel extends AppCompatActivity {

    //Actividad que se encarga de crear la ventana de los niveles

    //Obtenemos los elementos del layout
    String usuario;
    String categoria;
    String num_categoria;

    TextView usuarioI, puntos, monedas;

    Button atras, compartir;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Antes de que se cargue el layout de la actividad, obtenemos la Localizacion de
        // la aplicación para saber en qué idioma escribir los botones y textos
        Locale nuevaloc = new Locale(Idioma.locale);
        Locale.setDefault(nuevaloc);

        Configuration configuration =
                getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context2 = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration,context2.getResources().getDisplayMetrics());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nivel);

        //Obtenemos las variables de la anterior actividad
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
            categoria = extras.getString("categoria");
            num_categoria = extras.getString("num_categoria");
        }

        //Obtenemos el usuario identificado y la categoría seleccionada
        usuarioI = (TextView) findViewById(R.id.txtIdentificado);
        usuarioI.setText(usuario);


        //Establecemos el contexto de la aplicación
        context = this.getApplicationContext();

        puntos = (TextView) findViewById(R.id.txtPuntos1);
        monedas = (TextView) findViewById(R.id.txtMonedas1);
        mostrarPuntosYMonedas();

        //Cargamos los niveles dada la categoría elegida
        String [] niveles = cargarNiveles(categoria);

        String idioma = String.valueOf(getResources().getConfiguration().getLocales().get(0));
        if(idioma.contains("es")){
            idioma = "español";
        }else{
            idioma = "inglés";
        }


            //Accedemos al ListView y creamos el adaptador que visualizará los niveles cargados (además le pasamos el idioma)
        ListView lista = (ListView) findViewById(R.id.lista2);
        AdaptadorNiveles eladap= new AdaptadorNiveles(getApplicationContext(),niveles,idioma);
        lista.setAdapter(eladap);

        //Cuando el usuario pulse en un nivel concreto, deberá de llevarle a la actividad Adivinanza
        //donde tendrá que adivinar la palabra o frase. Tendré que pasarle el usuario identificado,
        //la categoría y el nivel seleccionado.
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(Nivel.this, Adivinanza.class);
                i.putExtra("usuario", usuario);
                i.putExtra("categoria", categoria);
                i.putExtra("num_categoria", num_categoria);

                //En nivel tendré que guardar el nombre del nivel, no el identificador
                //El View devuelve "Nivel X", necesito solamente sacar la X (el número) -> charAt(6)
                String v = ((TextView)view.findViewById(R.id.etiqueta)).getText().toString();
                String n = Character.toString(v.charAt(6));
                int numero = Integer.valueOf(n);
                i.putExtra("nivel", niveles[numero-1]);
                i.putExtra("num_nivel", String.valueOf(numero));
                i.putExtra("num_niveles", String.valueOf(niveles.length));

                startActivity(i);
               finish();
            }
        });


        compartir = (Button) findViewById(R.id.btnCompartir);
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String texto = "";

                if(String.valueOf(getResources().getConfiguration().locale).contains("es")){
                    texto = "Estoy jugando a desPISTAdos y es súper entretenido. ¡Corre y descárgatelo!";
                }else{
                    texto = "I'm playing desPISTAdos and it's super entertaining. Go and download it!";
                }

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, texto);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp");
                startActivity(intent);

            }
        });


        atras = (Button) findViewById(R.id.btnAtras1);
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(com.example.despistados2.Nivel.this, Menu.class);
                i.putExtra("usuario", usuario);
                startActivity(i);
                finish();
            }
        });

    }


    private String[] cargarNiveles(String categoria) {

        InputStream is = this.getResources().openRawResource(R.raw.data_es);

        String idioma = String.valueOf(getResources().getConfiguration().getLocales().get(0));


        if (idioma.contains("en")) {
            is = this.getResources().openRawResource(R.raw.data_en);
        }


        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String[] niveles = null;

        //Leemos la primera línea de data.txt

        try {
            String linea = reader.readLine();

            boolean nivelEncontrado = false;

            while(!nivelEncontrado){

                linea = reader.readLine();

                if(linea.equals(categoria)){
                    nivelEncontrado=true;
                    String n = reader.readLine();
                    niveles = n.split(";");
                }



            }



        } catch (IOException e) {
            e.printStackTrace();
        }


        return niveles;

    }


    //Método privado que se encarga de mostrar los puntos y las monedas que el usuario tiene (se hace una consulta a la BD)
    private void mostrarPuntosYMonedas() {

        //Hacemos una consulta a la BD

    /*    BD GestorDB = new BD(context, "BD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        //Miramos en la BD
        Cursor cursor = bd.rawQuery("SELECT PUNTOS, MONEDAS FROM USUARIOS WHERE USUARIO = '" + usuario + "'", null);
        if (cursor.moveToFirst()) {
            int p = cursor.getInt(cursor.getColumnIndex("PUNTOS"));
            int m = cursor.getInt(cursor.getColumnIndex("MONEDAS"));

            puntos.setText(String.valueOf(p));
            monedas.setText(String.valueOf(m));
        }
        cursor.close();
        GestorDB.close();*/

        ConexionBDWebService conexion = new ConexionBDWebService(Nivel.this);
        HashMap<String, String> hm = new HashMap<String,String>();
        hm.put("usuario", usuario);

        conexion.realizarConexion("mostrarpuntosmonedas", hm);



    }


    //Método que se encarga de visualizar un Dialog cuando el usuario le da al botón de atrás de su teléfono
    public void onBackPressed() {

        String texto1 = "";
        String texto2 = "";
        String texto3 = "";

        if(String.valueOf(getResources().getConfiguration().locale).contains("es")){
            texto1 = "Salir";
            texto2 = "¿Estás segur@ de que quieres cerrar sesión?";
            texto3 = "Sí";
        }else{
            texto1 = "Exit";
            texto2 = "Are you sure you want to log out?";
            texto3 = "Yes";
        }


        AlertDialog.Builder alertdialog=new AlertDialog.Builder(this);
        alertdialog.setTitle(texto1);
        alertdialog.setMessage(texto2);
        alertdialog.setPositiveButton(texto3, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //    Menu.super.onBackPressed();
                Intent i = new Intent(Nivel.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertdialog.show();

    }


    public void ejecutarResultadoMostradoPuntosMonedas(String resultado){

        try {
            //  JSONArray jsonarray = new JSONArray(resultado);

            JSONObject jsonpm = new JSONObject(resultado);

            String p = jsonpm.get("PUNTOS").toString();
            String m = jsonpm.get("MONEDAS").toString();

            puntos.setText(p);
            monedas.setText(m);


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }


}