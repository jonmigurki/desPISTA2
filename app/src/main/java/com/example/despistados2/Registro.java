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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Locale;

public class Registro extends AppCompatActivity {

    //Actividad que se encarga de crear la ventana de registro

    EditText usuarioR, contrasenaR;
    EditText nombreR, apellidosR;
    Button btnRegistrarse;

    Context context;

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


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //Obtenemos los elementos del layout
        usuarioR = (EditText) findViewById(R.id.txtUsuarioR);
        contrasenaR = (EditText) findViewById(R.id.txtContrasenaR);
        nombreR = (EditText) findViewById(R.id.txtNombre);
        apellidosR = (EditText) findViewById(R.id.txtApellidos);

        btnRegistrarse = (Button) findViewById(R.id.btnRegistrarse);

        context = this.getApplicationContext();


        //Obtenemos el token del dispositivo
  /*      FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    Log.d("ERROR EN FIREBASE", String.valueOf(task.getException()));
                    return;
                }

                token = task.getResult().getToken();
            }
        });
*/


        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String m1 = "";
                String m2 = "";
                if(String.valueOf(getResources().getConfiguration().locale).contains("es")){
                    m1 = "Debes rellenar los cuatro campos";
                    m2 = "Ya existe un usuario con ese nombre. Elige otro nombre";
                }else{
                    m1 = "You must write in the four fields";
                    m2 = "There's already another user with that name. Choose another one";
                }

                    if (usuarioR.getText().toString().equals("") || contrasenaR.getText().toString().equals("")
                    || nombreR.getText().toString().equals("") || apellidosR.getText().toString().equals("")) {

                    Toast.makeText(getApplicationContext(), m1, Toast.LENGTH_SHORT).show();

                } else {

                        ConexionBDWebService conexion = new ConexionBDWebService(Registro.this);
                        HashMap<String, String> hm = new HashMap<String,String>();
                        hm.put("usuario", usuarioR.getText().toString());
                        hm.put("contrasena", contrasenaR.getText().toString());

                        conexion.realizarConexion("comprobacion", hm);


                }
            }
        });



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
                Intent i = new Intent(Registro.this, MainActivity.class);
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



    public void ejecutarResultadoComprobacion(String resultado){

        //Si resultado == 1 --> Toast indicando que ya hay un usuario con ese nombre
        //Si resultado == 0 --> Se registra el usuario

        if(resultado.equals("1")){
            Toast.makeText(Registro.this, "El usuario introducido ya existe. Escribe otro distinto.", Toast.LENGTH_SHORT).show();

        }else if(resultado.equals("0")){
            Toast.makeText(Registro.this, "Se procede al registro", Toast.LENGTH_SHORT).show();

            ConexionBDWebService conexion = new ConexionBDWebService(Registro.this);
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("usuario", usuarioR.getText().toString());
            hm.put("contrasena", contrasenaR.getText().toString());
            hm.put("nombre", nombreR.getText().toString());
            hm.put("apellidos", apellidosR.getText().toString());




            hm.put("token", token);

            conexion.realizarConexion("registro", hm);

        }else{
            Toast.makeText(Registro.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        }

    }

    public void ejecutarResultadoRegistro(String resultado){

        //Si resultado == "OK" --> Se crea nuevo usuario y se lleva a Menu
        //Si resultado == "Error" --> Toast indicando error

        if(resultado.equals("OK")){
            Intent i = new Intent(Registro.this, Menu.class);
            i.putExtra("usuario", usuarioR.getText().toString());
            startActivity(i);
            finish();
        }

        else{
                Toast.makeText(Registro.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }

    }



}