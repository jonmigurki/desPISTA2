package com.example.despistados2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Actividad que se encarga de crear la ventana principal de la aplicación

    Button entrar;
    Button registrar;
    EditText usuario;
    EditText contrasena;
    Context context;

    Button ajustes;


    String user;



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
        setContentView(R.layout.activity_main);

        entrar = (Button) findViewById(R.id.btnEntrar);
        registrar = (Button) findViewById(R.id.btnRegistrar);
        usuario = (EditText) findViewById(R.id.txtUsuario);
        contrasena = (EditText) findViewById(R.id.txtContrasena);

        context = this.getApplicationContext();

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Recogemos los datos

                String u = usuario.getText().toString();
                String p = contrasena.getText().toString();

                user = u;

                ConexionBDWebService conexion = new ConexionBDWebService(MainActivity.this);
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("usuario", u);
                hm.put("contrasena", p);
                conexion.realizarConexion("identificacion", hm);


            }
        });


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Nuevo formulario para rellenar usuario y contrasena

                Intent i = new Intent(MainActivity.this, Registro.class);
                startActivity(i);
                finish();
            }
        });



        ajustes = (Button) findViewById(R.id.btnAjustes);
        ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Ajustes.class);
                startActivity(i);
                finish();
            }
        });



    }


    /*
            Basado en el código extraído de Stack Overflow
            Pregunta: https://stackoverflow.com/questions/45729852/android-check-if-back-button-was-pressed
            Autor: https://stackoverflow.com/users/4586742/bob
            Modificado por Jon Miguel para adaptar las funcionalidades y mensajes deseados
        */





    public void onBackPressed() {

        String m1 = "";
        String m2 = "";
        String m3 = "";

        if(String.valueOf(getResources().getConfiguration().locale).contains("es")){
            m1 = "Salir";
            m2 = "¿Estás segur@ de que quieres salir?";
            m3 = "Sí";
        }else{
            m1 = "Exit";
            m2 = "Are you sure you want to exit?";
            m3 = "Yes";
        }


        AlertDialog.Builder alertdialog=new AlertDialog.Builder(this);
        alertdialog.setTitle(m1);
        alertdialog.setMessage(m2);
        alertdialog.setPositiveButton(m3, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.onBackPressed();
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


    public void ejecutarResultadoIdentificacion(String resultado){

        if(resultado.equals("1")){
            Intent i = new Intent(MainActivity.this, Menu.class);
            i.putExtra("usuario", user);
            startActivity(i);
            finish();
        }else if(resultado.equals("0")){
            Toast.makeText(MainActivity.this, "El usuario o contraseña son incorrectos", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this, "No hay conexión con el servidor", Toast.LENGTH_SHORT).show();
        }

    }



}