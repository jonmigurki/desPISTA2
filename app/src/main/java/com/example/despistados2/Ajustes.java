package com.example.despistados2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Ajustes extends AppCompatActivity {

    //Actividad que se encarga de crear la ventana de ajustes

    Button cambiarIdioma, atras;

    Locale local;
    Configuration config;


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

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration,context.getResources().getDisplayMetrics());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        //Obtengo los elementos del layout
        cambiarIdioma = (Button) findViewById(R.id.btnIdioma);
        atras = (Button) findViewById(R.id.btnAtras2);

        //Si el usuario pulsa "Cambiar Idioma" se abre un AlertDialog con los 2 idiomas disponibles
        //Cuando el usuario selecciona un idioma, se inicializa la variable locale a un nuevo Locale con el idioma deseado
        //y actualizamos la actividad

        /*
            Basado en el código extraído de https://www.jc-mouse.net/
            Web: https://www.jc-mouse.net/android/crea-app-multilenguaje-con-android-studio
            Autor: JC Mouse
            Modificado por Jon Miguel para añadir los idiomas deseados
         */

        cambiarIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(Ajustes.this);
                b.setTitle("Cambiar idioma");

                String[] idiomas = {"Español", "English"};
                b.setItems(idiomas, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        switch(which){
                            case 0:
                                //Cuando el usuario pulsa "Español", la variable estática de la clase Idioma.java se cambia a "es"
                                Idioma.locale = "es";
                                finish();
                                startActivity(getIntent());
                                break;
                            case 1:
                                //Cuando el usuario pulsa "English", la variable estática de la clase Idioma.java se cambia a "en"
                                Idioma.locale = "en";
                                finish();
                                startActivity(getIntent());
                                break;

                        }


                    }

                });

                b.show();

            }

        });


        //Si el usuario pulsa el botón "Atrás" se vuelve a la anterior actividad
        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Ajustes.this, MainActivity.class);
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
                //Ajustes.super.onBackPressed();
                Intent i = new Intent(Ajustes.this, MainActivity.class);
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