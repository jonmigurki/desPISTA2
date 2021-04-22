package com.example.despistados2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class Modificar extends AppCompatActivity {


    Button btnSacarFoto, btnAtrasModificar, btnGuardarModificar;
    ImageView imageView;
    EditText txtNombre, txtApellidos, txtContrasena;

    Bitmap bitmap;

    String usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuario = extras.getString("usuario");
        }


        btnSacarFoto = (Button) findViewById(R.id.btnSacarFoto);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnAtrasModificar = (Button) findViewById(R.id.btnAtrasModificar);
        btnGuardarModificar = (Button) findViewById(R.id.btnGuardarModificar);
        txtNombre = (EditText) findViewById(R.id.nombreModificar);
        txtApellidos = (EditText) findViewById(R.id.apellidosModificar);
        txtContrasena = (EditText) findViewById(R.id.contrasenaModificar);


        btnSacarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent elIntentFoto= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(elIntentFoto, 1000);

            }
        });


        btnGuardarModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] fototransformada = stream.toByteArray();
                String fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);

                Log.d("LENGTH DE LA FOTO" , String.valueOf(fotoen64.length()));

                Log.d("FOTO EN STRING", fotoen64);


                ConexionBDWebService conexion = new ConexionBDWebService(Modificar.this);
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("usuario", usuario);
                hm.put("imagen", fotoen64);

                conexion.realizarConexion("actualizar", hm);


            }
        });



        btnAtrasModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap laminiatura = (Bitmap) extras.get("data");
            imageView.setImageBitmap(laminiatura);

            bitmap = laminiatura;
        }

    }

}