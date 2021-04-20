package com.example.despistados2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;

public class Menu extends AppCompatActivity {

    //Actividad que se encarga de crear la ventana del menú (cateogrías)

    //Obtenemos los elementos del layout
    TextView usuario, puntos, monedas;
    Button compartir, mensaje;

    Context context;

    String user;

    String token = "";

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


        //Cargamos las categorías
        String[] categorias = cargarCategorias();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Establecemos el contexto de la aplicación
        context = this.getApplicationContext();

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

        mensaje = (Button) findViewById(R.id.btnMensaje);
        mensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            Log.d("ERROR EN FIREBASE", String.valueOf(task.getException()));
                            return;
                        }

                       token = task.getResult().getToken();
                    }
                });


                Log.d("TOKEN", token.toString());
                Log.d("HOLA", "HOLAAAA");
                //Hacemos una conexión al servidor PHP
                ConexionBDWebService conexion = new ConexionBDWebService(Menu.this);
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("token", token);

                conexion.realizarConexion("firebase", hm);

            }
        });

        //Accedemos al ListView y creamos el adaptador que visualizará las categorías cargadas
        ListView lista = (ListView) findViewById(R.id.lista1);
        AdaptadorCategorias eladap= new AdaptadorCategorias(getApplicationContext(),categorias);
        lista.setAdapter(eladap);

        //Obtenemos el usuario que se ha identificado
        String u = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            u = extras.getString("usuario");
        }

        //Hacemos que se visualicen el nombre del usuario, sus puntos y sus monedas en la ventana
        usuario = (TextView) findViewById(R.id.txtIdentificado);
        usuario.setText(u);
        user = u;
        puntos = (TextView) findViewById(R.id.txtPuntos1);
        monedas = (TextView) findViewById(R.id.txtMonedas1);
        mostrarPuntosYMonedas();


        //Cuando el usuario seleccione una categoría, realizaremos un Intent explícito a una nueva ventana
        //para visualizarle los niveles disponibles en esa categoría. Además, necesitaremos pasarle el nombre
        //del usuario para recogerlo después y poder hacer los updates necesarios en la BD y guardar sus puntuaciones
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(Menu.this, Nivel.class);
                i.putExtra("categoria", ((TextView)view.findViewById(R.id.etiqueta)).getText().toString());
                i.putExtra("usuario", user);
                i.putExtra("num_categoria", String.valueOf(position+1));

                startActivity(i);

                finish();

            }
        });

    }



    //Método privado que se encarga de cargar las categorías leyendo el fichero de texto
    private String[] cargarCategorias() {

        String linea;

        InputStream is = this.getResources().openRawResource(R.raw.data_es);

        String idioma = String.valueOf(getResources().getConfiguration().getLocales().get(0));


        if (idioma.contains("en")) {
            is = this.getResources().openRawResource(R.raw.data_en);
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String[] c = null;

        //Leemos la primera línea de data.txt
        try {
            linea = reader.readLine();

            c = linea.split(";");


        } catch (IOException e) {
            e.printStackTrace();
        }


        return c;

    }


    //Método privado que se encarga de mostrar los puntos y las monedas que el usuario tiene (se hace una consulta a la BD)
    private void mostrarPuntosYMonedas() {

        //Hacemos una consulta a la BD

        BD GestorDB = new BD(context, "BD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();

        //Miramos en la BD
        Cursor cursor = bd.rawQuery("SELECT PUNTOS, MONEDAS FROM USUARIOS WHERE USUARIO = '" + user + "'", null);
        if (cursor.moveToFirst()) {
            int p = cursor.getInt(cursor.getColumnIndex("PUNTOS"));
            int m = cursor.getInt(cursor.getColumnIndex("MONEDAS"));

            puntos.setText(String.valueOf(p));
            monedas.setText(String.valueOf(m));
        }
        cursor.close();
        GestorDB.close();

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
                Intent i = new Intent(Menu.this, MainActivity.class);
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




}